<template>
  <div class="app-container sf-page">
    <div class="sf-tabs">
      <button
        v-for="item in statusTabs"
        :key="item.value"
        :class="['sf-tab-item', { active: query.status === item.value }]"
        @click="switchStatus(item.value)"
      >
        {{ item.label }}
      </button>
    </div>

    <div class="sf-toolbar">
      <div class="sf-filter-group">
        <span class="sf-muted">查看预约记录、取消未开始预约，并从可签到记录进入签到。</span>
      </div>
      <div class="sf-actions">
        <el-button icon="Refresh" @click="loadData">刷新</el-button>
      </div>
    </div>

    <div class="sf-table-wrap">
      <el-table v-loading="loading" :data="rows" style="width: 100%">
        <el-table-column prop="reservationId" label="预约ID" width="96" />
        <el-table-column label="空间" min-width="220">
          <template #default="scope">
            <div class="sf-inline-name">{{ scope.row.roomName }}</div>
            <div class="sf-muted">{{ scope.row.campusName }} / {{ scope.row.buildingName }} / {{ scope.row.floorName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="seatNo" label="座位" width="110" />
        <el-table-column label="预约时间" min-width="250">
          <template #default="scope">
            <div>{{ scope.row.startTime }}</div>
            <div class="sf-muted">至 {{ scope.row.endTime }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="checkDeadline" label="签到截止" min-width="170" />
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="statusMeta(scope.row.status).type">{{ statusMeta(scope.row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="scope">
            <el-button v-if="canCancel(scope.row)" size="small" type="danger" icon="Close" @click="handleCancel(scope.row)">取消</el-button>
            <span v-else class="sf-muted">--</span>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="query.pageNum"
        v-model:limit="query.pageSize"
        @pagination="loadData"
      />
    </div>
  </div>
</template>

<script setup name="SeatFlowMyReservation">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelReservation, listMyReservations } from '@/api/seatflow/reservation'
import { fixMojibake } from '@/utils/text'

const loading = ref(false)
const rows = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  status: ''
})

const statusTabs = [
  { label: '全部', value: '' },
  { label: '待签到', value: 'pending_checkin' },
  { label: '使用中', value: 'in_use' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' },
  { label: '爽约', value: 'no_show' }
]

function statusMeta(status) {
  const map = {
    pending_checkin: { label: '待签到', type: 'warning' },
    in_use: { label: '使用中', type: 'success' },
    completed: { label: '已完成', type: 'info' },
    cancelled: { label: '已取消', type: 'info' },
    no_show: { label: '爽约', type: 'danger' }
  }
  return map[status] || { label: status || '--', type: 'info' }
}

function normalizeRow(row) {
  const fields = ['roomName', 'seatNo', 'campusName', 'buildingName', 'floorName']
  const next = { ...row }
  fields.forEach(field => {
    next[field] = fixMojibake(next[field])
  })
  return next
}

function switchStatus(status) {
  query.status = status
  query.pageNum = 1
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const res = await listMyReservations(query)
    rows.value = (res.rows || []).map(normalizeRow)
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function canCancel(row) {
  return row.status === 'pending_checkin'
}

function handleCancel(row) {
  ElMessageBox.confirm(`确定取消 ${row.roomName} / ${row.seatNo} 的预约吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await cancelReservation(row.reservationId)
    ElMessage.success('取消成功')
    loadData()
  }).catch(() => {})
}

onMounted(loadData)
</script>

<style scoped lang="scss">
@use '@/assets/styles/seatflow.scss';
</style>
