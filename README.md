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
- [实验报告](./docs/实验报告.md)

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

课程项目业务闭环已经完成：

- 基础信息：校区、楼栋、楼层、自习室、座位维护和批量生成。
- 座位预约：空间筛选、座位状态、并发冲突校验、每日限次、我的预约和取消。
- 签到管控：15 分钟签到、手动结束、Quartz 自动完成、爽约累计和黑名单解除。
- 管理功能：预约总览、信用记录、黑名单处理和分级统计报表。
- 统计报表：热力图、使用率、热门时段、自习室排行和 ECharts 页面。

完整浏览器冒烟使用独立数据库运行：

```bash
cd seatflow-ui
npm run smoke
```

`npm run smoke` 会调用 `scripts/smoke/run.sh`，按顺序重置 `seatflow_smoke`、构建后端、启动 smoke profile 后端和前端，再执行 4 个 Playwright 串行场景。若需可视化观察浏览器，可运行：

```bash
cd seatflow-ui
npm run smoke:headed
```

脚本仅允许重置名称包含 `smoke` 或 `test` 的数据库。测试结果与真实页面截图见 [实验报告](./docs/实验报告.md)。

初始化表、演示账号、菜单权限和超时释放任务统一维护在 `sql/seatflow_20260702.sql`。模块自测记录位于 `docs/*-self-test.md`，整体验收范围见 [测试计划](./docs/test-plan.md)。
