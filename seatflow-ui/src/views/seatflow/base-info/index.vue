<template>
  <div class="app-container base-info-page">
    <!-- Pill Tabs -->
    <div class="sf-tabs">
      <button
        v-for="tab in tabList"
        :key="tab.name"
        :class="['sf-tab-item', { active: activeTab === tab.name }]"
        @click="activeTab = tab.name"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Campus Tab -->
    <div v-show="activeTab === 'campus'" class="tab-pane">
      <div class="table-header">
        <el-button type="primary" icon="Plus" @click="handleOpenAdd('campus')">新增校区</el-button>
      </div>
      <el-table :data="campusList" style="width: 100%" v-loading="loading">
        <el-table-column prop="campusId" label="校区ID" width="100" />
        <el-table-column prop="campusName" label="校区名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'danger'">
              {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" icon="Edit" @click="handleOpenEdit('campus', scope.row)">编辑</el-button>
            <el-button size="small" type="danger" icon="Delete" @click="handleDelete('campus', scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Building Tab -->
    <div v-show="activeTab === 'building'" class="tab-pane">
      <div class="table-header">
        <el-button type="primary" icon="Plus" @click="handleOpenAdd('building')">新增楼栋</el-button>
      </div>
      <el-table :data="buildingList" style="width: 100%" v-loading="loading">
        <el-table-column prop="buildingId" label="楼栋ID" width="100" />
        <el-table-column prop="buildingName" label="楼栋名称" />
        <el-table-column label="所属校区">
          <template #default="scope">
            {{ getCampusName(scope.row.campusId) }}
          </template>
        </el-table-column>
        <el-table-column prop="floorCount" label="总层数" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'danger'">
              {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" icon="Edit" @click="handleOpenEdit('building', scope.row)">编辑</el-button>
            <el-button size="small" type="danger" icon="Delete" @click="handleDelete('building', scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Floor Tab -->
    <div v-show="activeTab === 'floor'" class="tab-pane">
      <div class="table-header">
        <el-button type="primary" icon="Plus" @click="handleOpenAdd('floor')">新增楼层</el-button>
      </div>
      <el-table :data="floorList" style="width: 100%" v-loading="loading">
        <el-table-column prop="floorId" label="楼层ID" width="100" />
        <el-table-column prop="floorName" label="楼层名称" />
        <el-table-column prop="floorNumber" label="楼层数/编号" width="150" />
        <el-table-column label="所属楼栋">
          <template #default="scope">
            {{ getBuildingName(scope.row.buildingId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'danger'">
              {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" icon="Edit" @click="handleOpenEdit('floor', scope.row)">编辑</el-button>
            <el-button size="small" type="danger" icon="Delete" @click="handleDelete('floor', scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Room Tab -->
    <div v-show="activeTab === 'room'" class="tab-pane">
      <div class="table-header">
        <el-button type="primary" icon="Plus" @click="handleOpenAdd('room')">新增自习室</el-button>
      </div>
      <el-table :data="roomList" style="width: 100%" v-loading="loading">
        <el-table-column prop="roomId" label="自习室ID" width="100" />
        <el-table-column prop="roomName" label="自习室名称" />
        <el-table-column label="所属楼层">
          <template #default="scope">
            {{ getFloorName(scope.row.floorId) }}
          </template>
        </el-table-column>
        <el-table-column label="座位布局" width="150">
          <template #default="scope">
            {{ scope.row.rowCount }} 行 × {{ scope.row.colCount }} 列
          </template>
        </el-table-column>
        <el-table-column prop="totalSeats" label="总座位数" width="120" />
        <el-table-column label="开放时间" width="200">
          <template #default="scope">
            {{ scope.row.openTime }} - {{ scope.row.closeTime }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'danger'">
              {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" icon="Edit" @click="handleOpenEdit('room', scope.row)">编辑</el-button>
            <el-button size="small" type="danger" icon="Delete" @click="handleDelete('room', scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Seat Tab -->
    <div v-show="activeTab === 'seat'" class="tab-pane">
      <div class="seat-control-header">
        <div class="filter-item">
          <span class="label">选择自习室:</span>
          <el-select v-model="selectedRoomId" placeholder="请选择自习室" style="width: 260px" @change="fetchSeats">
            <el-option v-for="r in roomList" :key="r.roomId" :label="r.roomName" :value="r.roomId" />
          </el-select>
        </div>
        <div class="actions-group">
          <el-button v-if="selectedRoomId && seatList.length === 0" type="warning" icon="Grid" @click="handleBatchGenerate">
            批量生成座位
          </el-button>
          <el-button v-if="selectedRoomId" type="primary" icon="Plus" @click="handleOpenAdd('seat')">
            添加单个座位
          </el-button>
        </div>
      </div>

      <div v-if="selectedRoomId" class="seat-grid-container" v-loading="loading">
        <div class="legend">
          <div class="legend-item"><span class="box enabled"></span>启用中</div>
          <div class="legend-item"><span class="box disabled"></span>已停用</div>
          <span class="tip">(点击座位可快速切换 启用 / 停用 状态)</span>
        </div>

        <div v-if="seatList.length > 0" class="seating-grid" :style="{ gridTemplateColumns: `repeat(${selectedRoom?.colCount || 6}, 1fr)` }">
          <div
            v-for="seat in sortedSeats"
            :key="seat.seatId"
            class="seat-box"
            :class="seat.status"
            @click="toggleSeatStatus(seat)"
          >
            <div class="seat-no">{{ seat.seatNo }}</div>
            <div class="seat-coords">({{ seat.rowNum }}, {{ seat.colNum }})</div>
          </div>
        </div>

        <el-empty v-else description="该自习室暂无座位，请点击上方“批量生成座位”进行初始化。" />
      </div>
      <el-empty v-else description="请先选择需要管理座位的自习室。" />
    </div>

    <!-- Unified CRUD Dialog -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="550px" append-to-body class="sf-dialog">
      <el-form :model="form" ref="formRef" :rules="formRules" label-width="110px">
        <!-- Campus Fields -->
        <template v-if="dialogType === 'campus'">
          <el-form-item label="校区名称" prop="campusName">
            <el-input v-model="form.campusName" placeholder="请输入校区名称" />
          </el-form-item>
          <el-form-item label="地址" prop="address">
            <el-input v-model="form.address" placeholder="请输入校区地址" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio label="enabled">启用</el-radio>
              <el-radio label="disabled">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注内容" />
          </el-form-item>
        </template>

        <!-- Building Fields -->
        <template v-if="dialogType === 'building'">
          <el-form-item label="所属校区" prop="campusId">
            <el-select v-model="form.campusId" placeholder="请选择校区" style="width: 100%">
              <el-option v-for="c in campusList" :key="c.campusId" :label="c.campusName" :value="c.campusId" />
            </el-select>
          </el-form-item>
          <el-form-item label="楼栋名称" prop="buildingName">
            <el-input v-model="form.buildingName" placeholder="请输入楼栋名称" />
          </el-form-item>
          <el-form-item label="楼层总数" prop="floorCount">
            <el-input-number v-model="form.floorCount" :min="1" :max="50" style="width: 100%" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio label="enabled">启用</el-radio>
              <el-radio label="disabled">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
          </el-form-item>
        </template>

        <!-- Floor Fields -->
        <template v-if="dialogType === 'floor'">
          <el-form-item label="所属楼栋" prop="buildingId">
            <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%">
              <el-option v-for="b in buildingList" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
            </el-select>
          </el-form-item>
          <el-form-item label="楼层名称" prop="floorName">
            <el-input v-model="form.floorName" placeholder="请输入楼层名称 (如: 三层)" />
          </el-form-item>
          <el-form-item label="楼层编号" prop="floorNumber">
            <el-input-number v-model="form.floorNumber" style="width: 100%" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio label="enabled">启用</el-radio>
              <el-radio label="disabled">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
          </el-form-item>
        </template>

        <!-- Room Fields -->
        <template v-if="dialogType === 'room'">
          <el-form-item label="所属楼层" prop="floorId">
            <el-select v-model="form.floorId" placeholder="请选择楼层" style="width: 100%">
              <el-option v-for="f in floorList" :key="f.floorId" :label="f.floorName" :value="f.floorId" />
            </el-select>
          </el-form-item>
          <el-form-item label="自习室名称" prop="roomName">
            <el-input v-model="form.roomName" placeholder="请输入自习室名称" />
          </el-form-item>
          <el-form-item label="排布行数" prop="rowCount">
            <el-input-number v-model="form.rowCount" :min="1" :max="20" style="width: 100%" />
          </el-form-item>
          <el-form-item label="排布列数" prop="colCount">
            <el-input-number v-model="form.colCount" :min="1" :max="20" style="width: 100%" />
          </el-form-item>
          <el-form-item label="开放时间" prop="openTime">
            <el-time-picker v-model="form.openTime" format="HH:mm:ss" value-format="HH:mm:ss" placeholder="请选择开放时间" style="width: 100%" />
          </el-form-item>
          <el-form-item label="关闭时间" prop="closeTime">
            <el-time-picker v-model="form.closeTime" format="HH:mm:ss" value-format="HH:mm:ss" placeholder="请选择关闭时间" style="width: 100%" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio label="enabled">启用</el-radio>
              <el-radio label="disabled">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
          </el-form-item>
        </template>

        <!-- Seat Fields -->
        <template v-if="dialogType === 'seat'">
          <el-form-item label="自习室" prop="roomId">
            <el-select v-model="form.roomId" disabled placeholder="自习室" style="width: 100%">
              <el-option v-for="r in roomList" :key="r.roomId" :label="r.roomName" :value="r.roomId" />
            </el-select>
          </el-form-item>
          <el-form-item label="座位编号" prop="seatNo">
            <el-input v-model="form.seatNo" placeholder="请输入座位编号 (如: A01)" />
          </el-form-item>
          <el-form-item label="行号" prop="rowNum">
            <el-input-number v-model="form.rowNum" :min="1" :max="20" style="width: 100%" />
          </el-form-item>
          <el-form-item label="列号" prop="colNum">
            <el-input-number v-model="form.colNum" :min="1" :max="20" style="width: 100%" />
          </el-form-item>
        </template>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="BaseInfo">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { fixMojibake } from '@/utils/text'

const activeTab = ref('campus')
const loading = ref(false)

const campusList = ref([])
const buildingList = ref([])
const floorList = ref([])
const roomList = ref([])
const seatList = ref([])

const selectedRoomId = ref(null)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogType = ref('')
const isEdit = ref(false)

const form = ref({})
const formRef = ref(null)

const tabList = [
  { name: 'campus', label: '校区管理' },
  { name: 'building', label: '楼栋管理' },
  { name: 'floor', label: '楼层管理' },
  { name: 'room', label: '自习室管理' },
  { name: 'seat', label: '座位管理' }
]

const selectedRoom = computed(() => {
  return roomList.value.find(r => r.roomId === selectedRoomId.value)
})

const sortedSeats = computed(() => {
  return [...seatList.value].sort((a, b) => {
    if (a.rowNum !== b.rowNum) {
      return a.rowNum - b.rowNum
    }
    return a.colNum - b.colNum
  })
})

const formRules = {
  campusName: [{ required: true, message: '请输入校区名称', trigger: 'blur' }],
  buildingName: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  campusId: [{ required: true, message: '请选择所属校区', trigger: 'change' }],
  buildingId: [{ required: true, message: '请选择所属楼栋', trigger: 'change' }],
  floorName: [{ required: true, message: '请输入楼层名称', trigger: 'blur' }],
  floorNumber: [{ required: true, message: '请输入楼层编号', trigger: 'blur' }],
  floorId: [{ required: true, message: '请选择所属楼层', trigger: 'change' }],
  roomName: [{ required: true, message: '请输入自习室名称', trigger: 'blur' }],
  openTime: [{ required: true, message: '请选择开放时间', trigger: 'change' }],
  closeTime: [{ required: true, message: '请选择关闭时间', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择自习室', trigger: 'change' }],
  seatNo: [{ required: true, message: '请输入座位编号', trigger: 'blur' }],
  rowNum: [{ required: true, message: '请输入座位行号', trigger: 'blur' }],
  colNum: [{ required: true, message: '请输入座位列号', trigger: 'blur' }]
}

const textFields = ['campusName', 'address', 'buildingName', 'floorName', 'roomName', 'seatNo', 'remark']

function normalizeTextFields(row) {
  if (!row) {
    return row
  }
  const normalized = { ...row }
  textFields.forEach(field => {
    normalized[field] = fixMojibake(normalized[field])
  })
  return normalized
}

function normalizeRows(rows) {
  return Array.isArray(rows) ? rows.map(normalizeTextFields) : []
}

function getCampusName(campusId) {
  const c = campusList.value.find(x => x.campusId === campusId)
  return c ? c.campusName : '未知校区'
}

function getBuildingName(buildingId) {
  const b = buildingList.value.find(x => x.buildingId === buildingId)
  return b ? b.buildingName : '未知楼栋'
}

function getFloorName(floorId) {
  const f = floorList.value.find(x => x.floorId === floorId)
  return f ? f.floorName : '未知楼层'
}

async function fetchCampuses() {
  const res = await request.get('/seatflow/base/campus/list')
  campusList.value = normalizeRows(res.rows)
}

async function fetchBuildings() {
  const res = await request.get('/seatflow/base/building/list')
  buildingList.value = normalizeRows(res.rows)
}

async function fetchFloors() {
  const res = await request.get('/seatflow/base/floor/list')
  floorList.value = normalizeRows(res.rows)
}

async function fetchRooms() {
  const res = await request.get('/seatflow/base/room/list')
  roomList.value = normalizeRows(res.rows)
}

async function fetchSeats() {
  if (!selectedRoomId.value) return
  loading.value = true
  try {
    const res = await request.get('/seatflow/base/seat/list', { params: { roomId: selectedRoomId.value } })
    seatList.value = normalizeRows(res.rows)
  } finally {
    loading.value = false
  }
}

async function loadAllData() {
  loading.value = true
  try {
    await Promise.all([
      fetchCampuses(),
      fetchBuildings(),
      fetchFloors(),
      fetchRooms()
    ])
  } catch (err) {
    ElMessage.error('加载基础数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAllData()
})

function handleOpenAdd(type) {
  dialogType.value = type
  isEdit.value = false
  dialogTitle.value = `新增${getDialogTypeLabel(type)}`
  dialogVisible.value = true
  form.value = {
    status: 'enabled',
    rowCount: 5,
    colCount: 5,
    openTime: '08:00:00',
    closeTime: '22:00:00',
    roomId: selectedRoomId.value,
    rowNum: 1,
    colNum: 1
  }
}

function handleOpenEdit(type, row) {
  dialogType.value = type
  isEdit.value = true
  dialogTitle.value = `编辑${getDialogTypeLabel(type)}`
  dialogVisible.value = true
  form.value = normalizeTextFields(row)
}

function getDialogTypeLabel(type) {
  switch (type) {
    case 'campus': return '校区'
    case 'building': return '楼栋'
    case 'floor': return '楼层'
    case 'room': return '自习室'
    case 'seat': return '座位'
  }
}

async function submitForm() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    const method = isEdit.value ? 'put' : 'post'
    const url = `/seatflow/base/${dialogType.value}`

    try {
      await request[method](url, form.value)
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadAllData()
      if (dialogType.value === 'seat') {
        fetchSeats()
      }
    } catch (err) {
      // Axios interceptor will show the error message
    }
  })
}

async function handleDelete(type, row) {
  let idField = `${type}Id`
  let id = row[idField]

  ElMessageBox.confirm(`确定删除名称为“${row[type + 'Name'] || row.floorName || row.roomName}”的记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/seatflow/base/${type}/${id}`)
      ElMessage.success('删除成功')
      loadAllData()
    } catch (err) {}
  }).catch(() => {})
}

async function handleBatchGenerate() {
  if (!selectedRoomId.value) return
  ElMessageBox.confirm(`确定为该自习室自动批量生成 ${selectedRoom.value.rowCount * selectedRoom.value.colCount} 个网格座位吗？`, '提示', {
    confirmButtonText: '生成',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    loading.value = true
    try {
      await request.post(`/seatflow/base/room/${selectedRoomId.value}/seats/generate`)
      ElMessage.success('批量生成座位成功')
      fetchSeats()
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

async function toggleSeatStatus(seat) {
  const newStatus = seat.status === 'enabled' ? 'disabled' : 'enabled'
  try {
    await request.put(`/seatflow/base/seat/${seat.seatId}/status/${newStatus}`)
    seat.status = newStatus
    ElMessage.success(`座位 ${seat.seatNo} 已${newStatus === 'enabled' ? '启用' : '停用'}`)
  } catch (err) {}
}
</script>

<style scoped lang="scss">
.base-info-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* Pill Tabs */
.sf-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 32px;
  padding: 4px;
  width: fit-content;
}

.sf-tab-item {
  padding: 8px 18px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: #6B6560;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;

  &:hover {
    color: #2C2621;
    background: #EFE9E3;
  }

  &.active {
    background: #2C2621;
    color: #ffffff;
    font-weight: 600;
  }
}

/* Tab pane transition */
.tab-pane {
  animation: fadeIn 0.25s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}

.table-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

/* Custom buttons */
:deep(.el-button--primary) {
  background-color: #C9B59C !important;
  border-color: #C9B59C !important;
  color: #ffffff !important;
  border-radius: 999px;
  font-weight: 500;
  padding: 8px 16px;
  height: 36px;
  transition: all 0.2s ease;

  &:hover {
    background-color: #b5a08a !important;
    border-color: #b5a08a !important;
    transform: translateY(-1px);
  }
}

:deep(.el-button--danger) {
  background-color: #fef0f0 !important;
  border-color: #fde2e2 !important;
  color: #f56c6c !important;
  border-radius: 999px;
  height: 36px;
  transition: all 0.2s ease;

  &:hover {
    background-color: #f56c6c !important;
    border-color: #f56c6c !important;
    color: #ffffff !important;
    transform: translateY(-1px);
  }
}

:deep(.el-button--warning) {
  background-color: #fdf6ec !important;
  border-color: #f5dab1 !important;
  color: #e6a23c !important;
  border-radius: 999px;
  height: 36px;
  transition: all 0.2s ease;

  &:hover {
    background-color: #e6a23c !important;
    border-color: #e6a23c !important;
    color: #ffffff !important;
    transform: translateY(-1px);
  }
}

:deep(.el-button--default) {
  border-radius: 999px;
  height: 36px;
  border-color: #D9CFC7;
  color: #2C2621;

  &:hover {
    color: #C9B59C;
    border-color: #C9B59C;
    background-color: #F9F8F6;
  }
}

/* Custom Table style */
:deep(.el-table) {
  --el-table-border-color: transparent;
  --el-table-header-bg-color: #F9F8F6;
  color: #2C2621;
  font-size: 14px;
  border-radius: 0;
  overflow: hidden;
  border: none;

  th.el-table__cell {
    font-weight: 600;
    color: #2C2621;
    height: 48px;
    background-color: #F9F8F6 !important;
  }

  td.el-table__cell {
    height: 48px;
    border-bottom-color: #D9CFC7 !important;
  }

  tr {
    transition: background-color 0.2s ease;
  }

  tr:hover > td.el-table__cell {
    background-color: #F9F8F6 !important;
  }

  .el-table__inner-wrapper::before {
    display: none;
  }
}

/* Tags */
:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
  border: none;
}

:deep(.el-tag--success) {
  background-color: #f0fdf4;
  color: #15803d;
}

:deep(.el-tag--danger) {
  background-color: #fef2f2;
  color: #b91c1c;
}

/* Input styles */
:deep(.el-input__wrapper), :deep(.el-textarea__inner), :deep(.el-select .el-input__wrapper) {
  box-shadow: 0 0 0 1px #D9CFC7 inset !important;
  border-radius: 10px;
  transition: all 0.2s ease;

  &:hover {
    box-shadow: 0 0 0 1px #C9B59C inset !important;
  }

  &.is-focus {
    box-shadow: 0 0 0 2px #C9B59C inset !important;
  }
}

/* Dialog styling */
:deep(.sf-dialog .el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: none;

  .el-dialog__header {
    background-color: #F9F8F6;
    margin-right: 0;
    padding: 20px 28px;
    border-bottom: none;

    .el-dialog__title {
      color: #2C2621;
      font-weight: 600;
      font-size: 16px;
    }
  }

  .el-dialog__body {
    padding: 8px 28px 16px;
  }

  .el-dialog__footer {
    padding: 16px 28px;
    border-top: none;
    background-color: #F9F8F6;
  }
}

.seat-control-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  background-color: #F9F8F6;
  padding: 16px 20px;
  border-radius: 10px;

  .filter-item {
    display: flex;
    align-items: center;
    gap: 12px;

    .label {
      font-weight: 600;
      color: #2C2621;
      font-size: 14px;
    }
  }

  .actions-group {
    display: flex;
    gap: 10px;
  }
}

.seat-grid-container {
  margin-top: 4px;

  .legend {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 20px;
    margin-bottom: 20px;
    font-size: 13px;
    color: #2C2621;
    padding: 10px 0;

    .legend-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 500;
    }

    .box {
      width: 14px;
      height: 14px;
      border-radius: 4px;

      &.enabled {
        background-color: #ffffff;
        border: 2px solid #C9B59C;
      }

      &.disabled {
        background-color: #f4f4f5;
        border: 1px dashed #c0c4cc;
      }
    }

    .tip {
      font-size: 12px;
      color: #8A827A;
    }
  }
}

.seating-grid {
  display: grid;
  gap: 12px;
  max-width: 900px;
  background-color: #F9F8F6;
  border-radius: 12px;
  padding: 24px;
}

.seat-box {
  aspect-ratio: 1;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  border: 1px solid transparent;

  &.enabled {
    background-color: #ffffff;
    border: 2px solid #C9B59C;
    color: #C9B59C;

    &:hover {
      background-color: #C9B59C;
      color: #ffffff;
      transform: translateY(-2px);
    }
  }

  &.disabled {
    background-color: #F9F8F6;
    border: 1px dashed #D9CFC7;
    color: #a0aec0;

    &:hover {
      background-color: #fca5a5;
      border-color: #ef4444;
      color: #ffffff;
      transform: translateY(-2px);
    }
  }

  .seat-no {
    font-size: 15px;
    font-weight: 700;
    letter-spacing: -0.5px;
  }

  .seat-coords {
    font-size: 10px;
    opacity: 0.85;
    margin-top: 3px;
    font-family: monospace;
  }
}
</style>
