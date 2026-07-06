-- SeatFlow 课程完整增强增量脚本。
-- 适用于已经执行过 seatflow_20260702.sql 的数据库，只执行一次。

alter table seatflow_campus
  add unique key uk_seatflow_campus_name (campus_name);

alter table seatflow_building
  add unique key uk_seatflow_building_name (campus_id, building_name);

alter table seatflow_floor
  add unique key uk_seatflow_floor_number (building_id, floor_number);

alter table seatflow_room
  add unique key uk_seatflow_room_name (floor_id, room_name);

alter table seatflow_reservation
  add column complete_time datetime null comment '完成时间' after cancel_time,
  add key idx_seatflow_reservation_completion (status, end_time);

alter table seatflow_blacklist
  drop index uk_seatflow_blacklist_user_status,
  add unique key uk_seatflow_blacklist_user (user_id);

delete from sys_role_menu where menu_id in (2007, 2008);
delete from sys_menu where menu_id in (2007, 2008);

insert into sys_menu values
  ('2007', '预约管理', '2000', '5', 'reservation-manage',
   'seatflow/reservation-manage/index', '', '', 1, 0, 'C', '0', '0',
   'seatflow:reservation:list', 'list', 'admin', sysdate(), '', null, '管理员预约总览'),
  ('2008', '解除黑名单', '2005', '1', '#', '', '', '', 1, 0, 'F', '0', '0',
   'seatflow:control:blacklist:edit', '#', 'admin', sysdate(), '', null, '解除学生黑名单');

update sys_menu set order_num = 6 where menu_id = 2005;
update sys_menu set order_num = 7 where menu_id = 2006;

update sys_job
set remark = '每分钟释放超时预约、累计爽约并完成到期使用'
where job_name = 'SeatFlow超时未签到释放' and job_group = 'SEATFLOW';
