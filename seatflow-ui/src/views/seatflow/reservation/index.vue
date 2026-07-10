<template>
  <div class="app-container booking-page">
    <div class="hero">
      <div>
        <span class="eyebrow">SEATFLOW · 学习空间</span>
        <h2>预约一个安静座位</h2>
        <p>依次选择学习空间与时间，系统会实时校验座位占用。</p>
      </div>
      <el-steps :active="step" simple>
        <el-step title="选择空间" />
        <el-step title="查询座位" />
        <el-step title="确认预约" />
      </el-steps>
    </div>

    <el-card shadow="never" class="filter-card">
      <template #header>
        <div class="card-header">
          <strong>1. 选择空间与时间</strong>
          <span v-if="selectedRoom" class="room-hours">
            开放时间 {{ shortTime(selectedRoom.openTime) }} - {{ shortTime(selectedRoom.closeTime) }}
          </span>
        </div>
      </template>

      <el-form :model="query" label-position="top" class="selector-grid">
        <el-form-item label="校区">
          <el-select v-model="query.campusId" placeholder="请选择校区" @change="campusChanged">
            <el-option v-for="item in campuses" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="query.buildingId" placeholder="请选择楼栋" :disabled="!query.campusId" @change="buildingChanged">
            <el-option v-for="item in buildings" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层">
          <el-select v-model="query.floorId" placeholder="请选择楼层" :disabled="!query.buildingId" @change="floorChanged">
            <el-option v-for="item in floors" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="自习室">
          <el-select v-model="query.roomId" placeholder="请选择自习室" :disabled="!query.floorId" @change="roomChanged">
            <el-option v-for="item in rooms" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="预约日期">
          <el-date-picker
            v-model="reservationDate"
            type="date"
            value-format="YYYY-MM-DD"
            format="YYYY-MM-DD"
            placeholder="请选择日期"
            :disabled="!selectedRoom"
            :disabled-date="disableDate"
            @change="dateChanged"
          />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-select v-model="startMinute" placeholder="请选择开始时间" :disabled="!reservationDate" @change="startChanged">
            <el-option v-for="item in startOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-select v-model="endMinute" placeholder="请选择结束时间" :disabled="startMinute === undefined" @change="resetSeats">
            <el-option v-for="item in endOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-action">
          <el-button type="primary" size="large" :disabled="!canSearch" :loading="loading" @click="loadSeats">
            查询空闲座位
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-loading="loading" class="seat-card" shadow="never">
      <template #header>
        <div class="card-header">
          <strong>2. 选择座位</strong>
          <div v-if="seats.length" class="seat-stats">
            <span><b>{{ availableCount }}</b> 个空闲</span>
            <span>{{ seats.length }} 个座位</span>
          </div>
        </div>
      </template>
      <el-empty v-if="!seats.length" description="完成上方选择并查询，即可看到实时座位图" />
      <template v-else>
        <div class="legend">
          <span><i class="available" />空闲</span>
          <span><i class="reserved" />已预约</span>
          <span><i class="in-use" />使用中</span>
          <span><i class="disabled" />停用</span>
        </div>
        <div class="seat-grid" :style="gridStyle">
          <button
            v-for="seat in seats"
            :key="seat.seatId"
            class="seat"
            :class="[seat.status, { selected: selectedSeatId === seat.seatId }]"
            :disabled="seat.status !== 'available'"
            :aria-label="`${seat.seatNo} ${seatStatusLabel(seat.status)}`"
            :aria-pressed="seat.status === 'available' ? selectedSeatId === seat.seatId : undefined"
            @click="selectedSeatId = seat.seatId"
          >
            {{ seat.seatNo }}
          </button>
        </div>
      </template>
      <div v-if="seats.length" class="confirm-bar">
        <div>
          <small>当前选择</small>
          <strong>{{ selectedSeat ? `${selectedRoom?.name || ''} · ${selectedSeat.seatNo}` : '请在座位图中选择空闲座位' }}</strong>
          <span v-if="timeRange.length">{{ displayTimeRange }}</span>
        </div>
        <el-button type="primary" size="large" :disabled="!selectedSeat" :loading="submitting" @click="submit">
          确认预约
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup name="SeatFlowReservation">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fixMojibake } from '@/utils/text'
import {
  createReservation,
  listReservationBuildings,
  listReservationCampuses,
  listReservationFloors,
  listReservationRooms,
  listReservationSeats
} from '@/api/seatflow/reservation'

const APP_TIME_ZONE = 'Asia/Shanghai'
const SLOT_MINUTES = 30
const DEFAULT_DURATION_MINUTES = 60
const query = reactive({ campusId: undefined, buildingId: undefined, floorId: undefined, roomId: undefined })
const campuses = ref([])
const buildings = ref([])
const floors = ref([])
const rooms = ref([])
const seats = ref([])
const reservationDate = ref('')
const startMinute = ref(undefined)
const endMinute = ref(undefined)
const selectedSeatId = ref()
const loading = ref(false)
const submitting = ref(false)

const selectedRoom = computed(() => rooms.value.find(item => item.id === query.roomId))
const selectedSeat = computed(() => seats.value.find(item => item.seatId === selectedSeatId.value))
const availableCount = computed(() => seats.value.filter(item => item.status === 'available').length)
const roomOpenMinute = computed(() => parseMinute(selectedRoom.value?.openTime))
const roomCloseMinute = computed(() => parseMinute(selectedRoom.value?.closeTime))
const firstStartMinute = computed(() => roundUp(roomOpenMinute.value, SLOT_MINUTES))
const startOptions = computed(() => buildStartOptions())
const endOptions = computed(() => buildTimeOptions((startMinute.value ?? roomOpenMinute.value) + SLOT_MINUTES, roomCloseMinute.value))
const timeRange = computed(() => {
  if (!reservationDate.value || startMinute.value === undefined || endMinute.value === undefined) return []
  return [
    `${reservationDate.value} ${formatMinute(startMinute.value)}:00`,
    `${reservationDate.value} ${formatMinute(endMinute.value)}:00`
  ]
})
const displayTimeRange = computed(() => timeRange.value.length
  ? `${reservationDate.value} ${formatMinute(startMinute.value)} - ${formatMinute(endMinute.value)}`
  : '')
const canSearch = computed(() => query.roomId && timeRange.value.length === 2)
const step = computed(() => selectedSeat.value ? 3 : seats.value.length ? 2 : query.roomId ? 1 : 0)
const gridStyle = computed(() => ({
  gridTemplateColumns: `repeat(${Math.max(...seats.value.map(seat => seat.colNum), 1)}, minmax(58px, 82px))`
}))

const normalizeOption = item => ({ ...item, name: fixMojibake(item.name) })
const normalizeSeat = item => ({ ...item, seatNo: fixMojibake(item.seatNo) })

onMounted(async () => {
  campuses.value = ((await listReservationCampuses()).data || []).map(normalizeOption)
  if (campuses.value.length === 1) {
    query.campusId = campuses.value[0].id
    await campusChanged(query.campusId)
  }
})

async function campusChanged(id) {
  Object.assign(query, { buildingId: undefined, floorId: undefined, roomId: undefined })
  buildings.value = id ? ((await listReservationBuildings(id)).data || []).map(normalizeOption) : []
  floors.value = []
  rooms.value = []
  clearTimeAndSeats()
  if (buildings.value.length === 1) {
    query.buildingId = buildings.value[0].id
    await buildingChanged(query.buildingId)
  }
}

async function buildingChanged(id) {
  Object.assign(query, { floorId: undefined, roomId: undefined })
  floors.value = id ? ((await listReservationFloors(id)).data || []).map(normalizeOption) : []
  rooms.value = []
  clearTimeAndSeats()
  if (floors.value.length === 1) {
    query.floorId = floors.value[0].id
    await floorChanged(query.floorId)
  }
}

async function floorChanged(id) {
  query.roomId = undefined
  rooms.value = id ? ((await listReservationRooms(id)).data || []).map(normalizeOption) : []
  clearTimeAndSeats()
  if (rooms.value.length === 1) {
    query.roomId = rooms.value[0].id
    roomChanged()
  }
}

function roomChanged() {
  clearTimeAndSeats()
  if (selectedRoom.value) setRecommendedTime()
}

function setRecommendedTime() {
  const now = appNowParts()
  const today = `${now.year}-${pad(now.month)}-${pad(now.day)}`
  const nextBoundary = (Math.floor((now.hour * 60 + now.minute) / SLOT_MINUTES) + 1) * SLOT_MINUTES
  const todayStart = Math.max(firstStartMinute.value, nextBoundary)

  if (todayStart + DEFAULT_DURATION_MINUTES <= roomCloseMinute.value) {
    reservationDate.value = today
    startMinute.value = todayStart
  } else {
    reservationDate.value = addDays(today, 1)
    startMinute.value = firstStartMinute.value
  }
  endMinute.value = defaultEndMinute(startMinute.value)
}

function dateChanged() {
  resetSeats()
  const options = buildStartOptions()
  startMinute.value = options[0]?.value
  endMinute.value = defaultEndMinute(startMinute.value)
}

function startChanged() {
  resetSeats()
  endMinute.value = defaultEndMinute(startMinute.value)
}

function buildStartOptions() {
  if (!selectedRoom.value || !reservationDate.value) return []
  let minimum = firstStartMinute.value
  const now = appNowParts()
  const today = `${now.year}-${pad(now.month)}-${pad(now.day)}`
  if (reservationDate.value === today) {
    const nextBoundary = (Math.floor((now.hour * 60 + now.minute) / SLOT_MINUTES) + 1) * SLOT_MINUTES
    minimum = Math.max(minimum, nextBoundary)
  }
  return buildTimeOptions(minimum, roomCloseMinute.value - SLOT_MINUTES)
}

function buildTimeOptions(begin, end) {
  if (!Number.isFinite(begin) || !Number.isFinite(end) || begin > end) return []
  const options = []
  for (let minute = begin; minute <= end; minute += SLOT_MINUTES) {
    options.push({ value: minute, label: formatMinute(minute) })
  }
  return options
}

function defaultEndMinute(begin) {
  if (!Number.isFinite(begin)) return undefined
  const preferred = begin + DEFAULT_DURATION_MINUTES
  if (preferred <= roomCloseMinute.value) return preferred
  const minimum = begin + SLOT_MINUTES
  return minimum <= roomCloseMinute.value ? minimum : undefined
}

function disableDate(date) {
  const now = appNowParts()
  const todayUtc = Date.UTC(now.year, now.month - 1, now.day)
  return Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()) < todayUtc
}

function appNowParts() {
  const parts = new Intl.DateTimeFormat('en-CA', {
    timeZone: APP_TIME_ZONE,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hourCycle: 'h23'
  }).formatToParts(new Date())
  const values = Object.fromEntries(parts.map(part => [part.type, Number(part.value)]))
  return { year: values.year, month: values.month, day: values.day, hour: values.hour, minute: values.minute }
}

function addDays(dateText, days) {
  const [year, month, day] = dateText.split('-').map(Number)
  const value = new Date(Date.UTC(year, month - 1, day + days))
  return `${value.getUTCFullYear()}-${pad(value.getUTCMonth() + 1)}-${pad(value.getUTCDate())}`
}

function parseMinute(value) {
  if (!value) return NaN
  const [hour, minute] = value.split(':').map(Number)
  return hour * 60 + minute
}

function roundUp(value, step) {
  return Math.ceil(value / step) * step
}

function formatMinute(value) {
  return `${pad(Math.floor(value / 60))}:${pad(value % 60)}`
}

function shortTime(value) {
  return value?.slice(0, 5) || '--:--'
}

function pad(value) {
  return String(value).padStart(2, '0')
}

function seatStatusLabel(status) {
  return ({ available: '空闲', reserved: '已预约', in_use: '使用中', disabled: '停用' })[status] || status
}

function clearTimeAndSeats() {
  reservationDate.value = ''
  startMinute.value = undefined
  endMinute.value = undefined
  resetSeats()
}

function resetSeats() {
  seats.value = []
  selectedSeatId.value = undefined
}

async function loadSeats() {
  loading.value = true
  selectedSeatId.value = undefined
  try {
    seats.value = ((await listReservationSeats({
      roomId: query.roomId,
      startTime: timeRange.value[0],
      endTime: timeRange.value[1]
    })).data || []).map(normalizeSeat)
  } finally {
    loading.value = false
  }
}

async function submit() {
  const room = selectedRoom.value
  const seat = selectedSeat.value
  await ElMessageBox.confirm(
    `确认预约 ${room.name} ${seat.seatNo}？预约时间：${displayTimeRange.value}（开放 ${shortTime(room.openTime)}-${shortTime(room.closeTime)}）`,
    '确认预约',
    { type: 'info' }
  )
  submitting.value = true
  try {
    await createReservation({
      roomId: query.roomId,
      seatId: selectedSeatId.value,
      startTime: timeRange.value[0],
      endTime: timeRange.value[1]
    })
    ElMessage.success('预约成功，可在“我的预约”中查看')
    await loadSeats()
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.hero { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; padding: 24px 28px; border-radius: 12px; background: linear-gradient(120deg, #ecf5ff, #f0f9eb); h2 { margin: 6px 0; font-size: 26px; } p { margin: 0; color: var(--el-text-color-secondary); } .eyebrow { color: var(--el-color-primary); font-size: 12px; font-weight: 700; letter-spacing: 1px; } .el-steps { min-width: 460px; background: transparent; } }
.filter-card { margin-bottom: 16px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.room-hours, .seat-stats { color: var(--el-text-color-secondary); font-size: 13px; }
.seat-stats { display: flex; gap: 16px; b { color: var(--el-color-success); } }
.selector-grid { display: grid; grid-template-columns: repeat(4, minmax(130px, 1fr)); gap: 0 16px; }
.selector-grid :deep(.el-select), .selector-grid :deep(.el-date-editor) { width: 100%; }
.search-action { align-self: end; }
.search-action .el-button { width: 100%; }
.legend { display: flex; justify-content: center; gap: 24px; color: var(--el-text-color-secondary); font-size: 13px; }
.legend span { display: flex; align-items: center; gap: 6px; }
.legend i { width: 13px; height: 13px; border-radius: 3px; }
.available { background: #f0f9eb; border: 1px solid #67c23a; }
.reserved { background: #fdf6ec; }
.in-use { background: #ecf5ff; }
.disabled { background: #f4f4f5; }
.seat-grid { display: grid; gap: 12px; justify-content: center; overflow: auto; padding: 28px 8px; }
.seat { height: 52px; border: 1px solid #dcdfe6; border-radius: 7px; background: #fff; cursor: pointer; transition: .18s; }
.seat.available { color: #529b2e; }
.seat.available:hover { transform: translateY(-2px); box-shadow: 0 4px 10px #00000012; }
.seat.reserved { color: #e6a23c; }
.seat.in-use { color: #409eff; }
.seat.disabled { color: #a8abb2; }
.seat.selected { background: #67c23a; color: #fff; box-shadow: 0 0 0 3px #d1edc4; }
.confirm-bar { display: flex; justify-content: space-between; align-items: center; padding-top: 16px; border-top: 1px solid var(--el-border-color-lighter); }
.confirm-bar > div { display: flex; flex-direction: column; gap: 3px; }
.confirm-bar small, .confirm-bar span { color: var(--el-text-color-secondary); }

@media (max-width: 900px) {
  .hero { align-items: flex-start; }
  .hero .el-steps { display: none; }
  .selector-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}

@media (max-width: 600px) {
  .selector-grid { display: block; }
  .confirm-bar { align-items: flex-end; gap: 12px; }
  .confirm-bar span { display: none; }
  .legend { gap: 10px; flex-wrap: wrap; }
}
</style>
