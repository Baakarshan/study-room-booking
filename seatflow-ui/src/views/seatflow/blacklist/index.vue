<template>
  <div class="app-container blacklist-page">
    <div class="page-heading"><div><h2>信用管理</h2><p>查看黑名单和爽约明细，解除后学生可重新预约。</p></div></div>
    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-change="handleQuery">
        <el-tab-pane label="黑名单" name="blacklist" /><el-tab-pane label="爽约记录" name="violations" />
      </el-tabs>
      <el-form ref="queryRef" :model="query" :inline="true">
        <el-form-item label="账号" prop="userName"><el-input v-model="query.userName" clearable placeholder="学生账号" @keyup.enter="handleQuery" /></el-form-item>
        <el-form-item label="学号" prop="studentNo"><el-input v-model="query.studentNo" clearable placeholder="学生学号" @keyup.enter="handleQuery" /></el-form-item>
        <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="rows" :empty-text="activeTab === 'blacklist' ? '暂无黑名单学生' : '暂无爽约记录'">
        <el-table-column label="学生" min-width="170"><template #default="s"><strong>{{ s.row.nickName || '-' }}</strong><div class="subtext">{{ s.row.userName }} · {{ s.row.studentNo || '未填写学号' }}</div></template></el-table-column>
        <template v-if="activeTab === 'blacklist'">
          <el-table-column label="爽约次数" prop="violationCount" width="100" align="center" /><el-table-column label="原因" prop="reason" min-width="180" /><el-table-column label="列入时间" min-width="180"><template #default="s">{{ parseTime(s.row.startTime) }}</template></el-table-column>
          <el-table-column label="状态" width="100"><template #default><el-tag type="danger">限制预约</el-tag></template></el-table-column>
          <el-table-column label="操作" width="110" fixed="right"><template #default="s"><el-button link type="primary" @click="release(s.row)">解除限制</el-button></template></el-table-column>
        </template>
        <template v-else>
          <el-table-column prop="reservationId" label="预约编号" width="120" /><el-table-column prop="reason" label="爽约原因" min-width="220" /><el-table-column label="爽约时间" min-width="180"><template #default="s">{{ parseTime(s.row.violationTime) }}</template></el-table-column>
        </template>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup name="SeatFlowBlacklist">
import { getCurrentInstance, reactive, ref } from 'vue'
import { listBlacklist, listViolations, releaseBlacklist } from '@/api/seatflow/control'
const { proxy } = getCurrentInstance()
const loading = ref(false), rows = ref([]), total = ref(0), activeTab = ref('blacklist')
const query = reactive({ pageNum: 1, pageSize: 10, userName: undefined, studentNo: undefined })
async function getList(){ loading.value=true; try { const res=await (activeTab.value==='blacklist'?listBlacklist(query):listViolations(query)); rows.value=res.rows||[]; total.value=res.total||0 } finally { loading.value=false } }
function handleQuery(){ query.pageNum=1; getList() } function resetQuery(){ proxy.resetForm('queryRef'); handleQuery() }
async function release(row){ await proxy.$modal.confirm(`确认解除 ${row.nickName || row.userName} 的预约限制？爽约计数将清零，历史记录仍保留。`); await releaseBlacklist(row.blacklistId || row.id); proxy.$modal.msgSuccess('黑名单已解除'); getList() }
getList()
</script>

<style scoped lang="scss">
.page-heading{margin-bottom:16px;h2{margin:0 0 6px;font-size:22px}p{margin:0;color:var(--el-text-color-secondary)}}.subtext{margin-top:4px;color:var(--el-text-color-secondary);font-size:12px}
</style>
