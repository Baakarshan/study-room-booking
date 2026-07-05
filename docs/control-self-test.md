# Control 域自测说明

## 自动校验

```bash
mvn -pl ruoyi-seatflow -am test
cd seatflow-ui && npm run build:prod
```

## 数据库联调

1. 准备一条当前时间位于 `start_time` 与 `check_deadline` 之间、状态为 `pending_checkin` 的本人预约，调用 `POST /seatflow/control/checkin`，确认预约变为 `in_use` 且只生成一条签到记录。
2. 分别验证提前签到、他人预约签到、截止时间后签到均返回中文业务错误。
3. 准备超时的 `pending_checkin` 预约，执行 Quartz 调用目标 `seatFlowControlTask.releaseExpiredReservations()`，确认预约变为 `no_show`，生成一条爽约记录，档案次数加一。
4. 对同一预约重复执行任务，确认爽约记录和次数不重复增加。
5. 为同一学生累计三次爽约，确认 `blacklist_flag = 'yes'` 且生成一条 `active` 永久黑名单；继续爽约不会重复生成黑名单。
6. 学生权限只能访问可签到列表、本人爽约与签到接口；管理员权限可分页查询全量爽约和黑名单。

## 边界

- 第一版黑名单永久有效，不提供解除接口。
- Quartz 单次最多处理 50 批、每批 200 条；遗留数据超过 10000 条时由下一次调度继续处理。
- 依赖预约创建模块正确写入 `check_deadline = start_time + 15 分钟`，control 域不改预约创建与冲突规则。
