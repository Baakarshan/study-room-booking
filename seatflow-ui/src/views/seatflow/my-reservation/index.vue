<template>
  <div class="app-container reservation-list">
    <div class="page-heading">
      <div><h2>我的预约</h2><p>查看预约进度，在规定时间内取消或结束使用。</p></div>
      <el-button icon="Refresh" @click="load">刷新</el-button>
    </div>
    <el-row :gutter="12" class="summary-row">
      <el-col v-for="item in summary" :key="item.value" :xs="12" :sm="8" :md="4">
        <el-card shadow="hover" :class="['summary-card', { selected: query.status === item.value }]" @click="filterStatus(item.value)">
          <strong>{{ item.count }}</strong><span>{{ item.label }}</span>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never">
      <div class="toolbar">
        <el-segmented v-model="query.status" :options="filterOptions" @change="search" />
      </div>
      <el-table v-loading="loading" :data="rows" empty-text="暂时没有预约记录">
        <el-table-column label="学习位置" min-width="250"><template #default="s"><strong>{{ s.row.roomName }}</strong><div class="subtext">{{ [s.row.campusName, s.row.buildingName, s.row.floorName].filter(Boolean).join(' / ') }}</div></template></el-table-column>
        <el-table-column prop="seatNo" label="座位" width="80" align="center" />
        <el-table-column label="预约时段" min-width="190"><template #default="s">{{ parseTime(s.row.startTime, '{y}-{m}-{d} {h}:{i}') }}<div class="subtext">至 {{ parseTime(s.row.endTime, '{y}-{m}-{d} {h}:{i}') }}</div></template></el-table-column>
        <el-table-column label="状态" width="110"><template #default="s"><el-tag :type="tagType(s.row.status)">{{ statusLabel(s.row.status) }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="150" fixed="right"><template #default="s">
          <el-button v-if="canCancel(s.row)" link type="danger" @click="cancel(s.row)">取消预约</el-button>
          <el-button v-if="s.row.status === 'in_use'" link type="primary" @click="complete(s.row)">结束使用</el-button>
          <span v-if="!canCancel(s.row) && s.row.status !== 'in_use'" class="subtext">无需操作</span>
        </template></el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </el-card>
  </div>
</template>

<script setup name="SeatFlowMyReservation">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelReservation, listMyReservations } from '@/api/seatflow/reservation'
import { completeReservation } from '@/api/seatflow/control'

const statuses = [{ value: 'pending_checkin', label: '待签到' }, { value: 'in_use', label: '使用中' }, { value: 'completed', label: '已完成' }, { value: 'cancelled', label: '已取消' }, { value: 'no_show', label: '已爽约' }]
const filterOptions = [{ label: '全部', value: '' }, ...statuses]
const query = reactive({ pageNum: 1, pageSize: 10, status: '' }), rows = ref([]), total = ref(0), loading = ref(false)
const summary = computed(() => statuses.map(item => ({ ...item, count: rows.value.filter(row => row.status === item.value).length })))
onMounted(load)
async function load() { loading.value = true; try { const res = await listMyReservations({ ...query, status: query.status || undefined }); rows.value = res.rows || []; total.value = res.total || 0 } finally { loading.value = false } }
function search() { query.pageNum = 1; load() }
function filterStatus(status) { query.status = query.status === status ? '' : status; search() }
function statusLabel(value) { return statuses.find(item => item.value === value)?.label || value }
function tagType(value) { return ({ pending_checkin: 'warning', in_use: 'primary', cancelled: 'info', no_show: 'danger', completed: 'success' })[value] || 'info' }
function canCancel(row) { return row.status === 'pending_checkin' && new Date(row.startTime).getTime() > Date.now() }
async function cancel(row) { await ElMessageBox.confirm(`确认取消 ${row.roomName} ${row.seatNo} 的预约？`, '取消预约', { type: 'warning' }); await cancelReservation(row.reservationId); ElMessage.success('预约已取消'); load() }
async function complete(row) { await ElMessageBox.confirm('结束后将释放座位，且无法恢复。确认结束本次使用？', '结束使用', { type: 'warning' }); await completeReservation({ reservationId: row.reservationId }); ElMessage.success('本次使用已结束'); load() }
</script>

<style scoped lang="scss">
.page-heading { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; h2 { margin: 0 0 6px; font-size: 22px; } p { margin: 0; color: var(--el-text-color-secondary); } }
.summary-row { margin-bottom: 4px; }.summary-card { margin-bottom: 12px; cursor: pointer; :deep(.el-card__body) { display: flex; align-items: baseline; justify-content: space-between; } strong { color: var(--el-color-primary); font-size: 24px; } span { color: var(--el-text-color-secondary); } }.summary-card.selected { border-color: var(--el-color-primary); background: var(--el-color-primary-light-9); }
.toolbar { margin-bottom: 16px; overflow-x: auto; }.subtext { margin-top: 4px; color: var(--el-text-color-secondary); font-size: 12px; }
</style>
