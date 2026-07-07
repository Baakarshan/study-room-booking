<template>
  <div class="app-container seatflow-page">
    <div class="page-heading">
      <div>
        <h2>预约管理</h2>
        <p>统一查询学生预约与使用状态，便于课堂演示和日常管理。</p>
      </div>
      <el-button icon="Refresh" @click="load">刷新</el-button>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" label-width="68px">
        <el-form-item label="账号"><el-input v-model="query.userName" clearable placeholder="学生账号" /></el-form-item>
        <el-form-item label="学号"><el-input v-model="query.studentNo" clearable placeholder="学生学号" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部状态" style="width: 150px">
            <el-option v-for="item in statuses" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="校区"><el-select v-model="query.campusId" clearable placeholder="全部校区" style="width:140px" @change="campusChanged"><el-option v-for="item in campuses" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="楼栋"><el-select v-model="query.buildingId" clearable placeholder="全部楼栋" style="width:140px" :disabled="!query.campusId" @change="buildingChanged"><el-option v-for="item in buildings" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="楼层"><el-select v-model="query.floorId" clearable placeholder="全部楼层" style="width:130px" :disabled="!query.buildingId" @change="floorChanged"><el-option v-for="item in floors" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="自习室"><el-select v-model="query.roomId" clearable placeholder="全部自习室" style="width:170px" :disabled="!query.floorId"><el-option v-for="item in rooms" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button icon="Refresh" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="12" class="status-summary">
      <el-col v-for="item in statusSummary" :key="item.value" :xs="12" :sm="8" :md="4">
        <button class="summary-chip" :class="{ active: query.status === item.value }" @click="selectStatus(item.value)">
          <span>{{ item.label }}</span><strong>{{ item.count }}</strong>
        </button>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" empty-text="没有符合条件的预约">
        <el-table-column label="学生" min-width="150"><template #default="s"><strong>{{ s.row.nickName || '-' }}</strong><div class="subtext">{{ s.row.userName }} · {{ s.row.studentNo || '未填写学号' }}</div></template></el-table-column>
        <el-table-column label="位置" min-width="260"><template #default="s">{{ locationText(s.row) }}</template></el-table-column>
        <el-table-column prop="seatNo" label="座位" width="80" align="center" />
        <el-table-column label="预约时段" min-width="190"><template #default="s">{{ parseTime(s.row.startTime, '{y}-{m}-{d} {h}:{i}') }}<div class="subtext">至 {{ parseTime(s.row.endTime, '{y}-{m}-{d} {h}:{i}') }}</div></template></el-table-column>
        <el-table-column label="状态" width="110" align="center"><template #default="s"><el-tag :type="statusType(s.row.status)">{{ statusLabel(s.row.status) }}</el-tag></template></el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </el-card>
  </div>
</template>

<script setup name="SeatFlowReservationManage">
import { computed, onMounted, reactive, ref } from 'vue'
import { fixMojibake } from '@/utils/text'
import { listReservationCampuses, listReservationBuildings, listReservationFloors, listReservationRooms, listManagedReservations } from '@/api/seatflow/reservation'

const statuses = [
  { value: 'pending_checkin', label: '待签到' }, { value: 'in_use', label: '使用中' },
  { value: 'completed', label: '已完成' }, { value: 'cancelled', label: '已取消' },
  { value: 'no_show', label: '已爽约' }
]
const query = reactive({ pageNum: 1, pageSize: 10, userName: undefined, studentNo: undefined, campusId: undefined, buildingId: undefined, floorId: undefined, roomId: undefined, status: undefined })
const rows = ref([]), total = ref(0), loading = ref(false), dateRange = ref([])
const campuses = ref([]), buildings = ref([]), floors = ref([]), rooms = ref([])
const statusSummary = computed(() => statuses.map(item => ({ ...item, count: rows.value.filter(row => row.status === item.value).length })))
const normalizeOption = item => ({ ...item, name: fixMojibake(item.name) })

async function campusChanged(id) { Object.assign(query, { buildingId: undefined, floorId: undefined, roomId: undefined }); buildings.value = id ? (((await listReservationBuildings(id)).data) || []).map(normalizeOption) : []; floors.value = []; rooms.value = [] }
async function buildingChanged(id) { Object.assign(query, { floorId: undefined, roomId: undefined }); floors.value = id ? (((await listReservationFloors(id)).data) || []).map(normalizeOption) : []; rooms.value = [] }
async function floorChanged(id) { query.roomId = undefined; rooms.value = id ? (((await listReservationRooms(id)).data) || []).map(normalizeOption) : [] }
async function load() {
  loading.value = true
  try {
    const params = { ...query, beginTime: dateRange.value?.[0] ? `${dateRange.value[0]} 00:00:00` : undefined, endTime: dateRange.value?.[1] ? `${dateRange.value[1]} 23:59:59` : undefined }
    const res = await listManagedReservations(params)
    rows.value = res.rows || []; total.value = res.total || 0
  } finally { loading.value = false }
}
function search() { query.pageNum = 1; load() }
function reset() { Object.assign(query, { pageNum: 1, pageSize: 10, userName: undefined, studentNo: undefined, campusId: undefined, buildingId: undefined, floorId: undefined, roomId: undefined, status: undefined }); buildings.value = []; floors.value = []; rooms.value = []; dateRange.value = []; load() }
function selectStatus(status) { query.status = query.status === status ? undefined : status; search() }
function locationText(row) { return [row.campusName, row.buildingName, row.floorName, row.roomName].filter(Boolean).join(' / ') || '-' }
function statusLabel(value) { return statuses.find(item => item.value === value)?.label || value }
function statusType(value) { return ({ pending_checkin: 'warning', in_use: 'primary', completed: 'success', cancelled: 'info', no_show: 'danger' })[value] || 'info' }
onMounted(async () => { campuses.value = (((await listReservationCampuses()).data) || []).map(normalizeOption); load() })
</script>

<style scoped lang="scss">
.page-heading { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; h2 { margin: 0 0 6px; font-size: 22px; } p { margin: 0; color: var(--el-text-color-secondary); } }
.filter-card { margin-bottom: 12px; }
.status-summary { margin-bottom: 4px; }
.summary-chip { width: 100%; margin-bottom: 12px; padding: 14px 16px; display: flex; justify-content: space-between; border: 1px solid var(--el-border-color-light); border-radius: 8px; background: #fff; color: var(--el-text-color-regular); cursor: pointer; }
.summary-chip strong { color: var(--el-color-primary); font-size: 18px; }.summary-chip.active { border-color: var(--el-color-primary); background: var(--el-color-primary-light-9); }
.subtext { margin-top: 4px; color: var(--el-text-color-secondary); font-size: 12px; }
@media (max-width: 768px) { .page-heading { align-items: center; } }
</style>
