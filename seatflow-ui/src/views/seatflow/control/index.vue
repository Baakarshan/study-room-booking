<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的签到</span>
          <el-button icon="Refresh" @click="loadData">刷新</el-button>
        </div>
      </template>
      <el-alert title="预约开始后 15 分钟内可签到，超时将自动记为爽约。" type="info" :closable="false" class="mb16" />
      <el-table v-loading="loading" :data="reservations">
        <el-table-column label="自习室" prop="roomName" min-width="140" />
        <el-table-column label="座位" prop="seatNo" width="100" />
        <el-table-column label="开始时间" min-width="170"><template #default="s">{{ parseTime(s.row.startTime) }}</template></el-table-column>
        <el-table-column label="签到截止" min-width="170"><template #default="s">{{ parseTime(s.row.checkDeadline) }}</template></el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="s"><el-button link type="primary" :loading="checkingId === s.row.reservationId" @click="checkin(s.row)">签到</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card shadow="never" class="mt16">
      <template #header><span>我的爽约记录</span></template>
      <el-table v-loading="loading" :data="violations">
        <el-table-column label="预约编号" prop="reservationId" width="120" />
        <el-table-column label="原因" prop="reason" min-width="220" />
        <el-table-column label="爽约时间" min-width="180"><template #default="s">{{ parseTime(s.row.violationTime) }}</template></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { checkinReservation, listAvailableCheckins, listMyViolations } from '@/api/seatflow/control'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const checkingId = ref(null)
const reservations = ref([])
const violations = ref([])

async function loadData() {
  loading.value = true
  try {
    const [checkinRes, violationRes] = await Promise.all([listAvailableCheckins(), listMyViolations()])
    reservations.value = checkinRes.data || []
    violations.value = violationRes.data || []
  } finally {
    loading.value = false
  }
}

async function checkin(row) {
  checkingId.value = row.reservationId
  try {
    await checkinReservation({ reservationId: row.reservationId })
    proxy.$modal.msgSuccess('签到成功')
    await loadData()
  } finally {
    checkingId.value = null
  }
}

loadData()
</script>

<style scoped>
.card-header { display: flex; align-items: center; justify-content: space-between; }
.mb16 { margin-bottom: 16px; }
.mt16 { margin-top: 16px; }
</style>
