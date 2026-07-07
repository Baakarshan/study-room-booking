<template>
  <div class="app-container sf-page home-page">
    <section class="home-hero">
      <div class="hero-copy">
        <p class="eyebrow">SeatFlow</p>
        <h1>欢迎回来</h1>
        <p class="summary">今天的预约、签到和基础数据都可以从这里进入。先看当前需要处理的事项，再进入对应页面操作。</p>
      </div>
      <div class="today-text">
        <span>{{ today.weekday }}</span>
        <strong>{{ today.date }}</strong>
      </div>
    </section>

    <nav class="home-actions" aria-label="常用入口">
      <router-link v-for="item in quickActions" :key="item.path" :to="item.path" class="action-pill">
        <svg-icon :icon-class="item.icon" />
        <span>{{ item.title }}</span>
      </router-link>
    </nav>

    <section class="home-board">
      <div class="board-main">
        <div class="section-title">
          <h2>今天可以先看这些</h2>
          <span>日常处理</span>
        </div>
        <div class="work-list">
          <router-link v-for="item in workItems" :key="item.title" :to="item.path" class="work-row">
            <span class="work-index">{{ item.index }}</span>
            <span class="work-copy">
              <strong>{{ item.title }}</strong>
              <small>{{ item.desc }}</small>
            </span>
          </router-link>
        </div>
      </div>

      <aside class="board-side">
        <div class="section-title">
          <h2>系统范围</h2>
          <span>SeatFlow</span>
        </div>
        <p v-for="item in notes" :key="item">{{ item }}</p>
      </aside>
    </section>
  </div>
</template>

<script setup name="Index">
const now = new Date()
const today = {
  date: new Intl.DateTimeFormat('zh-CN', { month: 'long', day: 'numeric' }).format(now),
  weekday: new Intl.DateTimeFormat('zh-CN', { weekday: 'long' }).format(now)
}

const quickActions = [
  { title: '座位预约', path: '/seatflow/reservation', icon: 'date' },
  { title: '我的预约', path: '/seatflow/my-reservation', icon: 'list' },
  { title: '基础信息', path: '/seatflow/base-info', icon: 'tree' },
  { title: '统计报表', path: '/seatflow/report', icon: 'chart' }
]

const workItems = [
  { index: '01', title: '确认可预约空间', path: '/seatflow/base-info', desc: '校区、楼栋、楼层、自习室和座位数据保持可用。' },
  { index: '02', title: '查看今日预约', path: '/seatflow/control', desc: '处理待签到、超时释放和取消后的座位状态。' },
  { index: '03', title: '维护异常名单', path: '/seatflow/blacklist', desc: '核对多次爽约学生，必要时更新限制状态。' }
]

const notes = [
  '基础信息决定预约页面的筛选和座位展示。',
  '学生侧完成预约、签到、取消和历史记录查看。',
  '管理员侧维护座位数据，并查看签到、黑名单和报表。'
]
</script>

<style scoped lang="scss">
@use '@/assets/styles/seatflow.scss';

.home-page {
  padding-top: 10px;
}

.home-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 32px;
  min-height: 176px;
  padding: 24px 4px 34px;
}

.hero-copy {
  max-width: 680px;
}

.eyebrow {
  margin: 0 0 14px;
  color: #C9B59C;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0;
}

h1 {
  margin: 0;
  color: #2C2621;
  font-size: 38px;
  font-weight: 650;
  line-height: 1.2;
  letter-spacing: 0;
}

.summary {
  max-width: 640px;
  margin: 18px 0 0;
  color: #6B6560;
  font-size: 15px;
  line-height: 1.9;
}

.today-text {
  text-align: right;
  padding-bottom: 4px;

  span {
    display: block;
    margin-bottom: 8px;
    color: #8A827A;
    font-size: 13px;
  }

  strong {
    color: #2C2621;
    font-size: 22px;
    font-weight: 700;
  }
}

.home-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 34px;
  padding: 4px;
  width: fit-content;
}

.action-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 38px;
  padding: 0 18px;
  border-radius: 999px;
  color: #6B6560;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;

  &:hover {
    color: #2C2621;
    background: #EFE9E3;
    transform: translateY(-1px);
  }

  .svg-icon {
    font-size: 15px;
  }
}

.home-board {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(300px, 0.65fr);
  gap: 22px;
  align-items: start;
}

.board-main,
.board-side {
  background: #F9F8F6;
  border-radius: 10px;
  padding: 22px 24px;
}

.section-title {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;

  h2 {
    margin: 0;
    color: #2C2621;
    font-size: 17px;
    font-weight: 650;
    letter-spacing: 0;
  }

  span {
    color: #8A827A;
    font-size: 13px;
  }
}

.work-list {
  display: grid;
  gap: 2px;
}

.work-row {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  align-items: center;
  gap: 16px;
  min-height: 74px;
  padding: 10px 4px;
  color: inherit;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.2s ease;

  &:hover {
    background: #FFFFFF;
    padding-left: 12px;
  }
}

.work-index {
  color: #C9B59C;
  font-size: 14px;
  font-weight: 700;
}

.work-copy {
  min-width: 0;

  strong {
    display: block;
    color: #2C2621;
    font-size: 15px;
    font-weight: 650;
  }

  small {
    display: block;
    margin-top: 7px;
    color: #6B6560;
    font-size: 13px;
    line-height: 1.6;
  }
}

.board-side {
  p {
    margin: 0;
    color: #6B6560;
    font-size: 14px;
    line-height: 1.85;
  }

  p + p {
    margin-top: 14px;
  }
}

@media (max-width: 900px) {
  .home-hero,
  .home-board {
    grid-template-columns: 1fr;
  }

  .home-hero {
    display: grid;
    align-items: start;
  }

  .today-text {
    text-align: left;
  }
}

@media (max-width: 640px) {
  .home-hero {
    min-height: auto;
    padding-top: 6px;
  }

  h1 {
    font-size: 30px;
  }

  .home-actions {
    width: 100%;
  }

  .action-pill {
    flex: 1 1 calc(50% - 10px);
    justify-content: center;
  }

  .board-main,
  .board-side {
    padding: 18px;
  }

  .work-row {
    grid-template-columns: 34px minmax(0, 1fr);
  }
}
</style>
