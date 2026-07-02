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

当前已经完成 infra 脚手架：RuoYi 后端、RuoYi-Vue3 前端、`ruoyi-seatflow` 空业务模块、Docker Compose、SeatFlow 业务表和演示账号 SQL。业务功能还没有实现，下一步按 [开发流程](./docs/development-workflow.md) 拆 worktree。
