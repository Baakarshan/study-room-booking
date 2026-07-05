<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header><span>座位预约</span></template>
      <el-form :inline="true" :model="query" label-width="72px">
        <el-form-item label="校区"><el-select v-model="query.campusId" placeholder="请选择" style="width:180px" @change="campusChanged"><el-option v-for="item in campuses" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="楼栋"><el-select v-model="query.buildingId" placeholder="请选择" style="width:180px" @change="buildingChanged"><el-option v-for="item in buildings" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="楼层"><el-select v-model="query.floorId" placeholder="请选择" style="width:160px" @change="floorChanged"><el-option v-for="item in floors" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="自习室"><el-select v-model="query.roomId" placeholder="请选择" style="width:220px" @change="roomChanged"><el-option v-for="item in rooms" :key="item.id" :label="`${item.name} (${item.openTime}-${item.closeTime})`" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker v-model="timeRange" type="datetimerange" start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm" :disabled-date="disablePastDate" />
        </el-form-item>
        <el-form-item><el-button type="primary" :disabled="!canSearch" @click="loadSeats">查询座位</el-button></el-form-item>
      </el-form>
    </el-card>

    <el-card v-loading="loading" class="seat-card" shadow="never">
      <template #header><div class="legend"><span>座位图</span><i class="available" />空闲<i class="reserved" />已预约<i class="in-use" />使用中<i class="disabled" />停用</div></template>
      <el-empty v-if="!seats.length" description="请选择自习室和预约时间后查询" />
      <div v-else class="seat-grid" :style="gridStyle">
        <button v-for="seat in seats" :key="seat.seatId" class="seat" :class="[seat.status, { selected: selectedSeatId === seat.seatId }]" :disabled="seat.status !== 'available'" @click="selectedSeatId = seat.seatId">
          {{ seat.seatNo }}
        </button>
      </div>
      <div v-if="seats.length" class="actions"><span v-if="selectedSeat">已选择：{{ selectedSeat.seatNo }}</span><el-button type="primary" :disabled="!selectedSeat" :loading="submitting" @click="submit">确认预约</el-button></div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createReservation, listAvailableSeats, listBuildings, listCampuses, listFloors, listRooms } from '@/api/seatflow/reservation'

const query = reactive({ campusId: undefined, buildingId: undefined, floorId: undefined, roomId: undefined })
const campuses = ref([]), buildings = ref([]), floors = ref([]), rooms = ref([]), seats = ref([])
const timeRange = ref([]), selectedSeatId = ref(), loading = ref(false), submitting = ref(false)
const canSearch = computed(() => query.roomId && timeRange.value?.length === 2)
const selectedSeat = computed(() => seats.value.find(item => item.seatId === selectedSeatId.value))
const gridStyle = computed(() => ({ gridTemplateColumns: `repeat(${Math.max(...seats.value.map(s => s.colNum), 1)}, minmax(64px, 90px))` }))
const disablePastDate = date => date.getTime() < new Date().setHours(0, 0, 0, 0)

onMounted(async () => { campuses.value = (await listCampuses()).data || [] })
async function campusChanged(id) { Object.assign(query, { buildingId: undefined, floorId: undefined, roomId: undefined }); buildings.value = id ? (await listBuildings(id)).data || [] : []; floors.value = []; rooms.value = []; resetSeats() }
async function buildingChanged(id) { Object.assign(query, { floorId: undefined, roomId: undefined }); floors.value = id ? (await listFloors(id)).data || [] : []; rooms.value = []; resetSeats() }
async function floorChanged(id) { query.roomId = undefined; rooms.value = id ? (await listRooms(id)).data || [] : []; resetSeats() }
function roomChanged() { resetSeats() }
function resetSeats() { seats.value = []; selectedSeatId.value = undefined }
async function loadSeats() { loading.value = true; selectedSeatId.value = undefined; try { seats.value = (await listAvailableSeats({ roomId: query.roomId, startTime: timeRange.value[0], endTime: timeRange.value[1] })).data || [] } finally { loading.value = false } }
async function submit() {
  await ElMessageBox.confirm(`确认预约座位 ${selectedSeat.value.seatNo}？`, '提交预约', { type: 'info' })
  submitting.value = true
  try { await createReservation({ roomId: query.roomId, seatId: selectedSeatId.value, startTime: timeRange.value[0], endTime: timeRange.value[1] }); ElMessage.success('预约成功'); await loadSeats() } finally { submitting.value = false }
}
</script>

<style scoped>
.seat-card { margin-top: 16px; }.legend { display:flex; align-items:center; gap:8px }.legend span{margin-right:auto}.legend i{width:14px;height:14px;border-radius:3px}.seat-grid{display:grid;gap:12px;justify-content:center;overflow:auto;padding:24px}.seat{height:54px;border:1px solid #dcdfe6;border-radius:6px;background:#fff;cursor:pointer}.seat.available{border-color:#67c23a;color:#529b2e}.seat.reserved{background:#fdf6ec;color:#e6a23c}.seat.in-use{background:#ecf5ff;color:#409eff}.seat.disabled{background:#f4f4f5;color:#a8abb2}.seat.selected{background:#67c23a;color:#fff;box-shadow:0 0 0 3px #d1edc4}.actions{display:flex;justify-content:flex-end;align-items:center;gap:20px;border-top:1px solid #ebeef5;padding-top:16px}
</style>
