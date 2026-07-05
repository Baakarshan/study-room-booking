# Reservation 域自测说明

## 自动检查

- 后端：`mise exec maven@3.9.16 -- mvn -pl ruoyi-seatflow -am -DskipTests package`，通过（2026-07-05）。
- 前端：`npm run build:prod`（目录 `seatflow-ui`），通过（2026-07-05）。
- Mapper XML：`xmllint --noout ruoyi-seatflow/src/main/resources/mapper/seatflow/SeatFlowReservationMapper.xml`，通过。

## 数据库集成用例

1. 学生查询空间级联和指定时间座位图，停用、待签到占用、使用中分别显示对应状态。
2. 正常预约写入 `pending_checkin`，`check_deadline = start_time + 15分钟`。
3. 同座位重叠、同用户重叠、同一预约日期超过 1 次分别返回中文业务错误。
4. 预约时间跨日、超出开放时间、开始不早于结束、座位/房间停用均拒绝。
5. 黑名单用户拒绝；取消只允许本人在开始前取消 `pending_checkin` 预约。
6. 两个并发事务抢同一座位时只有一个成功；同一用户并发抢不同座位时也只有一个成功。

说明：`cancelled` 不计入每日限制；`no_show` 计入。座位冲突仅计算 `pending_checkin`、`in_use`。
