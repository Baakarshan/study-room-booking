# Reservation 域自测说明

## 自动检查

- 后端：`mise exec maven@3.9.16 -- mvn -pl ruoyi-seatflow -am test`，29 项通过（2026-07-10）。
- 前端：`npm run lint:seatflow` 与 `npm run build:prod`（目录 `seatflow-ui`），通过（2026-07-10）。
- Mapper XML：`xmllint --noout ruoyi-seatflow/src/main/resources/mapper/seatflow/SeatFlowReservationMapper.xml`，通过。
- 真实烟测：`npm run smoke:report`，4 个 Chrome 场景通过并生成 HTML 报告和预约流程截图。

## 数据库集成用例

1. 学生查询空间级联和指定时间座位图，停用、待签到占用、使用中分别显示对应状态。
2. 正常预约写入 `pending_checkin`，`check_deadline = start_time + 15分钟`。
3. 同座位重叠、同用户重叠、同一预约日期超过 1 次分别返回中文业务错误。
4. 预约时间跨日、超出开放时间、开始不早于结束、座位/房间停用均拒绝。
5. 黑名单用户拒绝；取消只允许本人在开始前取消 `pending_checkin` 预约。
6. 两个并发事务抢同一座位时只有一个成功；同一用户并发抢不同座位时也只有一个成功。
7. 预约页按上海时区自动给出下一个 30 分钟起点和 1 小时时长；若当天不足 1 小时则切到次日开放时间。
8. 开始、结束选项只落在自习室开放时间内，查询过去时段会被后端拒绝。
9. 学生端与管理端的状态数量独立聚合，不受当前分页影响；管理端汇总保留账号、空间和日期筛选，但忽略状态筛选。

说明：`cancelled` 不计入每日限制；`no_show` 计入。座位冲突仅计算 `pending_checkin`、`in_use`。
