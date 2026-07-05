-- SeatFlow reservation 域 SQL 草案（由 infra 汇总进初始化脚本）
create table if not exists seatflow_reservation (
  reservation_id bigint not null auto_increment comment '预约ID',
  user_id bigint not null comment '用户ID', room_id bigint not null comment '自习室ID',
  seat_id bigint not null comment '座位ID', start_time datetime not null comment '开始时间',
  end_time datetime not null comment '结束时间', check_deadline datetime not null comment '签到截止时间',
  status varchar(32) not null comment 'pending_checkin/in_use/cancelled/no_show/completed',
  cancel_time datetime null, create_by varchar(64) default '', create_time datetime null,
  update_by varchar(64) default '', update_time datetime null, remark varchar(500) null,
  primary key (reservation_id),
  key idx_seatflow_reservation_seat_time (seat_id,start_time,end_time,status),
  key idx_seatflow_reservation_user_time (user_id,start_time,status),
  key idx_seatflow_reservation_deadline (status,check_deadline)
) engine=InnoDB comment='SeatFlow预约表';

-- 菜单由 infra 分配最终 ID；学生角色需要以下两个权限：
-- seatflow:reservation:create  座位筛选、座位状态、提交预约
-- seatflow:reservation:mine    我的预约、取消预约

-- 并发策略：提交事务先 FOR UPDATE 锁 seatflow_seat，再锁 sys_user；
-- 分别串行化“同一座位”和“同一用户选择不同座位”的并发请求。
-- 重叠条件：existing.start_time < new_end_time and existing.end_time > new_start_time。
