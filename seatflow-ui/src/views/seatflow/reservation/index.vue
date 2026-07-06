<template>
  <div class="app-container booking-page">
    <div class="hero"><div><span class="eyebrow">SEATFLOW · 学习空间</span><h2>预约一个安静座位</h2><p>依次选择学习空间与时间，系统会实时校验座位占用。</p></div><el-steps :active="step" simple><el-step title="选择空间"/><el-step title="查询座位"/><el-step title="确认预约"/></el-steps></div>
    <el-card shadow="never" class="filter-card">
      <template #header><div class="card-header"><strong>1. 选择空间与时间</strong><span v-if="selectedRoom" class="room-hours">开放时间 {{ selectedRoom.openTime }} - {{ selectedRoom.closeTime }}</span></div></template>
      <el-form :model="query" label-position="top" class="selector-grid">
        <el-form-item label="校区"><el-select v-model="query.campusId" placeholder="请选择校区" @change="campusChanged"><el-option v-for="item in campuses" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="楼栋"><el-select v-model="query.buildingId" placeholder="请选择楼栋" :disabled="!query.campusId" @change="buildingChanged"><el-option v-for="item in buildings" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="楼层"><el-select v-model="query.floorId" placeholder="请选择楼层" :disabled="!query.buildingId" @change="floorChanged"><el-option v-for="item in floors" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="自习室"><el-select v-model="query.roomId" placeholder="请选择自习室" :disabled="!query.floorId" @change="roomChanged"><el-option v-for="item in rooms" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="预约时间" class="time-field"><el-date-picker v-model="timeRange" type="datetimerange" start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm" :disabled-date="disablePastDate" /></el-form-item>
        <el-form-item class="search-action"><el-button type="primary" size="large" :disabled="!canSearch" :loading="loading" @click="loadSeats">查询空闲座位</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card v-loading="loading" class="seat-card" shadow="never">
      <template #header><div class="card-header"><strong>2. 选择座位</strong><div v-if="seats.length" class="seat-stats"><span><b>{{ availableCount }}</b> 个空闲</span><span>{{ seats.length }} 个座位</span></div></div></template>
      <el-empty v-if="!seats.length" description="完成上方选择并查询，即可看到实时座位图" />
      <template v-else><div class="legend"><span><i class="available"/>空闲</span><span><i class="reserved"/>已预约</span><span><i class="in-use"/>使用中</span><span><i class="disabled"/>停用</span></div><div class="seat-grid" :style="gridStyle"><button v-for="seat in seats" :key="seat.seatId" class="seat" :class="[seat.status,{selected:selectedSeatId===seat.seatId}]" :disabled="seat.status!=='available'" :aria-label="`${seat.seatNo} ${seat.status}`" @click="selectedSeatId=seat.seatId">{{seat.seatNo}}</button></div></template>
      <div v-if="seats.length" class="confirm-bar"><div><small>当前选择</small><strong>{{ selectedSeat ? `${selectedRoom?.name || ''} · ${selectedSeat.seatNo}` : '请在座位图中选择空闲座位' }}</strong><span v-if="timeRange?.length">{{ timeRange[0] }} 至 {{ timeRange[1] }}</span></div><el-button type="primary" size="large" :disabled="!selectedSeat" :loading="submitting" @click="submit">确认预约</el-button></div>
    </el-card>
  </div>
</template>

<script setup name="SeatFlowReservation">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createReservation,listAvailableSeats,listBuildings,listCampuses,listFloors,listRooms } from '@/api/seatflow/reservation'
const query=reactive({campusId:undefined,buildingId:undefined,floorId:undefined,roomId:undefined})
const campuses=ref([]),buildings=ref([]),floors=ref([]),rooms=ref([]),seats=ref([]),timeRange=ref([]),selectedSeatId=ref(),loading=ref(false),submitting=ref(false)
const canSearch=computed(()=>query.roomId&&timeRange.value?.length===2),selectedSeat=computed(()=>seats.value.find(item=>item.seatId===selectedSeatId.value)),selectedRoom=computed(()=>rooms.value.find(item=>item.id===query.roomId)),availableCount=computed(()=>seats.value.filter(item=>item.status==='available').length),step=computed(()=>selectedSeat.value?3:seats.value.length?2:query.roomId?1:0)
const gridStyle=computed(()=>({gridTemplateColumns:`repeat(${Math.max(...seats.value.map(s=>s.colNum),1)}, minmax(58px, 82px))`})),disablePastDate=date=>date.getTime()<new Date().setHours(0,0,0,0)
onMounted(async()=>{campuses.value=(await listCampuses()).data||[]})
async function campusChanged(id){Object.assign(query,{buildingId:undefined,floorId:undefined,roomId:undefined});buildings.value=id?(await listBuildings(id)).data||[]:[];floors.value=[];rooms.value=[];resetSeats()}
async function buildingChanged(id){Object.assign(query,{floorId:undefined,roomId:undefined});floors.value=id?(await listFloors(id)).data||[]:[];rooms.value=[];resetSeats()}
async function floorChanged(id){query.roomId=undefined;rooms.value=id?(await listRooms(id)).data||[]:[];resetSeats()} function roomChanged(){resetSeats()} function resetSeats(){seats.value=[];selectedSeatId.value=undefined}
async function loadSeats(){loading.value=true;selectedSeatId.value=undefined;try{seats.value=(await listAvailableSeats({roomId:query.roomId,startTime:timeRange.value[0],endTime:timeRange.value[1]})).data||[]}finally{loading.value=false}}
async function submit(){await ElMessageBox.confirm(`确认预约 ${selectedRoom.value?.name} ${selectedSeat.value.seatNo}？`,'确认预约',{type:'info'});submitting.value=true;try{await createReservation({roomId:query.roomId,seatId:selectedSeatId.value,startTime:timeRange.value[0],endTime:timeRange.value[1]});ElMessage.success('预约成功，可在“我的预约”中查看');await loadSeats()}finally{submitting.value=false}}
</script>

<style scoped lang="scss">
.hero{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;padding:24px 28px;border-radius:12px;background:linear-gradient(120deg,#ecf5ff,#f0f9eb);h2{margin:6px 0;font-size:26px}p{margin:0;color:var(--el-text-color-secondary)}.eyebrow{color:var(--el-color-primary);font-size:12px;font-weight:700;letter-spacing:1px}.el-steps{min-width:460px;background:transparent}}
.filter-card{margin-bottom:16px}.card-header{display:flex;align-items:center;justify-content:space-between}.room-hours,.seat-stats{color:var(--el-text-color-secondary);font-size:13px}.seat-stats{display:flex;gap:16px;b{color:var(--el-color-success)}}.selector-grid{display:grid;grid-template-columns:repeat(4,minmax(130px,1fr));gap:0 16px}.selector-grid :deep(.el-select){width:100%}.time-field{grid-column:span 3}.time-field :deep(.el-date-editor){width:100%}.search-action{align-self:end}.search-action .el-button{width:100%}.legend{display:flex;justify-content:center;gap:24px;color:var(--el-text-color-secondary);font-size:13px}.legend span{display:flex;align-items:center;gap:6px}.legend i{width:13px;height:13px;border-radius:3px}.available{background:#f0f9eb;border:1px solid #67c23a}.reserved{background:#fdf6ec}.in-use{background:#ecf5ff}.disabled{background:#f4f4f5}.seat-grid{display:grid;gap:12px;justify-content:center;overflow:auto;padding:28px 8px}.seat{height:52px;border:1px solid #dcdfe6;border-radius:7px;background:#fff;cursor:pointer;transition:.18s}.seat.available{color:#529b2e}.seat.available:hover{transform:translateY(-2px);box-shadow:0 4px 10px #00000012}.seat.reserved{color:#e6a23c}.seat.in-use{color:#409eff}.seat.disabled{color:#a8abb2}.seat.selected{background:#67c23a;color:#fff;box-shadow:0 0 0 3px #d1edc4}.confirm-bar{display:flex;justify-content:space-between;align-items:center;padding-top:16px;border-top:1px solid var(--el-border-color-lighter)}.confirm-bar>div{display:flex;flex-direction:column;gap:3px}.confirm-bar small,.confirm-bar span{color:var(--el-text-color-secondary)}
@media(max-width:900px){.hero{align-items:flex-start}.hero .el-steps{display:none}.selector-grid{grid-template-columns:repeat(2,1fr)}.time-field{grid-column:span 2}}@media(max-width:600px){.selector-grid{display:block}.confirm-bar{align-items:flex-end;gap:12px}.confirm-bar span{display:none}.legend{gap:10px;flex-wrap:wrap}}
</style>
