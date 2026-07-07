<template>
  <div class="app-container sf-page reservation-page">
    <div class="sf-toolbar">
      <div class="sf-filter-group">
        <div class="sf-field">
          <span class="sf-field-label">校区</span>
          <el-select v-model="form.campusId" placeholder="请选择校区" style="width: 180px" @change="handleCampusChange">
            <el-option v-for="item in campuses" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </div>
        <div class="sf-field">
          <span class="sf-field-label">楼栋</span>
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 180px" :disabled="!form.campusId" @change="handleBuildingChange">
            <el-option v-for="item in buildings" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </div>
        <div class="sf-field">
          <span class="sf-field-label">楼层</span>
          <el-select v-model="form.floorId" placeholder="请选择楼层" style="width: 160px" :disabled="!form.buildingId" @change="handleFloorChange">
            <el-option v-for="item in floors" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </div>
        <div class="sf-field">
          <span class="sf-field-label">自习室</span>
          <el-select v-model="form.roomId" placeholder="请选择自习室" style="width: 220px" :disabled="!form.floorId" @change="handleRoomChange">
            <el-option v-for="item in rooms" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </div>
      </div>
    </div>

    <div class="sf-panel time-panel">
      <div class="sf-field">
        <span class="sf-field-label">预约时段</span>
        <el-date-picker
          v-model="timeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          format="YYYY-MM-DD HH:mm"
          :default-time="defaultTime"
          style="width: 390px"
          @change="fetchSeats"
        />
      </div>
      <div class="sf-actions">
        <el-button type="primary" icon="Search" :disabled="!canQuerySeats" @click="fetchSeats">查询座位</el-button>
        <el-button icon="Refresh" @click="resetAll">重置</el-button>
      </div>
    </div>

    <div v-if="selectedRoom" class="room-hint">
      <span class="sf-inline-name">{{ selectedRoom.name }}</span>
      <span class="sf-muted">开放时间 {{ selectedRoom.openTime || '--' }} - {{ selectedRoom.closeTime || '--' }}</span>
    </div>

    <div class="seat-grid-container" v-loading="loading">
      <div class="legend">
        <div class="legend-item"><span class="box available"></span>可预约</div>
        <div class="legend-item"><span class="box selected"></span>已选择</div>
        <div class="legend-item"><span class="box reserved"></span>不可用</div>
      </div>

      <div v-if="seats.length" class="seating-grid" :style="{ gridTemplateColumns: `repeat(${gridColumns}, 1fr)` }">
        <button
          v-for="seat in sortedSeats"
          :key="seat.seatId"
          class="seat-box"
          :class="[seatStatusClass(seat), { selected: selectedSeatId === seat.seatId }]"
          :disabled="!isSeatAvailable(seat)"
          @click="selectedSeatId = seat.seatId"
        >
          <span class="seat-no">{{ seat.seatNo }}</span>
          <span class="seat-coords">{{ seat.rowNum }} 行 {{ seat.colNum }} 列</span>
        </button>
      </div>
      <el-empty v-else description="请选择空间和时间段后查询座位" />
    </div>

    <div class="submit-bar">
      <div>
        <span class="sf-inline-name">{{ selectedSeat ? selectedSeat.seatNo : '未选择座位' }}</span>
        <span class="sf-muted"> {{ timeRange?.[0] || '--' }} 至 {{ timeRange?.[1] || '--' }}</span>
      </div>
      <el-button type="primary" icon="Check" :disabled="!canSubmit" @click="submitReservation">提交预约</el-button>
    </div>
  </div>
</template>

<script setup name="SeatFlowReservation">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fixMojibake } from '@/utils/text'
import {
  createReservation,
  listReservationBuildings,
  listReservationCampuses,
  listReservationFloors,
  listReservationRooms,
  listReservationSeats
} from '@/api/seatflow/reservation'

const loading = ref(false)
const campuses = ref([])
const buildings = ref([])
const floors = ref([])
const rooms = ref([])
const seats = ref([])
const selectedSeatId = ref(null)
const timeRange = ref([])
const defaultTime = [new Date(2000, 1, 1, 8, 0, 0), new Date(2000, 1, 1, 10, 0, 0)]

const form = reactive({
  campusId: null,
  buildingId: null,
  floorId: null,
  roomId: null
})

const selectedRoom = computed(() => rooms.value.find(item => item.id === form.roomId))
const selectedSeat = computed(() => seats.value.find(item => item.seatId === selectedSeatId.value))
const canQuerySeats = computed(() => form.roomId && timeRange.value?.[0] && timeRange.value?.[1])
const canSubmit = computed(() => canQuerySeats.value && selectedSeatId.value)
const gridColumns = computed(() => Math.max(...seats.value.map(item => item.colNum || 1), 6))
const sortedSeats = computed(() => [...seats.value].sort((a, b) => (a.rowNum - b.rowNum) || (a.colNum - b.colNum)))

function normalizeOption(item) {
  return { ...item, name: fixMojibake(item.name) }
}

function normalizeSeat(item) {
  return { ...item, seatNo: fixMojibake(item.seatNo) }
}

function resetLower(level) {
  if (level <= 1) {
    form.buildingId = null
    buildings.value = []
  }
  if (level <= 2) {
    form.floorId = null
    floors.value = []
  }
  if (level <= 3) {
    form.roomId = null
    rooms.value = []
  }
  seats.value = []
  selectedSeatId.value = null
}

async function loadCampuses() {
  const res = await listReservationCampuses()
  campuses.value = (res.data || []).map(normalizeOption)
}

async function handleCampusChange() {
  resetLower(1)
  if (!form.campusId) return
  const res = await listReservationBuildings(form.campusId)
  buildings.value = (res.data || []).map(normalizeOption)
}

async function handleBuildingChange() {
  resetLower(2)
  if (!form.buildingId) return
  const res = await listReservationFloors(form.buildingId)
  floors.value = (res.data || []).map(normalizeOption)
}

async function handleFloorChange() {
  resetLower(3)
  if (!form.floorId) return
  const res = await listReservationRooms(form.floorId)
  rooms.value = (res.data || []).map(normalizeOption)
}

function handleRoomChange() {
  seats.value = []
  selectedSeatId.value = null
  fetchSeats()
}

async function fetchSeats() {
  if (!canQuerySeats.value) return
  loading.value = true
  selectedSeatId.value = null
  try {
    const res = await listReservationSeats({
      roomId: form.roomId,
      startTime: timeRange.value[0],
      endTime: timeRange.value[1]
    })
    seats.value = (res.data || []).map(normalizeSeat)
  } finally {
    loading.value = false
  }
}

function isSeatAvailable(seat) {
  return seat.status === 'available' || seat.status === 'enabled'
}

function seatStatusClass(seat) {
  return isSeatAvailable(seat) ? 'available' : 'reserved'
}

async function submitReservation() {
  if (!canSubmit.value) return
  await createReservation({
    roomId: form.roomId,
    seatId: selectedSeatId.value,
    startTime: timeRange.value[0],
    endTime: timeRange.value[1]
  })
  ElMessage.success('预约成功')
  fetchSeats()
}

function resetAll() {
  form.campusId = null
  resetLower(1)
  timeRange.value = []
}

onMounted(loadCampuses)
</script>

<style scoped lang="scss">
@use '@/assets/styles/seatflow.scss';

.time-panel {
  justify-content: space-between;
}

.room-hint {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: -6px 0 20px;
}

.seat-grid-container {
  min-height: 260px;
  margin-top: 4px;
}

.legend {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
  font-size: 13px;
  color: #2C2621;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.box {
  width: 14px;
  height: 14px;
  border-radius: 4px;
}

.box.available {
  background: #ffffff;
  border: 2px solid #C9B59C;
}

.box.selected {
  background: #C9B59C;
}

.box.reserved {
  background: #f4f4f5;
  border: 1px dashed #c0c4cc;
}

.seating-grid {
  display: grid;
  gap: 12px;
  max-width: 900px;
  padding: 24px;
  background: #F9F8F6;
  border-radius: 12px;
}

.seat-box {
  aspect-ratio: 1;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  border: 1px solid transparent;
  background: #ffffff;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.seat-box.available {
  border: 2px solid #C9B59C;
  color: #C9B59C;
}

.seat-box.available:hover,
.seat-box.selected {
  background: #C9B59C;
  color: #ffffff;
  transform: translateY(-2px);
}

.seat-box.reserved {
  background: #F9F8F6;
  border: 1px dashed #D9CFC7;
  color: #a0aec0;
  cursor: not-allowed;
}

.seat-no {
  font-size: 15px;
  font-weight: 700;
}

.seat-coords {
  margin-top: 3px;
  font-size: 10px;
  opacity: 0.85;
}

.submit-bar {
  position: sticky;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
  padding: 16px 20px;
  background: rgba(249, 248, 246, 0.96);
  border-radius: 10px;
  backdrop-filter: blur(8px);
}
</style>
