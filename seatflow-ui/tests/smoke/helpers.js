import mysql from 'mysql2/promise'

const backendUrl = process.env.SEATFLOW_SMOKE_BACKEND_URL || 'http://127.0.0.1:18080'
const APP_TIMEZONE_OFFSET_MS = 8 * 60 * 60 * 1000

function formatAppDate(offsetMs = 0) {
  const date = new Date(Date.now() + offsetMs + APP_TIMEZONE_OFFSET_MS)
  const year = date.getUTCFullYear()
  const month = String(date.getUTCMonth() + 1).padStart(2, '0')
  const day = String(date.getUTCDate()).padStart(2, '0')
  const hour = String(date.getUTCHours()).padStart(2, '0')
  const minute = String(date.getUTCMinutes()).padStart(2, '0')
  const second = String(date.getUTCSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

async function findSeatId(connection, roomId, seatNo) {
  const [[seat]] = await connection.execute(
    'select seat_id as seatId from seatflow_seat where room_id = ? and seat_no = ?',
    [roomId, seatNo]
  )
  if (!seat) throw new Error(`座位不存在：${roomId}/${seatNo}`)
  return seat.seatId
}

export async function connectDatabase() {
  return mysql.createConnection({
    host: process.env.SEATFLOW_SMOKE_DB_HOST || '127.0.0.1',
    port: Number(process.env.SEATFLOW_SMOKE_DB_PORT || 3306),
    user: process.env.SEATFLOW_SMOKE_DB_USER || 'root',
    password: process.env.SEATFLOW_SMOKE_DB_PASSWORD || 'password',
    database: process.env.SEATFLOW_SMOKE_DB_NAME || 'seatflow_smoke'
  })
}

export async function loginPage(page, username) {
  await page.goto('/login')
  await page.getByPlaceholder('账号').fill(username)
  await page.getByPlaceholder('密码').fill('admin123')
  await page.getByRole('button', { name: '登 录' }).click()
  await page.waitForURL(url => !url.pathname.includes('/login'))
}

export async function loginApi(username) {
  const response = await fetch(`${backendUrl}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password: 'admin123', code: '', uuid: '' })
  })
  const body = await response.json()
  if (body.code !== 200 || !body.token) throw new Error(`登录失败：${JSON.stringify(body)}`)
  return body.token
}

export async function api(path, token, options = {}) {
  const response = await fetch(`${backendUrl}${path}`, {
    ...options,
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
      ...(options.headers || {})
    }
  })
  return { status: response.status, body: await response.json() }
}

export async function tomorrowRange(beginHour = 10, endHour = 11) {
  const connection = await connectDatabase()
  try {
    const [rows] = await connection.query(
      "select date_format(date_add(current_date, interval 1 day), '%Y-%m-%d') as day"
    )
    return [`${rows[0].day} ${String(beginHour).padStart(2, '0')}:00:00`, `${rows[0].day} ${String(endHour).padStart(2, '0')}:00:00`]
  } finally {
    await connection.end()
  }
}

export async function insertCheckinReadyReservation() {
  const connection = await connectDatabase()
  try {
    const seatId = await findSeatId(connection, 1, 'A04')
    const startTime = formatAppDate(-60 * 60 * 1000)
    const endTime = formatAppDate(24 * 60 * 60 * 1000)
    const checkDeadline = formatAppDate(12 * 60 * 60 * 1000)
    const [result] = await connection.execute(
      `insert into seatflow_reservation
       (user_id, room_id, seat_id, start_time, end_time, check_deadline, status, create_by, create_time)
       values (10, 1, ?, ?, ?, ?, 'pending_checkin', 'smoke', now())`,
      [seatId, startTime, endTime, checkDeadline]
    )
    return result.insertId
  } finally {
    await connection.end()
  }
}

export async function insertEndedReservation() {
  const connection = await connectDatabase()
  try {
    const seatId = await findSeatId(connection, 1, 'A05')
    const startTime = formatAppDate(-24 * 60 * 60 * 1000)
    const endTime = formatAppDate(-2 * 60 * 60 * 1000)
    const checkDeadline = formatAppDate(-23 * 60 * 60 * 1000)
    const [result] = await connection.execute(
      `insert into seatflow_reservation
       (user_id, room_id, seat_id, start_time, end_time, check_deadline, status, create_by, create_time)
       values (11, 1, ?, ?, ?, ?, 'in_use', 'smoke', now() - interval 1 day)`,
      [seatId, startTime, endTime, checkDeadline]
    )
    return result.insertId
  } finally {
    await connection.end()
  }
}

export async function reservationStatus(reservationId) {
  const connection = await connectDatabase()
  try {
    const [rows] = await connection.execute(
      'select status from seatflow_reservation where reservation_id = ?',
      [reservationId]
    )
    return rows[0]?.status
  } finally {
    await connection.end()
  }
}

export async function seedConflictReservation(startTime, endTime) {
  const connection = await connectDatabase()
  try {
    const seatId = await findSeatId(connection, 1, 'A03')
    await connection.execute(
      `insert into seatflow_reservation
       (user_id, room_id, seat_id, start_time, end_time, check_deadline, status, create_by, create_time)
       values (10, 1, ?, ?, ?, date_add(?, interval 15 minute),
               'pending_checkin', 'smoke', now())`,
      [seatId, startTime, endTime, startTime]
    )
    return seatId
  } finally {
    await connection.end()
  }
}

export async function seedBlacklistedStudent() {
  const connection = await connectDatabase()
  try {
    for (const [offset, seatNo] of [[3, 'B02'], [4, 'B03']]) {
      const seatId = await findSeatId(connection, 1, seatNo)
      const [reservation] = await connection.execute(
        `insert into seatflow_reservation
         (user_id, room_id, seat_id, start_time, end_time, check_deadline, status, create_by, create_time)
         values (11, 1, ?, current_date - interval ? day + interval 9 hour,
                 current_date - interval ? day + interval 10 hour,
                 current_date - interval ? day + interval 9 hour + interval 15 minute,
                 'no_show', 'smoke', now())`,
        [seatId, offset, offset, offset]
      )
      await connection.execute(
        `insert into seatflow_violation_record
         (reservation_id, user_id, reason, violation_time, status, create_by, create_time)
         values (?, 11, '预约开始后15分钟内未签到', now(), 'active', 'smoke', now())`,
        [reservation.insertId]
      )
    }
    const [[latest]] = await connection.query(
      'select max(violation_id) as violationId from seatflow_violation_record where user_id = 11'
    )
    await connection.execute(
      "update seatflow_user_profile set violation_count=3, blacklist_flag='yes' where user_id=11"
    )
    await connection.execute(
      `insert into seatflow_blacklist
       (user_id, violation_id, reason, start_time, status, create_by, create_time)
       values (11, ?, '累计爽约满3次', now(), 'active', 'smoke', now())
       on duplicate key update violation_id=values(violation_id), status='active', end_time=null`,
      [latest.violationId]
    )
  } finally {
    await connection.end()
  }
}

export async function studentControlState() {
  const connection = await connectDatabase()
  try {
    const [[profile]] = await connection.query(
      'select violation_count as violationCount, blacklist_flag as blacklistFlag from seatflow_user_profile where user_id=11'
    )
    return profile
  } finally {
    await connection.end()
  }
}
