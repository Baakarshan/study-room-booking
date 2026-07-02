# SeatFlow 开发流程

## 目录

- [协作原则](#协作原则)
- [worktree 分工](#worktree-分工)
- [公共文件](#公共文件)
- [SQL 和菜单](#sql-和菜单)
- [命名规范](#命名规范)
- [合并顺序](#合并顺序)

## 协作原则

SeatFlow 按业务域拆 worktree。每个模块尽量交付页面、接口、SQL 草案、测试说明的闭环。只按前端和后端分工，会把预约冲突、签到释放这类规则拆散，后面合并容易出错。

main 初始化由 infra 负责。main 没准备好之前，其他模块可以先写接口草案和 SQL 草案，不抢先改全局配置。

## worktree 分工

| worktree | 分支 | 负责内容 | 不负责 |
| --- | --- | --- | --- |
| `infra` | `feature/infra` | 脚手架、`ruoyi-seatflow` 模块、Docker Compose、数据库汇总 SQL、角色菜单、演示数据、部署文档 | 具体业务页面和业务规则 |
| `base-info` | `feature/base-info` | 校区、楼栋、楼层、自习室、座位、批量生成座位 | 预约冲突、签到、报表 |
| `reservation` | `feature/reservation` | 自习室筛选、座位状态、提交预约、我的预约、取消预约、冲突判断 | 批量生成座位、黑名单维护、报表 |
| `control` | `feature/control` | 签到、超时释放、爽约次数、黑名单、Quartz 任务 | 座位基础资料、报表图表 |
| `report` | `feature/report` | 热力图、日均使用率、热门时段、统计页面 | 改预约状态、改黑名单规则 |

建议命令在 main 初始化后执行：

```bash
git worktree add ../seatflow-infra -b feature/infra
git worktree add ../seatflow-base-info -b feature/base-info
git worktree add ../seatflow-reservation -b feature/reservation
git worktree add ../seatflow-control -b feature/control
git worktree add ../seatflow-report -b feature/report
```

## 公共文件

公共文件由 infra 汇总，其他模块提交草案或说明。

| 文件或目录 | 负责人 | 规则 |
| --- | --- | --- |
| 初始化 SQL | infra | 汇总各模块 SQL 草案 |
| 菜单和权限 SQL | infra | 汇总菜单、角色、按钮权限 |
| Docker Compose | infra | 只放 MySQL、Redis 等基础服务 |
| RuoYi 认证链路 | infra | 原则上不魔改 |
| `ruoyi-seatflow` 公共枚举 | infra 和相关模块 | 新增状态先更新 [spec.md](./spec.md) |

业务模块可以读公共表，但不要重写其他模块 Service。`report` 可以读预约表，不能修改预约状态；`control` 可以修改预约状态，不能改预约冲突规则。

## SQL 和菜单

各模块维护草案：

- `docs/sql-drafts/base-info.sql`
- `docs/sql-drafts/reservation.sql`
- `docs/sql-drafts/control.sql`
- `docs/sql-drafts/report.sql`

infra 合并时检查：

- 表名使用 `seatflow_` 前缀。
- 状态字段使用 `varchar` 语义码。
- 菜单权限以 `seatflow:` 开头。
- 演示数据覆盖管理员和学生。
- 预约相关索引符合 [spec.md](./spec.md)。

权限标识示例：

| 功能 | 权限标识 |
| --- | --- |
| 校区管理 | `seatflow:base:campus:list` |
| 自习室管理 | `seatflow:base:room:list` |
| 座位预约 | `seatflow:reservation:create` |
| 我的预约 | `seatflow:reservation:mine` |
| 预约签到 | `seatflow:control:checkin` |
| 黑名单管理 | `seatflow:control:blacklist:list` |
| 报表查看 | `seatflow:report:view` |

## 命名规范

| 类别 | 规范 |
| --- | --- |
| 后端包名 | `com.ruoyi.seatflow` |
| 后端模块 | `ruoyi-seatflow` |
| Controller 路径 | `/seatflow/base`、`/seatflow/reservation`、`/seatflow/control`、`/seatflow/report` |
| 前端页面 | `src/views/seatflow` |
| 前端 API | `src/api/seatflow` |
| 表名 | `seatflow_` 前缀 |
| 主键 | 业务名加 `_id` |
| 状态 | `varchar` 语义码 |
| 提交信息 | `模块: 行为`，例如 `reservation: 校验座位时间冲突` |

Service 持有事务边界，Controller 不写业务判断。Mapper XML 可以写复杂 SQL，预约冲突和报表查询要清楚，不追求层层包装。异常提示用中文，例如 `该座位在所选时间已被预约`。

## 合并顺序

1. infra 初始化脚手架、`ruoyi-seatflow`、数据库连接、角色、菜单根节点。
2. base-info 合并空间层级和座位生成。
3. reservation 合并预约主链路。
4. control 合并签到、释放、爽约、黑名单。
5. report 合并报表。
6. infra 汇总演示数据、最终 SQL、部署文档。

每次合并前，模块负责人要给出自测记录和已知限制。学生项目不需要繁琐流程，但不能让公共 SQL、菜单权限和状态枚举各写各的。
