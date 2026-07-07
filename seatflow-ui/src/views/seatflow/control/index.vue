<template>
  <div class="app-container sf-page">
    <div class="sf-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.name"
        :class="['sf-tab-item', { active: activeTab === tab.name }]"
        @click="activeTab = tab.name"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-show="activeTab === 'checkin'" class="sf-table-wrap">
      <div class="sf-toolbar">
        <div class="sf-filter-group">
          <span class="sf-muted">当前时间窗口内可签到的预约会显示在这里。</span>
        </div>
        <div class="sf-actions">
          <el-button icon="Refresh" @click="loadCheckins">刷新</el-button>
        </div>
      </div>

      <el-table v-loading="checkinLoading" :data="checkins" style="width: 100%">
        <el-table-column prop="reservationId" label="预约ID" width="96" />
        <el-table-column label="自习室" min-width="180">
          <template #default="scope">
            <div class="sf-inline-name">{{ scope.row.roomName }}</div>
            <div class="sf-muted">座位 {{ scope.row.seatNo }}</div>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" min-width="250">
          <template #default="scope">
            <div>{{ scope.row.startTime }}</div>
            <div class="sf-muted">至 {{ scope.row.endTime }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="checkDeadline" label="签到截止" min-width="170" />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" icon="Check" @click="handleCheckin(scope.row)">签到</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-show="activeTab === 'violations'" class="sf-table-wrap">
      <div class="sf-toolbar">
        <div class="sf-filter-group">
          <span class="sf-muted">爽约记录用于了解自己的限制风险。</span>
        </div>
        <div class="sf-actions">
          <el-button icon="Refresh" @click="loadViolations">刷新</el-button>
        </div>
      </div>

      <el-table v-loading="violationLoading" :data="violations" style="width: 100%">
        <el-table-column prop="violationId" label="记录ID" width="96" />
        <el-table-column prop="reservationId" label="预约ID" width="100" />
        <el-table-column prop="reason" label="原因" min-width="220" />
        <el-table-column prop="violationTime" label="发生时间" min-width="170" />
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'danger' : 'info'">{{ scope.row.status === 'active' ? '有效' : '已处理' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup name="SeatFlowControl">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { checkinReservation, listAvailableCheckins, listMyViolations } from '@/api/seatflow/control'
import { fixMojibake } from '@/utils/text'

const activeTab = ref('checkin')
const checkinLoading = ref(false)
const violationLoading = ref(false)
const checkins = ref([])
const violations = ref([])

const tabs = [
  { name: 'checkin', label: '预约签到' },
  { name: 'violations', label: '我的爽约' }
]

function normalizeRow(row) {
  const next = { ...row }
  ;['roomName', 'seatNo', 'reason'].forEach(field => {
    next[field] = fixMojibake(next[field])
  })
  return next
}

async function loadCheckins() {
  checkinLoading.value = true
  try {
    const res = await listAvailableCheckins()
    checkins.value = (res.data || []).map(normalizeRow)
  } finally {
    checkinLoading.value = false
  }
}

async function loadViolations() {
  violationLoading.value = true
  try {
    const res = await listMyViolations()
    violations.value = (res.data || []).map(normalizeRow)
  } finally {
    violationLoading.value = false
  }
}

async function handleCheckin(row) {
  await checkinReservation({ reservationId: row.reservationId })
  ElMessage.success('签到成功')
  loadCheckins()
}

onMounted(() => {
  loadCheckins()
  loadViolations()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/seatflow.scss';
</style>
