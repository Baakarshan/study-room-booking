<template>
  <div class="app-container seatflow-home">
    <section class="welcome">
      <div><span class="eyebrow">SEATFLOW · 课程设计</span><h1>{{ greeting }}，{{ userStore.name || '同学' }}</h1><p>{{ isAdmin ? '今天也可以从空间维护、预约总览和统计分析开始管理。' : '找一个合适的座位，把时间留给专注和成长。' }}</p><div class="hero-actions"><el-button type="primary" size="large" @click="go(primaryAction.path)">{{ primaryAction.label }}</el-button><el-button size="large" @click="go(secondaryAction.path)">{{ secondaryAction.label }}</el-button></div></div>
      <div class="illustration"><div class="desk"><span class="lamp"/><span class="book one"/><span class="book two"/><span class="cup">☕</span></div><strong>安静 · 有序 · 高效</strong></div>
    </section>

    <section class="section"><div class="section-title"><div><h2>{{ isAdmin ? '管理工作台' : '预约流程' }}</h2><p>{{ isAdmin ? '快速进入常用业务模块' : '四步完成一次自习室使用' }}</p></div></div>
      <el-row :gutter="16"><el-col v-for="(item,index) in actions" :key="item.path" :xs="24" :sm="12" :lg="6"><button class="action-card" @click="go(item.path)"><span class="step">{{ String(index+1).padStart(2,'0') }}</span><el-icon :size="26"><component :is="item.icon"/></el-icon><strong>{{ item.title }}</strong><small>{{ item.description }}</small><span class="link">进入模块 →</span></button></el-col></el-row>
    </section>

    <el-row :gutter="16" class="section">
      <el-col :xs="24" :lg="16"><el-card shadow="never" class="guide-card"><template #header><strong>项目能力一览</strong></template><div class="capabilities"><div v-for="item in capabilities" :key="item.title"><el-icon color="#409eff"><CircleCheckFilled/></el-icon><span><strong>{{ item.title }}</strong><small>{{ item.text }}</small></span></div></div></el-card></el-col>
      <el-col :xs="24" :lg="8"><el-card shadow="never" class="rules-card"><template #header><strong>使用提示</strong></template><ul><li>预约前确认日期和开放时段</li><li>开始后 15 分钟内完成签到</li><li>暂时不用时请主动取消预约</li><li>离开座位时及时结束使用</li></ul></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import useUserStore from '@/store/modules/user'
const router=useRouter(),userStore=useUserStore(),isAdmin=computed(()=>userStore.roles.includes('admin'))
const greeting=computed(()=>{const hour=new Date().getHours();return hour<11?'早上好':hour<14?'中午好':hour<18?'下午好':'晚上好'})
const studentActions=[{title:'选择空间',description:'按校区和楼层定位自习室',path:'/seatflow/reservation',icon:'LocationFilled'},{title:'预约座位',description:'查看实时占用并选择座位',path:'/seatflow/reservation',icon:'Calendar'},{title:'按时签到',description:'开始后及时确认到场',path:'/seatflow/control',icon:'CircleCheck'},{title:'专注学习',description:'查看记录或结束本次使用',path:'/seatflow/my-reservation',icon:'Reading'}]
const adminActions=[{title:'空间维护',description:'维护校区、自习室和座位',path:'/seatflow/base-info',icon:'OfficeBuilding'},{title:'预约管理',description:'查看全部学生预约状态',path:'/seatflow/reservation-manage',icon:'List'},{title:'信用管理',description:'处理爽约和黑名单记录',path:'/seatflow/blacklist',icon:'UserFilled'},{title:'统计报表',description:'分析使用率和热门时段',path:'/seatflow/report',icon:'DataAnalysis'}]
const actions=computed(()=>isAdmin.value?adminActions:studentActions),primaryAction=computed(()=>actions.value[0]),secondaryAction=computed(()=>actions.value[isAdmin.value?3:2])
const capabilities=[{title:'空间分级维护',text:'校区、楼栋、楼层、自习室和座位统一管理'},{title:'预约冲突校验',text:'实时校验用户和座位的重叠时段'},{title:'签到与信用规则',text:'超时爽约、累计次数和黑名单形成闭环'},{title:'可视化统计',text:'使用率、时段排行和座位热力图清晰呈现'}]
function go(path){router.push(path)}
</script>

<style scoped lang="scss">
.seatflow-home{max-width:1400px;margin:0 auto}.welcome{position:relative;display:flex;justify-content:space-between;align-items:center;min-height:270px;padding:38px 48px;overflow:hidden;border-radius:16px;background:linear-gradient(125deg,#eaf4ff 0%,#f0f9eb 65%,#fff 100%);h1{margin:8px 0 12px;font-size:34px;color:#263445}p{margin:0;color:#637083;font-size:16px}.eyebrow{color:var(--el-color-primary);font-weight:700;letter-spacing:1px}.hero-actions{margin-top:28px}.illustration{text-align:center;color:#547087}.desk{position:relative;width:260px;height:105px;margin-bottom:18px;border-bottom:10px solid #96b5ce}.lamp{position:absolute;left:28px;bottom:10px;width:10px;height:76px;background:#6b8ba4}.lamp:before{content:'';position:absolute;left:-18px;top:0;width:48px;height:26px;border-radius:25px 25px 5px 5px;background:#409eff}.book{position:absolute;bottom:10px;width:100px;height:17px;border-radius:3px}.book.one{right:28px;background:#67c23a}.book.two{right:38px;bottom:30px;background:#e6a23c}.cup{position:absolute;right:2px;bottom:12px;font-size:32px}}.section{margin-top:24px}.section-title h2{margin:0 0 5px;font-size:22px}.section-title p{margin:0 0 16px;color:var(--el-text-color-secondary)}.action-card{position:relative;width:100%;min-height:190px;margin-bottom:16px;padding:24px;text-align:left;border:1px solid var(--el-border-color-light);border-radius:12px;background:#fff;cursor:pointer;transition:.2s}.action-card:hover{transform:translateY(-4px);border-color:var(--el-color-primary-light-5);box-shadow:0 10px 26px #2c3e5014}.action-card .step{position:absolute;right:20px;top:16px;color:#d9e5ef;font-size:28px;font-weight:700}.action-card strong,.action-card small{display:block}.action-card strong{margin:14px 0 7px;font-size:17px}.action-card small{min-height:35px;color:var(--el-text-color-secondary)}.action-card .link{display:block;margin-top:13px;color:var(--el-color-primary);font-size:13px}.guide-card,.rules-card{margin-bottom:16px}.capabilities{display:grid;grid-template-columns:repeat(2,1fr);gap:22px}.capabilities>div{display:flex;align-items:flex-start;gap:10px}.capabilities span,.capabilities small{display:block}.capabilities small{margin-top:4px;color:var(--el-text-color-secondary)}.rules-card ul{margin:0;padding-left:20px;line-height:2.15;color:var(--el-text-color-regular)}
@media(max-width:768px){.welcome{padding:28px 24px}.illustration{display:none}.welcome h1{font-size:27px}.capabilities{grid-template-columns:1fr}}
</style>
