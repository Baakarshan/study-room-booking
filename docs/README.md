# SeatFlow 项目说明

## 目录

- [定位](#定位)
- [技术线](#技术线)
- [项目结构](#项目结构)
- [开发边界](#开发边界)
- [上游依据](#上游依据)

## 定位

SeatFlow 是智能校园自习室预约管理平台，面向 Java 学生课设。它解决的主线问题很明确：座位被占、空位不清楚、限时预约、超时释放、爽约限制、统计看板。

项目追求稳定交付、方便分工、方便 AI Agent 并行开发。前后端分离，基于成熟脚手架改造，业务放在 SeatFlow 自己的模块里。微服务、网关、消息队列、对象存储、多数据库、复杂缓存都不进入第一版。

## 技术线

当前默认采用 RuoYi 最新上游线：后端基于 RuoYi-Vue `master`，前端基于 RuoYi-Vue3。这个选择贴近上游维护方向，但比传统 `springboot2` 加 Vue 2 的课设组合更新，开发时要少改框架本体，依赖版本以官方文件为准。

| 项 | 当前选择 | 说明 |
| --- | --- | --- |
| SeatFlow 版本 | `0.1.0-SNAPSHOT` | 项目自己的版本 |
| RuoYi 来源 | `3.9.2` | 只记录脚手架来源 |
| Maven 父工程 | `com.ruoyi:ruoyi:3.9.2` | 默认保留，避免全局改名 |
| 后端业务模块 | `ruoyi-seatflow` | 新增独立模块 |
| 后端业务包 | `com.ruoyi.seatflow` | 放 SeatFlow 业务代码 |
| 前端页面目录 | `src/views/seatflow` | RuoYi-Vue3 页面 |
| 前端 API 目录 | `src/api/seatflow` | RuoYi-Vue3 请求封装 |
| 数据库 | MySQL 8 | 预约、签到、报表都落库 |
| Redis | RuoYi 框架依赖 | 登录、验证码、token、权限缓存 |
| 定时任务 | RuoYi Quartz | 超时未签到释放座位 |

Redis 不做 SeatFlow 业务缓存。预约冲突、座位状态、黑名单、报表优先靠 MySQL 表、事务、索引、Quartz 和 SQL 聚合解决。为了去掉 Redis 而改 RuoYi 认证链路，收益不高，风险很大。

## 项目结构

文档保持少而全：

- [spec.md](./spec.md)：业务需求、流程、数据库表、状态枚举、接口边界。
- [development-workflow.md](./development-workflow.md)：worktree 分工、SQL 菜单合并、提交和合并规则。
- [test-plan.md](./test-plan.md)：主流程、边界条件、Playwright、部署冒烟。
- [study-room-booking-er.mmd](./study-room-booking-er.mmd)：ER 草图。
- [题目.png](./题目.png)：原始题目截图。

实现时也保持这个思路：文档一处说清，代码一处落位。重复写同一条规则，后面很容易改漏。

## 开发边界

SeatFlow 复用 RuoYi 的用户、角色、菜单和权限体系。演示账号默认是 `admin`、`student01`、`student02`，管理员和学生权限必须分开。

业务表统一使用 `seatflow_` 前缀。RuoYi 自带表继续使用上游命名，不为了项目身份去改框架表。后续如果老师要求 Maven 坐标必须改为 `com.seatflow:seatflow`，再做一次集中改名，不让各模块各改各的。

## 上游依据

- [RuoYi-Vue README](https://github.com/yangzongzhuan/RuoYi-Vue/blob/master/README.md)
- [RuoYi-Vue master pom.xml](https://github.com/yangzongzhuan/RuoYi-Vue/blob/master/pom.xml)
- [RuoYi-Vue3 README](https://github.com/yangzongzhuan/RuoYi-Vue3/blob/master/README.md)
- [RuoYi-Vue3 package.json](https://github.com/yangzongzhuan/RuoYi-Vue3/blob/master/package.json)
- [Spring Boot System Requirements](https://docs.spring.io/spring-boot/system-requirements.html)
- [MySQL InnoDB Transaction Isolation](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-isolation-levels.html)
- [MySQL Locking Reads](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking-reads.html)
- [Docker Compose Docs](https://docs.docker.com/compose/)
- [Maven Wrapper Docs](https://maven.apache.org/wrapper/)
