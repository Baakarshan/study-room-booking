-- SeatFlow 基础信息域 SQL 草案；由 infra 合并进最终初始化脚本。
create table seatflow_campus (
  campus_id bigint not null auto_increment comment '校区ID', campus_name varchar(100) not null comment '校区名称',
  address varchar(255) default null, status varchar(16) not null default 'enabled',
  create_by varchar(64) default '', create_time datetime default current_timestamp, update_by varchar(64) default '', update_time datetime default null, remark varchar(500) default null,
  primary key (campus_id), unique key uk_seatflow_campus_name (campus_name)
) engine=InnoDB comment='SeatFlow校区';

create table seatflow_building (
  building_id bigint not null auto_increment, campus_id bigint not null, building_name varchar(100) not null, floor_count int default null,
  status varchar(16) not null default 'enabled', create_by varchar(64) default '', create_time datetime default current_timestamp, update_by varchar(64) default '', update_time datetime default null, remark varchar(500) default null,
  primary key (building_id), key idx_seatflow_building_campus (campus_id), unique key uk_seatflow_building_name (campus_id,building_name)
) engine=InnoDB comment='SeatFlow楼栋';

create table seatflow_floor (
  floor_id bigint not null auto_increment, building_id bigint not null, floor_number int not null, floor_name varchar(100) default null,
  status varchar(16) not null default 'enabled', create_by varchar(64) default '', create_time datetime default current_timestamp, update_by varchar(64) default '', update_time datetime default null, remark varchar(500) default null,
  primary key (floor_id), key idx_seatflow_floor_building (building_id), unique key uk_seatflow_floor_number (building_id,floor_number)
) engine=InnoDB comment='SeatFlow楼层';

create table seatflow_room (
  room_id bigint not null auto_increment, floor_id bigint not null, room_name varchar(100) not null, row_count int not null, col_count int not null, total_seats int not null default 0,
  open_time time not null, close_time time not null, status varchar(16) not null default 'enabled', create_by varchar(64) default '', create_time datetime default current_timestamp, update_by varchar(64) default '', update_time datetime default null, remark varchar(500) default null,
  primary key (room_id), key idx_seatflow_room_floor (floor_id), unique key uk_seatflow_room_name (floor_id,room_name)
) engine=InnoDB comment='SeatFlow自习室';

create table seatflow_seat (
  seat_id bigint not null auto_increment, room_id bigint not null, seat_no varchar(16) not null, row_num int not null, col_num int not null,
  status varchar(16) not null default 'enabled', create_by varchar(64) default '', create_time datetime default current_timestamp, update_by varchar(64) default '', update_time datetime default null, remark varchar(500) default null,
  primary key (seat_id), unique key uk_seatflow_seat_no (room_id,seat_no), unique key uk_seatflow_seat_position (room_id,row_num,col_num)
) engine=InnoDB comment='SeatFlow座位';

-- 菜单按钮权限建议（infra 根据最终菜单ID合并）：
-- seatflow:base:campus:{list,add,edit,remove}
-- seatflow:base:building:{list,add,edit,remove}
-- seatflow:base:floor:{list,add,edit,remove}
-- seatflow:base:room:{list,add,edit,remove}
-- seatflow:base:seat:{list,generate,edit}
