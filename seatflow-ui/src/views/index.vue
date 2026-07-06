<template>
  <div class="app-container home-page">
    <section class="hero-section">
      <p class="eyebrow">SeatFlow</p>
      <h1>{{ pageTitle }}</h1>
      <p class="summary">{{ pageSummary }}</p>

      <div class="hero-stats">
        <div v-for="item in stats" :key="item.label" class="stat-item">
          <span class="stat-value">{{ item.value }}</span>
          <span class="stat-label">{{ item.label }}</span>
        </div>
      </div>
    </section>

    <section class="modules-section">
      <div v-for="item in modules" :key="item.title" class="module-item">
        <div class="module-header">
          <svg-icon :icon-class="item.icon" class="module-icon" />
          <span class="module-title">{{ item.title }}</span>
        </div>
        <p class="module-desc">{{ item.desc }}</p>
      </div>
    </section>
  </div>
</template>

<script setup name="Index">
const pageTitle = '自习室预约管理平台'
const pageSummary = '围绕座位建模、预约、签到、爽约管控和统计报表，完成校园自习室的日常预约闭环。'

const stats = [
  { value: 6, label: '业务页面' },
  { value: 2, label: '角色视图' },
  { value: 15, label: '分钟签到窗口' }
]

const modules = [
  { title: '基础信息', icon: 'tree', desc: '维护校区、楼栋、楼层、自习室和座位数据。' },
  { title: '座位预约', icon: 'date', desc: '学生按空间和时间筛选座位，提交预约。' },
  { title: '我的预约', icon: 'list', desc: '查看预约记录，处理取消和签到入口。' },
  { title: '签到管控', icon: 'validCode', desc: '处理签到、超时释放和爽约状态。' },
  { title: '黑名单管理', icon: 'peoples', desc: '查看爽约累计后进入限制名单的学生。' },
  { title: '统计报表', icon: 'chart', desc: '查看热力图、使用率、热门时段和排行。' }
]
</script>

<style scoped lang="scss">
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}

.hero-section {
  margin-bottom: 48px;

  .eyebrow {
    margin: 0 0 12px;
    color: #C9B59C;
    font-weight: 700;
    font-size: 13px;
    letter-spacing: 1px;
    text-transform: uppercase;
  }

  h1 {
    margin: 0;
    font-size: 36px;
    line-height: 1.2;
    color: #2C2621;
    font-weight: 600;
    letter-spacing: -0.5px;
  }

  .summary {
    max-width: 640px;
    margin: 16px 0 0;
    color: #6B6560;
    line-height: 1.7;
    font-size: 16px;
  }

  .hero-stats {
    display: flex;
    gap: 40px;
    margin-top: 32px;
  }

  .stat-item {
    display: flex;
    flex-direction: column;
    gap: 6px;

    .stat-value {
      font-size: 32px;
      font-weight: 700;
      color: #2C2621;
      line-height: 1;
    }

    .stat-label {
      color: #8A827A;
      font-size: 14px;
    }
  }
}

.modules-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
  padding-top: 32px;
  border-top: 1px solid #EFE9E3;
}

.module-item {
  .module-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;

    .module-icon {
      font-size: 20px;
      color: #C9B59C;
    }

    .module-title {
      font-weight: 600;
      font-size: 15px;
      color: #2C2621;
    }
  }

  .module-desc {
    margin: 0;
    color: #6B6560;
    line-height: 1.7;
    font-size: 14px;
  }
}

@media (max-width: 900px) {
  .modules-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 560px) {
  .hero-section {
    h1 {
      font-size: 28px;
    }

    .hero-stats {
      gap: 24px;
    }
  }

  .modules-section {
    grid-template-columns: 1fr;
  }
}
</style>
