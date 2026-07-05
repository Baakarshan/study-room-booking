-- SeatFlow control 域 SQL 草案（MySQL 8）
-- 由 infra 合并到总初始化 SQL；预约表和用户档案表由上游模块创建，本文件不修改预约冲突规则。

create table if not exists seatflow_checkin_record (
    checkin_id bigint not null auto_increment comment '签到ID',
    reservation_id bigint not null comment '预约ID',
    user_id bigint not null comment '用户ID',
    checkin_time datetime not null comment '签到时间',
    status varchar(20) not null default 'active' comment '记录状态',
    create_time datetime default current_timestamp,
    update_time datetime null,
    remark varchar(500) null,
    primary key (checkin_id),
    unique key uk_checkin_reservation (reservation_id),
    key idx_checkin_user_time (user_id, checkin_time)
) engine=InnoDB comment='SeatFlow签到记录';

create table if not exists seatflow_violation_record (
    violation_id bigint not null auto_increment comment '爽约ID',
    reservation_id bigint not null comment '预约ID',
    user_id bigint not null comment '用户ID',
    reason varchar(255) not null comment '爽约原因',
    violation_time datetime not null comment '爽约时间',
    status varchar(20) not null default 'active' comment '记录状态',
    create_time datetime default current_timestamp,
    update_time datetime null,
    remark varchar(500) null,
    primary key (violation_id),
    unique key uk_violation_reservation (reservation_id),
    key idx_violation_user_time (user_id, violation_time)
) engine=InnoDB comment='SeatFlow爽约记录';

create table if not exists seatflow_blacklist (
    blacklist_id bigint not null auto_increment comment '黑名单ID',
    user_id bigint not null comment '用户ID',
    violation_id bigint not null comment '触发黑名单的爽约ID',
    reason varchar(255) not null comment '列入原因',
    start_time datetime not null comment '生效时间',
    end_time datetime null comment '结束时间，永久黑名单为空',
    status varchar(20) not null default 'active' comment '记录状态',
    create_time datetime default current_timestamp,
    update_time datetime null,
    remark varchar(500) null,
    primary key (blacklist_id),
    unique key uk_blacklist_user_status (user_id, status),
    key idx_blacklist_user_status (user_id, status)
) engine=InnoDB comment='SeatFlow黑名单';

-- 超时扫描必须有该索引；若 reservation 草案已创建同名/等价索引，infra 仅保留一份。
create index idx_reservation_status_deadline
    on seatflow_reservation (status, check_deadline);

-- Quartz 建议每分钟执行一次。job_id/menu_id/role_id 由 infra 按汇总 SQL 的号段调整。
insert into sys_job
    (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status,
     create_by, create_time, remark)
values
    ('SeatFlow超时未签到释放', 'SEATFLOW', 'seatFlowControlTask.releaseExpiredReservations()',
     '0 * * * * ?', '2', '1', '0', 'admin', sysdate(), '每分钟释放超时预约并累计爽约');

-- 权限草案：学生 seatflow:control:checkin；管理员 seatflow:control:blacklist:list。
-- 对应页面：seatflow/control/index（学生签到），seatflow/blacklist/index（管理员黑名单）。
