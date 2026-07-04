-- SeatFlow report 模块 SQL 草案
-- 本模块不新增业务表，不修改预约、签到、黑名单等业务状态。
-- 报表数据实时读取 seatflow_room、seatflow_seat、seatflow_reservation、seatflow_checkin_record。

-- 1、菜单和权限
-- sql/seatflow_20260702.sql 中已有统计报表菜单：
-- menu_id = 2006，权限标识 = seatflow:report:view。
-- 本模块不追加菜单 SQL。

-- 2、依赖索引
-- seatflow_reservation: idx_seatflow_reservation_seat_time (seat_id, start_time, end_time, status)
-- seatflow_reservation: idx_seatflow_reservation_user_time (user_id, start_time, status)
-- seatflow_checkin_record: idx_seatflow_checkin_reservation (reservation_id)
-- seatflow_seat: idx_seatflow_seat_grid (room_id, row_num, col_num)
-- seatflow_room: idx_seatflow_room_floor (floor_id)

-- 3、统计口径
-- 预约数：seatflow_reservation.status in ('pending_checkin', 'in_use', 'completed')
-- 签到数：seatflow_checkin_record.status = 'active'
-- 爽约数：seatflow_reservation.status = 'no_show'
-- 使用分钟数：seatflow_reservation.status in ('in_use', 'completed')
-- 使用率分母：启用座位数 * 自习室开放分钟数 * 查询天数
-- 取消和爽约：cancelled、no_show 不计入有效使用分钟数

-- 4、后续扩展
-- 第一版不建日报汇总表。
-- 若演示数据量扩大导致实时聚合变慢，再由 infra 统一评估新增报表汇总表。
