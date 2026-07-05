<template>
  <div class="app-container">
    <el-form ref="queryRef" :model="query" :inline="true">
      <el-form-item label="账号" prop="userName"><el-input v-model="query.userName" clearable placeholder="学生账号" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="学号" prop="studentNo"><el-input v-model="query.studentNo" clearable placeholder="学生学号" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="rows">
      <el-table-column label="账号" prop="userName" min-width="120" />
      <el-table-column label="姓名" prop="nickName" min-width="120" />
      <el-table-column label="学号" prop="studentNo" min-width="140" />
      <el-table-column label="爽约次数" prop="violationCount" width="100" align="center" />
      <el-table-column label="原因" prop="reason" min-width="160" />
      <el-table-column label="列入时间" min-width="180"><template #default="s">{{ parseTime(s.row.startTime) }}</template></el-table-column>
      <el-table-column label="期限" width="100"><template #default><el-tag type="danger">永久</el-tag></template></el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="getList" />
  </div>
</template>

<script setup>
import { listBlacklist } from '@/api/seatflow/control'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const rows = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, userName: undefined, studentNo: undefined })

async function getList() {
  loading.value = true
  try {
    const res = await listBlacklist(query)
    rows.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}
function handleQuery() { query.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
getList()
</script>
