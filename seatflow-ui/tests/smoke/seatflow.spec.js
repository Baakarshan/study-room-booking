/* global window */
import { expect, test } from '@playwright/test'
import {
  api,
  captureExperiment,
  captureExperimentElement,
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
    const completedSummary = page.locator('.summary-chip').filter({ hasText: '已完成' })
    await expect(completedSummary.locator('strong')).toHaveText('2')

    await page.goto('/seatflow/report')
    await expect(page.getByText('预约数')).toBeVisible()
    await expect(page.locator('canvas')).toHaveCount(4)
  })

  test('学生通过页面预约并取消', async ({ page }) => {
    await loginPage(page, 'student01')
    await page.goto('/seatflow/reservation')
    const campusField = page.locator('.el-form-item').filter({ hasText: '校区' })
    await campusField.locator('.el-select__wrapper').click()
    await page.getByRole('option', { name: '主校区' }).click()
    await expect(page.getByPlaceholder('请选择日期')).not.toHaveValue('')
    const startField = page.locator('.el-form-item').filter({ hasText: '开始时间' })
    const endField = page.locator('.el-form-item').filter({ hasText: '结束时间' })
    await expect(startField).toContainText(/\d{2}:\d{2}/)
    await expect(endField).toContainText(/\d{2}:\d{2}/)
    await captureExperiment(page, '08-reservation-default-time.png')
    await page.setViewportSize({ width: 390, height: 844 })
    await captureExperiment(page, '13-reservation-mobile.png')
    await page.setViewportSize({ width: 1280, height: 720 })

    await page.getByRole('button', { name: '明天' }).click()
    await startField.locator('.el-select__wrapper').click()
    const availableTimes = page.locator('.el-select-dropdown:visible .el-select-dropdown__item')
    await expect(availableTimes).not.toHaveCount(0)
    const timeLabels = await availableTimes.allTextContents()
    expect(timeLabels.every(label => label >= '08:00' && label <= '21:30')).toBe(true)
    await captureExperiment(page, '09-reservation-time-options.png')
    await page.keyboard.press('Escape')

    await page.getByRole('button', { name: '查询空闲座位' }).click()
    await page.getByRole('button', { name: 'A03 空闲' }).click()
    await captureExperiment(page, '10-reservation-seat-selected.png')
    await page.getByRole('button', { name: '确认预约' }).click()
    const confirmDialog = page.getByRole('dialog')
    await expect(confirmDialog).toContainText('预约时间：')
    await expect(confirmDialog).toContainText('开放时间：08:00 - 22:00')
    await captureExperimentElement(confirmDialog, '11-reservation-confirm-dialog.png')
    await confirmDialog.getByRole('button', { name: /确\s*定/ }).click()
    await expect(page.getByText('预约成功，可在“我的预约”中查看')).toBeVisible()

    await page.goto('/seatflow/my-reservation')
    await expect(page.getByText('待签到').first()).toBeVisible()
    const pendingSummary = page.locator('.summary-card').filter({ hasText: '待签到' })
    await expect(pendingSummary.locator('strong')).toHaveText('1')
    await expect(page.locator('.reservation-list .el-loading-mask')).toBeHidden()
    await captureExperiment(page, '12-my-reservation-summary.png')
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
