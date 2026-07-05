<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header><span>我的预约</span></template>
      <el-form :inline="true"><el-form-item label="状态"><el-select v-model="query.status" clearable placeholder="全部" style="width:180px"><el-option v-for="item in statuses" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item></el-form>
      <el-table v-loading="loading" :data="rows">
        <el-table-column label="位置" min-width="260"><template #default="scope">{{ scope.row.campusName }} / {{ scope.row.buildingName }} / {{ scope.row.floorName }} / {{ scope.row.roomName }}</template></el-table-column>
        <el-table-column prop="seatNo" label="座位" width="90" />
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column label="状态" width="120"><template #default="scope"><el-tag :type="tagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="120" fixed="right"><template #default="scope"><el-button v-if="canCancel(scope.row)" link type="danger" @click="cancel(scope.row)">取消预约</el-button><span v-else>-</span></template></el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelReservation, listMyReservations } from '@/api/seatflow/reservation'
const statuses = [{ value:'pending_checkin',label:'待签到'},{value:'in_use',label:'使用中'},{value:'cancelled',label:'已取消'},{value:'no_show',label:'已爽约'},{value:'completed',label:'已完成'}]
const query = reactive({ pageNum:1, pageSize:10, status:undefined }), rows = ref([]), total = ref(0), loading = ref(false)
onMounted(load)
async function load(){ loading.value=true; try { const res=await listMyReservations(query); rows.value=res.rows||[]; total.value=res.total||0 } finally { loading.value=false } }
function search(){ query.pageNum=1; load() } function reset(){ query.status=undefined; search() }
function statusLabel(value){ return statuses.find(item=>item.value===value)?.label || value }
function tagType(value){ return ({pending_checkin:'warning',in_use:'primary',cancelled:'info',no_show:'danger',completed:'success'})[value] || 'info' }
function canCancel(row){ return row.status==='pending_checkin' && new Date(row.startTime).getTime()>Date.now() }
async function cancel(row){ await ElMessageBox.confirm(`确认取消 ${row.roomName} ${row.seatNo} 的预约？`,'取消预约',{type:'warning'}); await cancelReservation(row.reservationId); ElMessage.success('已取消预约'); load() }
</script>
