<template>
  <div class="app-container report-page">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters" label-width="76px">
        <el-form-item label="统计日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :clearable="false"
            :disabled-date="disableFutureDate"
          />
        </el-form-item>
        <el-form-item label="校区"><el-select v-model="filters.campusId" clearable placeholder="全部校区" style="width: 150px" @change="campusChanged"><el-option v-for="item in campuses" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="楼栋"><el-select v-model="filters.buildingId" clearable placeholder="全部楼栋" style="width: 150px" :disabled="!filters.campusId" @change="buildingChanged"><el-option v-for="item in buildings" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="楼层"><el-select v-model="filters.floorId" clearable placeholder="全部楼层" style="width: 140px" :disabled="!filters.buildingId" @change="floorChanged"><el-option v-for="item in floors" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="自习室"><el-select v-model="filters.roomId" clearable placeholder="全部自习室" style="width: 180px" :disabled="!filters.floorId"><el-option v-for="item in rooms" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="loadReport">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" class="summary-row" v-loading="loading">
      <el-col v-for="item in summaryItems" :key="item.label" :xs="12" :sm="8" :lg="4">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-label">{{ item.label }}</div>
          <div class="summary-value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>日均使用率</span>
              <span class="hint">按自习室与日期统计</span>
            </div>
          </template>
          <div ref="usageChartRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span>热门时段</span></template>
          <div ref="slotChartRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>自习室排行</span>
              <el-select v-model="rankingMetric" style="width: 130px" @change="loadRanking">
                <el-option label="预约次数" value="reservation_count" />
                <el-option label="签到次数" value="checkin_count" />
                <el-option label="使用率" value="usage_rate" />
              </el-select>
            </div>
          </template>
          <div ref="rankingChartRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>座位热力图</span>
              <el-select v-model="heatmapMetric" style="width: 140px" @change="loadHeatmap">
                <el-option label="预约次数" value="reservation_count" />
                <el-option label="使用分钟" value="usage_minutes" />
              </el-select>
            </div>
          </template>
          <div ref="heatmapChartRef" class="chart" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="SeatFlowReport">
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import {
  getPopularSlots,
  getRoomRanking,
  getSeatHeatmap,
  getUsageRate,
  getUsageSummary
} from '@/api/seatflow/report'
import { listBuildings, listCampuses, listFloors, listRooms } from '@/api/seatflow/reservation'

const loading = ref(false)
const filters = reactive({ campusId: undefined, buildingId: undefined, floorId: undefined, roomId: undefined })
const campuses = ref([])
const buildings = ref([])
const floors = ref([])
const rooms = ref([])
const dateRange = ref(defaultDateRange())
const rankingMetric = ref('reservation_count')
const heatmapMetric = ref('reservation_count')
const summary = reactive({})

const usageChartRef = ref()
const slotChartRef = ref()
const rankingChartRef = ref()
const heatmapChartRef = ref()
const charts = {}

const summaryItems = computed(() => [
  { label: '预约数', value: summary.reservationCount ?? 0 },
  { label: '签到数', value: summary.checkinCount ?? 0 },
  { label: '爽约数', value: summary.noShowCount ?? 0 },
  { label: '启用座位', value: summary.activeSeatCount ?? 0 },
  { label: '使用分钟', value: summary.usageMinutes ?? 0 },
  { label: '使用率', value: formatRate(summary.usageRate) }
])

function defaultDateRange() {
  const end = new Date()
  const begin = new Date()
  begin.setDate(end.getDate() - 6)
  return [formatDate(begin), formatDate(end)]
}

function formatDate(date) {
  const offset = date.getTimezoneOffset() * 60000
  return new Date(date.getTime() - offset).toISOString().slice(0, 10)
}

function formatRate(value) {
  return `${((Number(value) || 0) * 100).toFixed(1)}%`
}

function baseQuery() {
  return {
    beginTime: `${dateRange.value[0]} 00:00:00`,
    endTime: `${dateRange.value[1]} 23:59:59`,
    roomId: filters.roomId || undefined
  }
}

function chart(name, element) {
  if (!charts[name]) charts[name] = echarts.init(element)
  return charts[name]
}

function emptyOption(message = '暂无数据') {
  return {
    title: { text: message, left: 'center', top: 'middle', textStyle: { color: '#909399', fontSize: 14, fontWeight: 'normal' } }
  }
}

async function loadReport() {
  if (!dateRange.value?.length) return
  loading.value = true
  try {
    const query = baseQuery()
    const [summaryRes, usageRes, slotsRes, rankingRes, heatmapRes] = await Promise.all([
      getUsageSummary(query),
      getUsageRate(query),
      getPopularSlots({ ...query, slotType: 'hour' }),
      getRoomRanking({ ...query, metric: rankingMetric.value }),
      getSeatHeatmap({ ...query, metric: heatmapMetric.value })
    ])
    Object.assign(summary, summaryRes.data || {})
    await nextTick()
    renderUsage(usageRes.data || [])
    renderSlots(slotsRes.data || [])
    renderRanking(rankingRes.data || [])
    renderHeatmap(heatmapRes.data || [])
  } finally {
    loading.value = false
  }
}

async function loadRanking() {
  const res = await getRoomRanking({ ...baseQuery(), metric: rankingMetric.value })
  renderRanking(res.data || [])
}

async function loadHeatmap() {
  const res = await getSeatHeatmap({ ...baseQuery(), metric: heatmapMetric.value })
  renderHeatmap(res.data || [])
}

function renderUsage(rows) {
  const instance = chart('usage', usageChartRef.value)
  if (!rows.length) return instance.setOption(emptyOption(), true)
  instance.setOption({
    tooltip: { trigger: 'axis', formatter: params => `${params[0].axisValue}<br/>${params[0].marker}${formatRate(params[0].data)}` },
    grid: { left: 50, right: 20, bottom: 55, top: 20 },
    xAxis: { type: 'category', data: rows.map(item => `${item.reportDate}\n${item.roomName}`), axisLabel: { interval: 0, rotate: 25 } },
    yAxis: { type: 'value', axisLabel: { formatter: value => `${Math.round(value * 100)}%` }, max: 1 },
    series: [{ type: 'line', smooth: true, areaStyle: { opacity: 0.15 }, data: rows.map(item => Number(item.usageRate) || 0) }]
  }, true)
}

function renderSlots(rows) {
  const instance = chart('slots', slotChartRef.value)
  if (!rows.length) return instance.setOption(emptyOption(), true)
  instance.setOption({
    tooltip: { trigger: 'axis' }, legend: { data: ['预约数', '签到数'] },
    grid: { left: 45, right: 20, bottom: 35, top: 45 },
    xAxis: { type: 'category', data: rows.map(item => item.slotLabel) }, yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: '预约数', type: 'bar', data: rows.map(item => item.reservationCount || 0) },
      { name: '签到数', type: 'bar', data: rows.map(item => item.checkinCount || 0) }
    ]
  }, true)
}

function renderRanking(rows) {
  const instance = chart('ranking', rankingChartRef.value)
  if (!rows.length) return instance.setOption(emptyOption(), true)
  const field = { reservation_count: 'reservationCount', checkin_count: 'checkinCount', usage_rate: 'usageRate' }[rankingMetric.value]
  const rateMetric = rankingMetric.value === 'usage_rate'
  instance.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, valueFormatter: value => rateMetric ? formatRate(value) : value },
    grid: { left: 110, right: 35, bottom: 25, top: 20 },
    xAxis: { type: 'value', axisLabel: { formatter: value => rateMetric ? `${Math.round(value * 100)}%` : value } },
    yAxis: { type: 'category', data: rows.map(item => item.roomName), inverse: true },
    series: [{ type: 'bar', data: rows.map(item => Number(item[field]) || 0), barMaxWidth: 28 }]
  }, true)
}

function renderHeatmap(rows) {
  const instance = chart('heatmap', heatmapChartRef.value)
  if (!rows.length) return instance.setOption(emptyOption(filters.roomId ? '暂无数据' : '请选择自习室查看座位热力图'), true)
  const field = heatmapMetric.value === 'usage_minutes' ? 'usageMinutes' : 'reservationCount'
  const values = rows.map(item => Number(item[field]) || 0)
  instance.setOption({
    tooltip: { formatter: params => `${params.data[3]}<br/>${params.marker}${params.data[2]}` },
    grid: { left: 45, right: 70, bottom: 35, top: 20 },
    xAxis: { type: 'value', minInterval: 1, name: '列' }, yAxis: { type: 'value', minInterval: 1, inverse: true, name: '行' },
    visualMap: { min: 0, max: Math.max(...values, 1), calculable: true, orient: 'vertical', right: 0, top: 'middle' },
    series: [{ type: 'scatter', symbol: 'roundRect', symbolSize: 34, data: rows.map(item => [item.colNum, item.rowNum, Number(item[field]) || 0, `${item.roomName} / ${item.seatNo}`]) }]
  }, true)
}

function resetFilters() {
  Object.assign(filters, { campusId: undefined, buildingId: undefined, floorId: undefined, roomId: undefined })
  buildings.value = []; floors.value = []; rooms.value = []
  dateRange.value = defaultDateRange()
  rankingMetric.value = 'reservation_count'
  heatmapMetric.value = 'reservation_count'
  loadReport()
}

async function campusChanged(id) {
  Object.assign(filters, { buildingId: undefined, floorId: undefined, roomId: undefined })
  buildings.value = id ? (await listBuildings(id)).data || [] : []; floors.value = []; rooms.value = []
}
async function buildingChanged(id) {
  Object.assign(filters, { floorId: undefined, roomId: undefined })
  floors.value = id ? (await listFloors(id)).data || [] : []; rooms.value = []
}
async function floorChanged(id) {
  filters.roomId = undefined
  rooms.value = id ? (await listRooms(id)).data || [] : []
}

function disableFutureDate(date) {
  return date.getTime() > Date.now()
}

function resizeCharts() {
  Object.values(charts).forEach(item => item.resize())
}

onMounted(() => {
  window.addEventListener('resize', resizeCharts)
  listCampuses().then(res => { campuses.value = res.data || [] })
  loadReport()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  Object.values(charts).forEach(item => item.dispose())
})
</script>

<style scoped lang="scss">
.filter-card { margin-bottom: 16px; }
.summary-row { margin-bottom: 4px; }
.summary-card { margin-bottom: 16px; text-align: center; }
.summary-label { color: var(--el-text-color-secondary); font-size: 14px; }
.summary-value { margin-top: 10px; color: var(--el-color-primary); font-size: 26px; font-weight: 600; }
.chart-card { margin-bottom: 16px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.hint { color: var(--el-text-color-secondary); font-size: 12px; }
.chart { width: 100%; height: 340px; }
@media (max-width: 768px) {
  .filter-card :deep(.el-form-item), .filter-card :deep(.el-select), .filter-card :deep(.el-date-editor) { width: 100% !important; }
  .chart { height: 290px; }
}
</style>
