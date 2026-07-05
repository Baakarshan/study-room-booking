<template>
  <div class="app-container">
    <el-tabs v-model="active" @tab-change="load">
      <el-tab-pane v-for="item in tabs" :key="item.key" :label="item.label" :name="item.key" />
    </el-tabs>
    <el-form :inline="true" :model="query">
      <el-form-item :label="current.nameLabel"><el-input v-model="query[current.nameKey]" clearable placeholder="请输入名称" @keyup.enter="search" /></el-form-item>
      <el-form-item label="状态"><el-select v-model="query.status" clearable style="width:120px"><el-option label="启用" value="enabled"/><el-option label="停用" value="disabled"/></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="search">查询</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button><el-button v-if="active !== 'seat'" type="primary" plain icon="Plus" @click="openForm()">新增</el-button></el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="rows">
      <el-table-column v-for="col in current.columns" :key="col.prop" :prop="col.prop" :label="col.label" :min-width="col.width || 100" />
      <el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="scope.row.status === 'enabled' ? 'success' : 'info'">{{ scope.row.status === 'enabled' ? '启用' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <template v-if="active === 'seat'">
            <el-button link type="primary" @click="toggleSeat(scope.row)">{{ scope.row.status === 'enabled' ? '停用' : '启用' }}</el-button>
          </template>
          <template v-else>
            <el-button link type="primary" icon="Edit" @click="openForm(scope.row)">修改</el-button>
            <el-button v-if="active === 'room'" link type="success" @click="generate(scope.row)">生成座位</el-button>
            <el-button link type="danger" icon="Delete" @click="remove(scope.row)">删除</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />

    <el-dialog v-model="visible" :title="form[current.idKey] ? '修改' + current.label : '新增' + current.label" width="560px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item v-if="current.parent" :label="current.parent.label" :prop="current.parent.key">
          <el-select v-model="form[current.parent.key]" filterable style="width:100%"><el-option v-for="o in parentOptions" :key="o[current.parent.id]" :label="o[current.parent.name]" :value="o[current.parent.id]"/></el-select>
        </el-form-item>
        <el-form-item :label="current.nameLabel" :prop="current.nameKey"><el-input v-model="form[current.nameKey]" /></el-form-item>
        <template v-if="active === 'campus'"><el-form-item label="地址"><el-input v-model="form.address" /></el-form-item></template>
        <template v-if="active === 'building'"><el-form-item label="楼层数" prop="floorCount"><el-input-number v-model="form.floorCount" :min="1" /></el-form-item></template>
        <template v-if="active === 'floor'"><el-form-item label="楼层编号" prop="floorNumber"><el-input-number v-model="form.floorNumber" :min="-9" /></el-form-item></template>
        <template v-if="active === 'room'">
          <el-form-item label="座位布局" required><el-input-number v-model="form.rowCount" :min="1" :max="26" /> 行 × <el-input-number v-model="form.colCount" :min="1" :max="99" /> 列</el-form-item>
          <el-form-item label="开放时段" required><el-time-picker v-model="roomTimes" is-range value-format="HH:mm:ss" range-separator="至" start-placeholder="开放时间" end-placeholder="关闭时间" /></el-form-item>
        </template>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio value="enabled">启用</el-radio><el-radio value="disabled">停用</el-radio></el-radio-group></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup name="SeatFlowBaseInfo">
import { computed, getCurrentInstance, reactive, ref } from 'vue'
import { addBase, delBase, generateSeats, getBase, listBase, listOptions, updateBase, updateSeatStatus } from '@/api/seatflow/base'

const { proxy } = getCurrentInstance()
const tabs = [
  { key:'campus', label:'校区', idKey:'campusId', nameKey:'campusName', nameLabel:'校区名称', columns:[{prop:'campusName',label:'校区名称'},{prop:'address',label:'地址',width:180}] },
  { key:'building', label:'楼栋', idKey:'buildingId', nameKey:'buildingName', nameLabel:'楼栋名称', parent:{type:'campus',key:'campusId',id:'campusId',name:'campusName',label:'所属校区'}, columns:[{prop:'campusName',label:'所属校区'},{prop:'buildingName',label:'楼栋名称'},{prop:'floorCount',label:'楼层数'}] },
  { key:'floor', label:'楼层', idKey:'floorId', nameKey:'floorName', nameLabel:'楼层名称', parent:{type:'building',key:'buildingId',id:'buildingId',name:'buildingName',label:'所属楼栋'}, columns:[{prop:'buildingName',label:'所属楼栋'},{prop:'floorNumber',label:'楼层编号'},{prop:'floorName',label:'楼层名称'}] },
  { key:'room', label:'自习室', idKey:'roomId', nameKey:'roomName', nameLabel:'自习室名称', parent:{type:'floor',key:'floorId',id:'floorId',name:'floorName',label:'所属楼层'}, columns:[{prop:'floorName',label:'所属楼层'},{prop:'roomName',label:'自习室名称'},{prop:'rowCount',label:'行数'},{prop:'colCount',label:'列数'},{prop:'totalSeats',label:'座位数'},{prop:'openTime',label:'开放时间'},{prop:'closeTime',label:'关闭时间'}] },
  { key:'seat', label:'座位', idKey:'seatId', nameKey:'seatNo', nameLabel:'座位编号', columns:[{prop:'roomName',label:'自习室'},{prop:'seatNo',label:'座位编号'},{prop:'rowNum',label:'行号'},{prop:'colNum',label:'列号'}] }
]
const active = ref('campus'), current = computed(() => tabs.find(t => t.key === active.value))
const loading = ref(false), rows = ref([]), total = ref(0), visible = ref(false), parentOptions = ref([]), roomTimes = ref([])
const query = reactive({ pageNum:1, pageSize:10 }), form = reactive({})
const rules = computed(() => ({ [current.value.nameKey]:[{ required:true,message:`${current.value.nameLabel}不能为空`,trigger:'blur' }], ...(current.value.parent ? {[current.value.parent.key]:[{required:true,message:`${current.value.parent.label}不能为空`,trigger:'change'}]} : {}) }))

function load() { loading.value=true; listBase(active.value, query).then(r => { rows.value=r.rows; total.value=r.total }).finally(()=>loading.value=false) }
function search() { query.pageNum=1; load() }
function resetQuery() { Object.keys(query).forEach(k => { if (!['pageNum','pageSize'].includes(k)) delete query[k] }); query.pageNum=1; load() }
async function openForm(row) {
  Object.keys(form).forEach(k=>delete form[k]); Object.assign(form,{status:'enabled'})
  if (current.value.parent) parentOptions.value=(await listOptions(current.value.parent.type)).data
  if (row) Object.assign(form,(await getBase(active.value,row[current.value.idKey])).data)
  roomTimes.value=active.value==='room' && form.openTime ? [form.openTime,form.closeTime] : []
  visible.value=true
}
function submit() { proxy.$refs.formRef.validate(async ok => { if (!ok) return; if(active.value==='room'){ form.openTime=roomTimes.value?.[0]; form.closeTime=roomTimes.value?.[1] } const fn=form[current.value.idKey]?updateBase:addBase; await fn(active.value,form); proxy.$modal.msgSuccess('保存成功'); visible.value=false; load() }) }
function remove(row) { proxy.$modal.confirm(`确认删除“${row[current.value.nameKey]}”吗？`).then(()=>delBase(active.value,row[current.value.idKey])).then(()=>{proxy.$modal.msgSuccess('删除成功');load()}).catch(()=>{}) }
function generate(row) { proxy.$modal.confirm(`将按 ${row.rowCount}×${row.colCount} 生成座位，已有座位时会拒绝。是否继续？`).then(()=>generateSeats(row.roomId)).then(()=>{proxy.$modal.msgSuccess('座位生成成功');active.value='seat';load()}).catch(()=>{}) }
function toggleSeat(row) { updateSeatStatus(row.seatId,row.status==='enabled'?'disabled':'enabled').then(()=>{proxy.$modal.msgSuccess('状态更新成功');load()}) }
load()
</script>
