-- ----------------------------
-- SeatFlow 增量初始化脚本
-- 先执行 RuoYi 原始 SQL 和 quartz.sql，再执行本文件。
-- ----------------------------

-- ----------------------------
-- 1、学生档案表
-- ----------------------------
drop table if exists seatflow_user_profile;
create table seatflow_user_profile (
  profile_id        bigint(20)      not null auto_increment    comment '档案ID',
  user_id           bigint(20)      not null                   comment '用户ID',
  student_no        varchar(32)     not null                   comment '学号',
  violation_count   int             default 0                  comment '爽约次数',
  blacklist_flag    varchar(16)     default 'no'               comment '黑名单标记',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (profile_id),
  unique key uk_seatflow_profile_user (user_id),
  unique key uk_seatflow_profile_student (student_no)
) engine=innodb comment = 'SeatFlow学生档案表';

-- ----------------------------
-- 2、空间基础信息表
-- ----------------------------
drop table if exists seatflow_campus;
create table seatflow_campus (
  campus_id      bigint(20)    not null auto_increment comment '校区ID',
  campus_name    varchar(64)   not null                comment '校区名称',
  address        varchar(255)  default ''              comment '地址',
  status         varchar(16)   default 'enabled'       comment '状态',
  create_by      varchar(64)   default ''              comment '创建者',
  create_time    datetime                              comment '创建时间',
  update_by      varchar(64)   default ''              comment '更新者',
  update_time    datetime                              comment '更新时间',
  remark         varchar(500)  default null            comment '备注',
  primary key (campus_id)
) engine=innodb comment = 'SeatFlow校区表';

drop table if exists seatflow_building;
create table seatflow_building (
  building_id     bigint(20)    not null auto_increment comment '楼栋ID',
  campus_id       bigint(20)    not null                comment '校区ID',
  building_name   varchar(64)   not null                comment '楼栋名称',
  floor_count     int           default 0               comment '楼层数',
  status          varchar(16)   default 'enabled'       comment '状态',
  create_by       varchar(64)   default ''              comment '创建者',
  create_time     datetime                              comment '创建时间',
  update_by       varchar(64)   default ''              comment '更新者',
  update_time     datetime                              comment '更新时间',
  remark          varchar(500)  default null            comment '备注',
  primary key (building_id),
  key idx_seatflow_building_campus (campus_id)
) engine=innodb comment = 'SeatFlow楼栋表';

drop table if exists seatflow_floor;
create table seatflow_floor (
  floor_id      bigint(20)    not null auto_increment comment '楼层ID',
  building_id   bigint(20)    not null                comment '楼栋ID',
  floor_number  int           not null                comment '楼层编号',
  floor_name    varchar(64)   not null                comment '楼层名称',
  status        varchar(16)   default 'enabled'       comment '状态',
  create_by     varchar(64)   default ''              comment '创建者',
  create_time   datetime                              comment '创建时间',
  update_by     varchar(64)   default ''              comment '更新者',
  update_time   datetime                              comment '更新时间',
  remark        varchar(500)  default null            comment '备注',
  primary key (floor_id),
  key idx_seatflow_floor_building (building_id)
) engine=innodb comment = 'SeatFlow楼层表';

drop table if exists seatflow_room;
create table seatflow_room (
  room_id       bigint(20)    not null auto_increment comment '自习室ID',
  floor_id      bigint(20)    not null                comment '楼层ID',
  room_name     varchar(64)   not null                comment '自习室名称',
  row_count     int           default 0               comment '座位行数',
  col_count     int           default 0               comment '座位列数',
  total_seats   int           default 0               comment '座位总数',
  open_time     time          not null                comment '开放时间',
  close_time    time          not null                comment '关闭时间',
  status        varchar(16)   default 'enabled'       comment '状态',
  create_by     varchar(64)   default ''              comment '创建者',
  create_time   datetime                              comment '创建时间',
  update_by     varchar(64)   default ''              comment '更新者',
  update_time   datetime                              comment '更新时间',
  remark        varchar(500)  default null            comment '备注',
  primary key (room_id),
  key idx_seatflow_room_floor (floor_id)
) engine=innodb comment = 'SeatFlow自习室表';

drop table if exists seatflow_seat;
create table seatflow_seat (
  seat_id      bigint(20)   not null auto_increment comment '座位ID',
  room_id      bigint(20)   not null                comment '自习室ID',
  seat_no      varchar(32)  not null                comment '座位编号',
  row_num      int          not null                comment '行号',
  col_num      int          not null                comment '列号',
  status       varchar(16)  default 'enabled'       comment '状态',
  create_by    varchar(64)  default ''              comment '创建者',
  create_time  datetime                             comment '创建时间',
  update_by    varchar(64)  default ''              comment '更新者',
  update_time  datetime                             comment '更新时间',
  remark       varchar(500) default null            comment '备注',
  primary key (seat_id),
  unique key uk_seatflow_seat_no (room_id, seat_no),
  key idx_seatflow_seat_grid (room_id, row_num, col_num)
) engine=innodb comment = 'SeatFlow座位表';

-- ----------------------------
-- 3、预约与管控表
-- ----------------------------
drop table if exists seatflow_reservation;
create table seatflow_reservation (
  reservation_id  bigint(20)   not null auto_increment comment '预约ID',
  user_id         bigint(20)   not null                comment '用户ID',
  room_id         bigint(20)   not null                comment '自习室ID',
  seat_id         bigint(20)   not null                comment '座位ID',
  start_time      datetime     not null                comment '开始时间',
  end_time        datetime     not null                comment '结束时间',
  check_deadline  datetime     not null                comment '签到截止时间',
  status          varchar(32)  not null                comment '预约状态',
  cancel_time     datetime                             comment '取消时间',
  create_by       varchar(64)  default ''              comment '创建者',
  create_time     datetime                             comment '创建时间',
  update_by       varchar(64)  default ''              comment '更新者',
  update_time     datetime                             comment '更新时间',
  remark          varchar(500) default null            comment '备注',
  primary key (reservation_id),
  key idx_seatflow_reservation_seat_time (seat_id, start_time, end_time, status),
  key idx_seatflow_reservation_user_time (user_id, start_time, status),
  key idx_seatflow_reservation_deadline (status, check_deadline)
) engine=innodb comment = 'SeatFlow预约表';

drop table if exists seatflow_checkin_record;
create table seatflow_checkin_record (
  checkin_id      bigint(20)   not null auto_increment comment '签到ID',
  reservation_id  bigint(20)   not null                comment '预约ID',
  user_id         bigint(20)   not null                comment '用户ID',
  checkin_time    datetime     not null                comment '签到时间',
  status          varchar(16)  default 'active'        comment '状态',
  create_by       varchar(64)  default ''              comment '创建者',
  create_time     datetime                             comment '创建时间',
  update_by       varchar(64)  default ''              comment '更新者',
  update_time     datetime                             comment '更新时间',
  remark          varchar(500) default null            comment '备注',
  primary key (checkin_id),
  key idx_seatflow_checkin_reservation (reservation_id),
  key idx_seatflow_checkin_user (user_id)
) engine=innodb comment = 'SeatFlow签到记录表';

drop table if exists seatflow_violation_record;
create table seatflow_violation_record (
  violation_id    bigint(20)   not null auto_increment comment '爽约ID',
  reservation_id  bigint(20)   not null                comment '预约ID',
  user_id         bigint(20)   not null                comment '用户ID',
  reason          varchar(255) default ''              comment '原因',
  violation_time  datetime     not null                comment '爽约时间',
  status          varchar(16)  default 'active'        comment '状态',
  create_by       varchar(64)  default ''              comment '创建者',
  create_time     datetime                             comment '创建时间',
  update_by       varchar(64)  default ''              comment '更新者',
  update_time     datetime                             comment '更新时间',
  remark          varchar(500) default null            comment '备注',
  primary key (violation_id),
  key idx_seatflow_violation_user (user_id),
  key idx_seatflow_violation_reservation (reservation_id)
) engine=innodb comment = 'SeatFlow爽约记录表';

drop table if exists seatflow_blacklist;
create table seatflow_blacklist (
  blacklist_id   bigint(20)   not null auto_increment comment '黑名单ID',
  user_id        bigint(20)   not null                comment '用户ID',
  violation_id   bigint(20)   default null            comment '触发爽约ID',
  reason         varchar(255) default ''              comment '原因',
  start_time     datetime     not null                comment '开始时间',
  end_time       datetime     default null            comment '结束时间',
  status         varchar(16)  default 'active'        comment '状态',
  create_by      varchar(64)  default ''              comment '创建者',
  create_time    datetime                             comment '创建时间',
  update_by      varchar(64)  default ''              comment '更新者',
  update_time    datetime                             comment '更新时间',
  remark         varchar(500) default null            comment '备注',
  primary key (blacklist_id),
  key idx_seatflow_blacklist_user (user_id, status)
) engine=innodb comment = 'SeatFlow黑名单表';

-- ----------------------------
-- 4、角色、用户、菜单
-- ----------------------------
delete from sys_role_menu where role_id = 3 or menu_id between 2000 and 2006;
delete from sys_user_role where user_id in (10, 11) or role_id = 3;
delete from sys_menu where menu_id between 2000 and 2006;
delete from sys_user where user_id in (10, 11);
delete from sys_role where role_id = 3;

insert into sys_role values('3', '学生', 'student', 3, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, 'SeatFlow学生角色');

insert into sys_user values(10, 105, 'student01', '学生一', '00', 'student01@example.com', '13800000001', '2', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, 'SeatFlow演示学生');
insert into sys_user values(11, 105, 'student02', '学生二', '00', 'student02@example.com', '13800000002', '2', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, 'SeatFlow演示学生');
insert into sys_user_role values ('10', '3');
insert into sys_user_role values ('11', '3');
insert into seatflow_user_profile values(1, 10, 'S2026001', 0, 'no', 'admin', sysdate(), '', null, '演示学生');
insert into seatflow_user_profile values(2, 11, 'S2026002', 0, 'no', 'admin', sysdate(), '', null, '演示学生');

insert into sys_menu values('2000', 'SeatFlow', '0', '5', 'seatflow', null, '', '', 1, 0, 'M', '0', '0', '', 'education', 'admin', sysdate(), '', null, 'SeatFlow目录');
insert into sys_menu values('2001', '基础信息', '2000', '1', 'base-info', 'seatflow/base-info/index', '', '', 1, 0, 'C', '0', '0', 'seatflow:base:list', 'tree', 'admin', sysdate(), '', null, '基础信息菜单');
insert into sys_menu values('2002', '座位预约', '2000', '2', 'reservation', 'seatflow/reservation/index', '', '', 1, 0, 'C', '0', '0', 'seatflow:reservation:create', 'date', 'admin', sysdate(), '', null, '座位预约菜单');
insert into sys_menu values('2003', '我的预约', '2000', '3', 'my-reservation', 'seatflow/my-reservation/index', '', '', 1, 0, 'C', '0', '0', 'seatflow:reservation:mine', 'list', 'admin', sysdate(), '', null, '我的预约菜单');
insert into sys_menu values('2004', '签到管控', '2000', '4', 'control', 'seatflow/control/index', '', '', 1, 0, 'C', '0', '0', 'seatflow:control:checkin', 'validCode', 'admin', sysdate(), '', null, '签到管控菜单');
insert into sys_menu values('2005', '黑名单管理', '2000', '5', 'blacklist', 'seatflow/blacklist/index', '', '', 1, 0, 'C', '0', '0', 'seatflow:control:blacklist:list', 'peoples', 'admin', sysdate(), '', null, '黑名单管理菜单');
insert into sys_menu values('2006', '统计报表', '2000', '6', 'report', 'seatflow/report/index', '', '', 1, 0, 'C', '0', '0', 'seatflow:report:view', 'chart', 'admin', sysdate(), '', null, '统计报表菜单');

insert into sys_role_menu values ('3', '2000');
insert into sys_role_menu values ('3', '2002');
insert into sys_role_menu values ('3', '2003');
insert into sys_role_menu values ('3', '2004');

-- ----------------------------
-- 5、演示空间数据
-- ----------------------------
insert into seatflow_campus values(1, '主校区', '教学楼片区', 'enabled', 'admin', sysdate(), '', null, '演示校区');
insert into seatflow_campus values(2, '图书馆校区', '图书馆片区', 'enabled', 'admin', sysdate(), '', null, '演示校区');

insert into seatflow_building values(1, 1, '第一教学楼', 5, 'enabled', 'admin', sysdate(), '', null, '');
insert into seatflow_building values(2, 2, '图书馆', 6, 'enabled', 'admin', sysdate(), '', null, '');

insert into seatflow_floor values(1, 1, 2, '二层', 'enabled', 'admin', sysdate(), '', null, '');
insert into seatflow_floor values(2, 2, 3, '三层', 'enabled', 'admin', sysdate(), '', null, '');

insert into seatflow_room values(1, 1, '教学楼 201 自习室', 4, 5, 20, '08:00:00', '22:00:00', 'enabled', 'admin', sysdate(), '', null, '');
insert into seatflow_room values(2, 2, '图书馆 301 自习室', 5, 6, 30, '08:00:00', '23:00:00', 'enabled', 'admin', sysdate(), '', null, '');

insert into seatflow_seat (room_id, seat_no, row_num, col_num, status, create_by, create_time)
select 1, concat(r.row_label, lpad(c.col_num, 2, '0')), r.row_num, c.col_num, 'enabled', 'admin', sysdate()
from (
  select 1 row_num, 'A' row_label union all
  select 2, 'B' union all
  select 3, 'C' union all
  select 4, 'D'
) r
cross join (select 1 col_num union all select 2 union all select 3 union all select 4 union all select 5) c;

insert into seatflow_seat (room_id, seat_no, row_num, col_num, status, create_by, create_time)
select 2, concat(r.row_label, lpad(c.col_num, 2, '0')), r.row_num, c.col_num, 'enabled', 'admin', sysdate()
from (
  select 1 row_num, 'A' row_label union all
  select 2, 'B' union all
  select 3, 'C' union all
  select 4, 'D' union all
  select 5, 'E'
) r
cross join (select 1 col_num union all select 2 union all select 3 union all select 4 union all select 5 union all select 6) c;
