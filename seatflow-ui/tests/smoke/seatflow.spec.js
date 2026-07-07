import { expect, test } from '@playwright/test'
import {
  api,
  insertCheckinReadyReservation,
  insertEndedReservation,
  loginApi,
  loginPage,
  reservationStatus,
  seedBlacklistedStudent,
  seedConflictReservation,
  studentControlState,
  tomorrowRange
} from './helpers.js'

test.describe.serial('SeatFlow 课程项目主链路', () => {
  test('管理员维护空间并查看预约与报表', async ({ page }) => {
    await loginPage(page, 'admin')
    await expect(page.getByRole('heading', { name: '欢迎回来' })).toBeVisible()

    await page.goto('/seatflow/base-info')
    await page.getByRole('button', { name: '新增校区' }).click()
    const dialog = page.getByRole('dialog')
    await dialog.getByRole('textbox', { name: /校区名称/ }).fill('冒烟测试校区')
    await dialog.getByRole('textbox', { name: '地址' }).fill('自动化测试地址')
    await dialog.getByRole('button', { name: /确\s*定/ }).click()
    await expect(page.getByText('冒烟测试校区')).toBeVisible()

    await page.goto('/seatflow/reservation-manage')
    await expect(page.getByRole('heading', { name: '预约管理' })).toBeVisible()
    await expect(page.getByText('学生一').first()).toBeVisible()

    await page.goto('/seatflow/report')
    await expect(page.getByText('预约数')).toBeVisible()
    await expect(page.locator('canvas')).toHaveCount(4)
  })

  test('学生通过页面预约并取消', async ({ page }) => {
    await loginPage(page, 'student01')
    await page.goto('/seatflow/reservation')

    await page.getByText('请选择校区', { exact: true }).click()
    await page.getByRole('option', { name: '主校区' }).click()
    await page.getByText('请选择楼栋', { exact: true }).click()
    await page.getByRole('option', { name: '第一教学楼' }).click()
    await page.getByText('请选择楼层', { exact: true }).click()
    await page.getByRole('option', { name: '二层' }).click()
    await page.getByText('请选择自习室', { exact: true }).click()
    await page.getByRole('option', { name: '教学楼 201 自习室' }).click()
    await page.getByRole('button', { name: '明天 10:00-11:00' }).click()
    await page.getByRole('button', { name: '查询空闲座位' }).click()
    await page.getByRole('button', { name: 'A03 available' }).click()
    await page.getByRole('button', { name: '确认预约' }).click()
    await page.getByRole('button', { name: /确\s*定/ }).click()
    await expect(page.getByText('预约成功，可在“我的预约”中查看')).toBeVisible()

    await page.goto('/seatflow/my-reservation')
    await expect(page.getByText('待签到').first()).toBeVisible()
    await page.getByRole('button', { name: '取消预约' }).click()
    await page.getByRole('button', { name: /确\s*定/ }).click()
    await expect(page.getByText('预约已取消')).toBeVisible()
  })

  test('签到、手动结束与Quartz自动完成', async ({ page }) => {
    test.setTimeout(90_000)
    await insertCheckinReadyReservation()
    await loginPage(page, 'student01')
    await page.goto('/seatflow/control')
    await expect(page.getByText('A04')).toBeVisible()
    await page.getByRole('button', { name: '立即签到' }).click()
    await expect(page.getByText('签到成功')).toBeVisible()

    await page.goto('/seatflow/my-reservation')
    await page.getByRole('button', { name: '结束使用' }).click()
    await page.getByRole('button', { name: /确\s*定/ }).click()
    await expect(page.getByText('本次使用已结束')).toBeVisible()

    const endedId = await insertEndedReservation()
    await expect.poll(() => reservationStatus(endedId), { timeout: 75_000 }).toBe('completed')
  })

  test('冲突、权限、黑名单与管理员解除形成闭环', async ({ page }) => {
    const [startTime, endTime] = await tomorrowRange(14, 15)
    const conflictSeatId = await seedConflictReservation(startTime, endTime)
    const studentToken = await loginApi('student02')

    const conflict = await api('/seatflow/reservation', studentToken, {
      method: 'POST',
      body: JSON.stringify({ roomId: 1, seatId: conflictSeatId, startTime, endTime })
    })
    expect(conflict.body.code).toBe(500)
    expect(conflict.body.msg).toContain('已被预约')

    const forbidden = await api(
      `/seatflow/report/summary?beginTime=${encodeURIComponent(startTime)}&endTime=${encodeURIComponent(endTime)}`,
      studentToken
    )
    expect(forbidden.body.code).toBe(403)

    await seedBlacklistedStudent()
    const blacklistedAttempt = await api('/seatflow/reservation', studentToken, {
      method: 'POST',
      body: JSON.stringify({ roomId: 1, seatId: 9, startTime, endTime })
    })
    expect(blacklistedAttempt.body.msg).toContain('黑名单')

    await loginPage(page, 'admin')
    await page.goto('/seatflow/blacklist')
    await expect(page.getByText('学生二')).toBeVisible()
    await page.getByRole('button', { name: '解除限制' }).click()
    await page.getByRole('button', { name: /确\s*定/ }).click()
    await expect(page.getByText('黑名单已解除')).toBeVisible()
    await expect.poll(studentControlState).toMatchObject({ violationCount: 0, blacklistFlag: 'no' })

    await page.evaluate(() => {
      window.localStorage.clear()
      window.sessionStorage.clear()
    })
    await page.context().clearCookies()
    await page.reload()
    await loginPage(page, 'student02')
    const topNav = page.getByRole('banner').getByRole('navigation')
    await expect(topNav.getByRole('link', { name: '基础信息' })).toHaveCount(0)
    await expect(topNav.getByRole('link', { name: '预约管理' })).toHaveCount(0)
  })
})
