# SeatFlow

SeatFlow 是基于 RuoYi-Vue `master` 后端和 RuoYi-Vue3 前端搭建的自习室预约管理平台课设脚手架。

## 目录

- [文档](#文档)
- [本地启动](#本地启动)
- [当前状态](#当前状态)

## 文档

- [项目说明](./docs/README.md)
- [需求与设计](./docs/spec.md)
- [开发流程](./docs/development-workflow.md)
- [测试计划](./docs/test-plan.md)

## 本地启动

先启动基础服务：

```bash
docker compose up -d mysql redis
```

后端启动：

```bash
mvn -pl ruoyi-admin -am spring-boot:run
```

前端启动：

```bash
cd seatflow-ui
npm install
npm run dev
```

MySQL 初始化脚本来自 `sql/ry_20260417.sql`、`sql/quartz.sql` 和 `sql/seatflow_20260702.sql`。首次启动 MySQL 容器时会自动执行。

## 当前状态

第一版业务链路已经完成：

- 基础信息：校区、楼栋、楼层、自习室、座位维护和批量生成。
- 座位预约：空间筛选、座位状态、并发冲突校验、每日限次、我的预约和取消。
- 签到管控：15 分钟签到、Quartz 超时释放、爽约累计和永久黑名单。
- 统计报表：热力图、使用率、热门时段、自习室排行和 ECharts 页面。

初始化表、演示账号、菜单权限和超时释放任务统一维护在 `sql/seatflow_20260702.sql`。模块自测记录位于 `docs/*-self-test.md`，整体验收范围见 [测试计划](./docs/test-plan.md)。
