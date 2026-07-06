<template>
  <div class="app-container control-page">
    <div class="page-heading"><div><h2>签到管控</h2><p>预约开始后 15 分钟内完成签到，进入自习室后请按时使用。</p></div><el-button icon="Refresh" @click="loadData">刷新</el-button></div>
    <el-row :gutter="16" class="profile-row">
      <el-col :xs="24" :md="16"><el-alert :title="riskTitle" :type="profile.blacklisted ? 'error' : profile.violationCount ? 'warning' : 'success'" :closable="false" show-icon><template #default>{{ riskDescription }}</template></el-alert></el-col>
      <el-col :xs="24" :md="8"><el-card shadow="never" class="risk-card"><div><span>爽约进度</span><strong>{{ profile.violationCount || 0 }} / {{ profile.threshold || 3 }}</strong></div><el-progress :percentage="riskPercentage" :status="profile.blacklisted ? 'exception' : undefined" :show-text="false" /></el-card></el-col>
    </el-row>

    <el-card shadow="never">
      <template #header><div class="card-header"><span>可签到预约</span><el-tag type="primary" effect="plain">{{ reservations.length }} 项</el-tag></div></template>
      <el-table v-loading="loading" :data="reservations" empty-text="当前没有需要签到的预约">
        <el-table-column label="学习位置" prop="roomName" min-width="180"><template #default="s"><strong>{{ s.row.roomName }}</strong><div class="subtext">座位 {{ s.row.seatNo }}</div></template></el-table-column>
        <el-table-column label="预约时段" min-width="190"><template #default="s">{{ parseTime(s.row.startTime, '{y}-{m}-{d} {h}:{i}') }}<div class="subtext">至 {{ parseTime(s.row.endTime, '{h}:{i}') }}</div></template></el-table-column>
        <el-table-column label="签到截止" min-width="180"><template #default="s"><span :class="{ urgent: remainingSeconds(s.row) <= 300 }">{{ countdownText(s.row) }}</span><div class="subtext">{{ parseTime(s.row.checkDeadline, '{y}-{m}-{d} {h}:{i}') }}</div></template></el-table-column>
        <el-table-column label="操作" width="110" align="center"><template #default="s"><el-button type="primary" :loading="checkingId === s.row.reservationId" @click="checkin(s.row)">立即签到</el-button></template></el-table-column>
      </el-table>
    </el-card>
    <el-card shadow="never" class="mt16">
      <template #header><div class="card-header"><span>我的爽约记录</span><span class="hint">记录仅用于课程项目规则演示</span></div></template>
      <el-table v-loading="loading" :data="violations" empty-text="保持得很好，暂无爽约记录">
        <el-table-column label="预约编号" prop="reservationId" width="120" /><el-table-column label="原因" prop="reason" min-width="220" /><el-table-column label="爽约时间" min-width="180"><template #default="s">{{ parseTime(s.row.violationTime) }}</template></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="SeatFlowControl">
import { computed, getCurrentInstance, onBeforeUnmount, ref } from 'vue'
import { checkinReservation, getControlProfile, listAvailableCheckins, listMyViolations } from '@/api/seatflow/control'

const { proxy } = getCurrentInstance()
const loading = ref(false), checkingId = ref(null), reservations = ref([]), violations = ref([]), now = ref(Date.now())
const profile = ref({ violationCount: 0, threshold: 3, blacklisted: false })
const riskPercentage = computed(() => Math.min(100, Math.round((profile.value.violationCount || 0) / (profile.value.threshold || 3) * 100)))
const riskTitle = computed(() => profile.value.blacklisted ? '当前账号已进入黑名单' : profile.value.violationCount ? '请留意爽约次数' : '当前信用状态良好')
const riskDescription = computed(() => profile.value.blacklisted ? '请联系管理员解除后再进行预约。' : `再爽约 ${Math.max(0, (profile.value.threshold || 3) - (profile.value.violationCount || 0))} 次将进入黑名单。`)
const timer = window.setInterval(() => { now.value = Date.now() }, 1000)
onBeforeUnmount(() => window.clearInterval(timer))

async function loadData() { loading.value = true; try { const [checkinRes, violationRes, profileRes] = await Promise.all([listAvailableCheckins(), listMyViolations(), getControlProfile()]); reservations.value = checkinRes.data || []; violations.value = violationRes.data || violationRes.rows || []; profile.value = { ...profile.value, ...(profileRes.data || {}) } } finally { loading.value = false } }
function remainingSeconds(row) { return Math.max(0, Math.floor((new Date(row.checkDeadline).getTime() - now.value) / 1000)) }
function countdownText(row) { const seconds = remainingSeconds(row); if (!seconds) return '即将截止'; return `${Math.floor(seconds / 60)} 分 ${String(seconds % 60).padStart(2, '0')} 秒` }
async function checkin(row) { checkingId.value = row.reservationId; try { await checkinReservation({ reservationId: row.reservationId }); proxy.$modal.msgSuccess('签到成功，祝学习顺利'); await loadData() } finally { checkingId.value = null } }
loadData()
</script>

<style scoped lang="scss">
.page-heading,.card-header,.risk-card>div { display:flex; align-items:center; justify-content:space-between; }.page-heading { align-items:flex-start; margin-bottom:16px; h2{margin:0 0 6px;font-size:22px} p{margin:0;color:var(--el-text-color-secondary)} }.profile-row { margin-bottom:16px; }.risk-card { height:100%; :deep(.el-card__body){padding:13px 18px} strong{color:var(--el-color-primary)} .el-progress{margin-top:10px} }.mt16{margin-top:16px}.subtext,.hint{margin-top:4px;color:var(--el-text-color-secondary);font-size:12px}.urgent{color:var(--el-color-danger);font-weight:600}
@media(max-width:991px){.risk-card{margin-top:12px}}
</style>
