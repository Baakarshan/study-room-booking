<template>
  <div class="app-container sf-page">
    <div class="sf-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.name"
        :class="['sf-tab-item', { active: activeTab === tab.name }]"
        @click="switchTab(tab.name)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div class="sf-toolbar">
      <div class="sf-filter-group">
        <div class="sf-field">
          <span class="sf-field-label">用户名</span>
          <el-input v-model="query.userName" placeholder="输入用户名" clearable style="width: 180px" @keyup.enter="loadData" />
        </div>
        <div class="sf-field">
          <span class="sf-field-label">学号</span>
          <el-input v-model="query.studentNo" placeholder="输入学号" clearable style="width: 180px" @keyup.enter="loadData" />
        </div>
      </div>
      <div class="sf-actions">
        <el-button type="primary" icon="Search" @click="search">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
    </div>

    <div v-show="activeTab === 'blacklist'" class="sf-table-wrap">
      <el-table v-loading="loading" :data="rows" style="width: 100%">
        <el-table-column prop="blacklistId" label="名单ID" width="96" />
        <el-table-column label="学生" min-width="180">
          <template #default="scope">
            <div class="sf-inline-name">{{ scope.row.nickName || scope.row.userName }}</div>
            <div class="sf-muted">{{ scope.row.userName }} / {{ scope.row.studentNo || '--' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="violationCount" label="爽约次数" width="110" />
        <el-table-column prop="reason" label="原因" min-width="220" />
        <el-table-column prop="startTime" label="加入时间" min-width="170" />
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'danger' : 'info'">{{ scope.row.status === 'active' ? '限制中' : '已解除' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-show="activeTab === 'violations'" class="sf-table-wrap">
      <el-table v-loading="loading" :data="rows" style="width: 100%">
        <el-table-column prop="violationId" label="记录ID" width="96" />
        <el-table-column label="学生" min-width="170">
          <template #default="scope">
            <div class="sf-inline-name">{{ scope.row.userName }}</div>
            <div class="sf-muted">{{ scope.row.studentNo || '--' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="reservationId" label="预约ID" width="110" />
        <el-table-column prop="reason" label="原因" min-width="220" />
        <el-table-column prop="violationTime" label="发生时间" min-width="170" />
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'danger' : 'info'">{{ scope.row.status === 'active' ? '有效' : '已处理' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="query.pageNum"
      v-model:limit="query.pageSize"
      @pagination="loadData"
    />
  </div>
</template>

<script setup name="SeatFlowBlacklist">
import { onMounted, reactive, ref } from 'vue'
import { listBlacklist, listViolations } from '@/api/seatflow/control'
import { fixMojibake } from '@/utils/text'

const activeTab = ref('blacklist')
const loading = ref(false)
const rows = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  studentNo: ''
})

const tabs = [
  { name: 'blacklist', label: '黑名单' },
  { name: 'violations', label: '爽约记录' }
]

function normalizeRow(row) {
  const next = { ...row }
  ;['userName', 'nickName', 'studentNo', 'reason'].forEach(field => {
    next[field] = fixMojibake(next[field])
  })
  return next
}

function switchTab(name) {
  activeTab.value = name
  query.pageNum = 1
  loadData()
}

function search() {
  query.pageNum = 1
  loadData()
}

function resetQuery() {
  query.userName = ''
  query.studentNo = ''
  search()
}

async function loadData() {
  loading.value = true
  try {
    const request = activeTab.value === 'blacklist' ? listBlacklist : listViolations
    const res = await request(query)
    rows.value = (res.rows || []).map(normalizeRow)
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
@use '@/assets/styles/seatflow.scss';
</style>
