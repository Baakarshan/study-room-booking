<template>
  <div class="app-container sf-page report-page">
    <div class="sf-toolbar">
      <div class="sf-filter-group">
        <div class="sf-field">
          <span class="sf-field-label">统计时段</span>
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            format="YYYY-MM-DD HH:mm"
            style="width: 390px"
          />
        </div>
        <div class="sf-field">
          <span class="sf-field-label">指标</span>
          <el-select v-model="query.metric" style="width: 170px">
            <el-option label="预约数" value="reservation_count" />
            <el-option label="使用分钟" value="usage_minutes" />
            <el-option label="签到数" value="checkin_count" />
            <el-option label="使用率" value="usage_rate" />
          </el-select>
        </div>
      </div>
      <div class="sf-actions">
        <el-button type="primary" icon="Search" @click="loadReport">生成报表</el-button>
        <el-button icon="Refresh" @click="resetRange">重置</el-button>
      </div>
    </div>

    <div class="sf-metric-row" v-loading="loading">
      <div v-for="item in metrics" :key="item.label" class="sf-metric">
        <div class="sf-metric-label">{{ item.label }}</div>
        <div class="sf-metric-value">{{ item.value }}</div>
      </div>
    </div>

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

    <div v-show="activeTab === 'heatmap'" class="sf-table-wrap">
      <div class="heat-list">
        <div v-for="seat in heatmap" :key="seat.seatId" class="heat-row">
          <div class="heat-main">
            <span class="sf-inline-name">{{ seat.roomName }} / {{ seat.seatNo }}</span>
            <span class="sf-muted">{{ seat.rowNum }} 行 {{ seat.colNum }} 列</span>
          </div>
          <div class="heat-bar"><span :style="{ width: heatWidth(seat) }"></span></div>
          <div class="heat-value">{{ seat.reservationCount }} 次</div>
        </div>
      </div>
      <el-empty v-if="!heatmap.length && !loading" description="暂无热力数据" />
    </div>

    <div v-show="activeTab === 'usage'" class="sf-table-wrap">
      <el-table :data="usageRate" style="width: 100%">
        <el-table-column prop="reportDate" label="日期" width="140" />
        <el-table-column prop="roomName" label="自习室" min-width="180" />
        <el-table-column prop="usageMinutes" label="使用分钟" width="120" />
        <el-table-column prop="availableMinutes" label="可用分钟" width="120" />
        <el-table-column label="使用率" min-width="180">
          <template #default="scope">
            <div class="rate-cell">
              <el-progress :percentage="toPercent(scope.row.usageRate)" :show-text="false" />
              <span>{{ toPercent(scope.row.usageRate) }}%</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-show="activeTab === 'slots'" class="sf-table-wrap">
      <el-table :data="popularSlots" style="width: 100%">
        <el-table-column prop="slotLabel" label="时段" width="120" />
        <el-table-column prop="reservationCount" label="预约数" width="120" />
        <el-table-column prop="checkinCount" label="签到数" width="120" />
        <el-table-column label="热度" min-width="220">
          <template #default="scope">
            <div class="heat-bar"><span :style="{ width: slotWidth(scope.row) }"></span></div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-show="activeTab === 'rooms'" class="sf-table-wrap">
      <el-table :data="roomRanking" style="width: 100%">
        <el-table-column type="index" label="#" width="70" />
        <el-table-column prop="roomName" label="自习室" min-width="180" />
        <el-table-column prop="reservationCount" label="预约数" width="110" />
        <el-table-column prop="checkinCount" label="签到数" width="110" />
        <el-table-column prop="usageMinutes" label="使用分钟" width="120" />
        <el-table-column label="使用率" width="120">
          <template #default="scope">{{ toPercent(scope.row.usageRate) }}%</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup name="SeatFlowReport">
import { computed, onMounted, reactive, ref } from 'vue'
import { fixMojibake } from '@/utils/text'
import { getPopularSlots, getRoomRanking, getSeatHeatmap, getUsageRate, getUsageSummary } from '@/api/seatflow/report'

const loading = ref(false)
const activeTab = ref('heatmap')
const summary = ref({})
const heatmap = ref([])
const usageRate = ref([])
const popularSlots = ref([])
const roomRanking = ref([])
const dateRange = ref(defaultRange())

const query = reactive({
  metric: 'reservation_count'
})

const tabs = [
  { name: 'heatmap', label: '座位热力' },
  { name: 'usage', label: '使用率' },
  { name: 'slots', label: '热门时段' },
  { name: 'rooms', label: '自习室排行' }
]

const metrics = computed(() => [
  { label: '预约数', value: summary.value.reservationCount || 0 },
  { label: '签到数', value: summary.value.checkinCount || 0 },
  { label: '爽约数', value: summary.value.noShowCount || 0 },
  { label: '使用率', value: `${toPercent(summary.value.usageRate)}%` }
])

function pad(value) {
  return String(value).padStart(2, '0')
}

function formatDateTime(date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

function defaultRange() {
  const end = new Date()
  end.setHours(23, 59, 59, 0)
  const begin = new Date(end)
  begin.setDate(begin.getDate() - 6)
  begin.setHours(0, 0, 0, 0)
  return [formatDateTime(begin), formatDateTime(end)]
}

function baseQuery() {
  return {
    beginTime: dateRange.value?.[0],
    endTime: dateRange.value?.[1],
    metric: query.metric,
    slotType: 'hour'
  }
}

function normalizeName(row) {
  return {
    ...row,
    roomName: fixMojibake(row.roomName),
    seatNo: fixMojibake(row.seatNo)
  }
}

function toPercent(value) {
  return Math.round(Number(value || 0) * 100)
}

const maxHeat = computed(() => Math.max(...heatmap.value.map(item => item.reservationCount || 0), 1))
const maxSlot = computed(() => Math.max(...popularSlots.value.map(item => item.reservationCount || 0), 1))

function heatWidth(row) {
  return `${Math.max(6, Math.round((row.reservationCount || 0) / maxHeat.value * 100))}%`
}

function slotWidth(row) {
  return `${Math.max(6, Math.round((row.reservationCount || 0) / maxSlot.value * 100))}%`
}

async function loadReport() {
  if (!dateRange.value?.[0] || !dateRange.value?.[1]) return
  loading.value = true
  try {
    const params = baseQuery()
    const [summaryRes, heatmapRes, usageRes, slotsRes, roomsRes] = await Promise.all([
      getUsageSummary(params),
      getSeatHeatmap(params),
      getUsageRate(params),
      getPopularSlots(params),
      getRoomRanking(params)
    ])
    summary.value = summaryRes.data || {}
    heatmap.value = (heatmapRes.data || []).map(normalizeName)
    usageRate.value = (usageRes.data || []).map(normalizeName)
    popularSlots.value = slotsRes.data || []
    roomRanking.value = (roomsRes.data || []).map(normalizeName)
  } finally {
    loading.value = false
  }
}

function resetRange() {
  dateRange.value = defaultRange()
  query.metric = 'reservation_count'
  loadReport()
}

onMounted(loadReport)
</script>

<style scoped lang="scss">
@use '@/assets/styles/seatflow.scss';

.heat-list {
  display: grid;
  gap: 10px;
}

.heat-row {
  display: grid;
  grid-template-columns: minmax(180px, 1.4fr) minmax(160px, 2fr) 70px;
  align-items: center;
  gap: 14px;
  min-height: 52px;
  border-bottom: 1px solid #D9CFC7;
}

.heat-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.heat-bar {
  height: 10px;
  overflow: hidden;
  background: #F9F8F6;
  border-radius: 999px;
}

.heat-bar span {
  display: block;
  height: 100%;
  background: #C9B59C;
  border-radius: 999px;
}

.heat-value {
  color: #2C2621;
  font-weight: 600;
  text-align: right;
}

.rate-cell {
  display: grid;
  grid-template-columns: minmax(120px, 1fr) 52px;
  align-items: center;
  gap: 12px;
}

@media (max-width: 760px) {
  .heat-row {
    grid-template-columns: 1fr;
    padding: 12px 0;
  }

  .heat-value {
    text-align: left;
  }
}
</style>
