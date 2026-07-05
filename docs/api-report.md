# SeatFlow 报表接口文档

## 目录

- [公共说明](#公共说明)
- [查询参数](#查询参数)
- [GET /seatflow/report/summary](#get-seatflowreportsummary)
- [GET /seatflow/report/heatmap](#get-seatflowreportheatmap)
- [GET /seatflow/report/usage-rate](#get-seatflowreportusage-rate)
- [GET /seatflow/report/popular-slots](#get-seatflowreportpopular-slots)
- [GET /seatflow/report/room-ranking](#get-seatflowreportroom-ranking)
- [统计口径](#统计口径)
- [错误响应](#错误响应)
- [自测记录](#自测记录)

## 公共说明

报表接口用于管理员查看 SeatFlow 统计数据。接口只读业务表，不修改预约、签到、爽约、黑名单等业务状态。

项目依据：

- [需求文档的报表口径](./spec.md#报表口径)
- [需求文档的接口边界](./spec.md#接口边界)
- [测试计划的边界条件](./test-plan.md#边界条件)
- [开发流程的分工说明](./development-workflow.md#worktree-分工)

| 项目 | 说明 |
| --- | --- |
| 接口前缀 | `/seatflow/report` |
| 请求方式 | `GET` |
| 权限标识 | `seatflow:report:view` |
| 返回结构 | RuoYi `AjaxResult` |
| 时间格式 | `yyyy-MM-dd HH:mm:ss` |
| 最大时间范围 | 31 天 |

统一成功响应：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

## 查询参数

所有接口都支持空间筛选。`beginTime` 和 `endTime` 必填，其余参数选填。

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `beginTime` | string | 是 | 查询开始时间，例如 `2026-07-01 00:00:00` |
| `endTime` | string | 是 | 查询结束时间，例如 `2026-07-07 23:59:59` |
| `campusId` | number | 否 | 校区 ID |
| `buildingId` | number | 否 | 楼栋 ID |
| `floorId` | number | 否 | 楼层 ID |
| `roomId` | number | 否 | 自习室 ID |
| `metric` | string | 否 | 指标，按具体接口限制取值 |
| `slotType` | string | 否 | 热门时段粒度，第一版只支持 `hour` |

时间范围采用预约时间交集过滤：

```sql
r.start_time < endTime
and r.end_time > beginTime
```

## GET /seatflow/report/summary

返回报表总览卡片数据，兼容前端已有 `getUsageSummary(query)` 调用。

请求示例：

```http
GET /seatflow/report/summary?beginTime=2026-07-01%2000:00:00&endTime=2026-07-07%2023:59:59&roomId=1
```

响应示例：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "reservationCount": 32,
    "checkinCount": 26,
    "noShowCount": 3,
    "activeSeatCount": 20,
    "usageMinutes": 4680,
    "usageRate": 0.2786
  }
}
```

字段说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `reservationCount` | number | 预约数 |
| `checkinCount` | number | 签到数 |
| `noShowCount` | number | 爽约数 |
| `activeSeatCount` | number | 启用座位数 |
| `usageMinutes` | number | 有效使用分钟数 |
| `usageRate` | number | 使用率，保留 4 位小数 |

## GET /seatflow/report/heatmap

返回座位热力图数据。无预约的启用座位也会返回，便于前端绘制完整座位图。

`metric` 支持：

| 值 | 说明 |
| --- | --- |
| `reservation_count` | 按预约次数排序，默认值 |
| `usage_minutes` | 按使用分钟数排序 |

请求示例：

```http
GET /seatflow/report/heatmap?beginTime=2026-07-01%2000:00:00&endTime=2026-07-07%2023:59:59&roomId=1&metric=usage_minutes
```

响应示例：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "seatId": 1,
      "roomId": 1,
      "roomName": "教学楼 201 自习室",
      "seatNo": "A01",
      "rowNum": 1,
      "colNum": 1,
      "reservationCount": 4,
      "usageMinutes": 360
    }
  ]
}
```

## GET /seatflow/report/usage-rate

按日期和自习室返回使用率。第一版只返回查询范围内有有效使用记录的日期和自习室。

请求示例：

```http
GET /seatflow/report/usage-rate?beginTime=2026-07-01%2000:00:00&endTime=2026-07-07%2023:59:59
```

响应示例：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "reportDate": "2026-07-01",
      "roomId": 1,
      "roomName": "教学楼 201 自习室",
      "usageMinutes": 720,
      "availableMinutes": 16800,
      "usageRate": 0.0429
    }
  ]
}
```

## GET /seatflow/report/popular-slots

返回热门时段。第一版固定按小时统计，时段格式为 `HH:00`。

请求示例：

```http
GET /seatflow/report/popular-slots?beginTime=2026-07-01%2000:00:00&endTime=2026-07-07%2023:59:59&slotType=hour
```

响应示例：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "slotLabel": "09:00",
      "reservationCount": 12,
      "checkinCount": 10
    }
  ]
}
```

## GET /seatflow/report/room-ranking

返回自习室排行。

`metric` 支持：

| 值 | 说明 |
| --- | --- |
| `reservation_count` | 按预约次数排序，默认值 |
| `checkin_count` | 按签到次数排序 |
| `usage_rate` | 按使用率排序 |

请求示例：

```http
GET /seatflow/report/room-ranking?beginTime=2026-07-01%2000:00:00&endTime=2026-07-07%2023:59:59&metric=usage_rate
```

响应示例：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "roomId": 1,
      "roomName": "教学楼 201 自习室",
      "reservationCount": 32,
      "checkinCount": 26,
      "usageMinutes": 4680,
      "usageRate": 0.2786
    }
  ]
}
```

## 统计口径

| 指标 | 口径 |
| --- | --- |
| 预约数 | `seatflow_reservation.status in ('pending_checkin', 'in_use', 'completed')` |
| 签到数 | `seatflow_checkin_record.status = 'active'` |
| 爽约数 | `seatflow_reservation.status = 'no_show'` |
| 有效使用分钟数 | `seatflow_reservation.status in ('in_use', 'completed')` |
| 启用座位数 | `seatflow_seat.status = 'enabled'` |
| 使用率 | 有效使用分钟数 / 启用座位开放分钟数 |

`cancelled` 和 `no_show` 不计入有效使用分钟数。`pending_checkin` 计入预约数，但不计入有效使用分钟数。

## 错误响应

参数错误由 RuoYi 全局异常处理返回中文提示。

示例：

```json
{
  "code": 500,
  "msg": "开始时间不能晚于结束时间"
}
```

常见错误：

| 场景 | 提示 |
| --- | --- |
| 缺少开始时间 | `开始时间不能为空` |
| 缺少结束时间 | `结束时间不能为空` |
| 开始时间晚于结束时间 | `开始时间不能晚于结束时间` |
| 日期范围超过 31 天 | `查询时间范围不能超过31天` |
| 指标参数非法 | `报表指标参数不合法` |
| 热门时段粒度非法 | `热门时段第一版只支持按小时统计` |
| 没有权限 | 由 RuoYi 权限拦截返回 |

## 自测记录

已执行编译检查：

```bash
mvn -pl ruoyi-seatflow -am test
mvn -pl ruoyi-admin -am test
```

接口自测清单：

- 使用 `admin` 登录后请求 5 个报表接口，确认返回 200。
- 不传 `beginTime` 或 `endTime`，确认返回中文参数错误。
- `beginTime` 晚于 `endTime`，确认返回 `开始时间不能晚于结束时间`。
- 查询范围超过 31 天，确认返回 `查询时间范围不能超过31天`。
- 无数据时间段返回 0 或空数组。
- 有演示数据时，热力图、使用率、热门时段、自习室排行有可复现数据。
- 无 `seatflow:report:view` 权限的账号访问时被 RuoYi 拦截。

口径检查：

- `cancelled`、`no_show` 不增加有效使用分钟数。
- 停用座位不进入使用率分母。
- `pending_checkin` 计入预约数，不计入有效使用分钟数。
- `in_use`、`completed` 计入有效使用分钟数。
- 热门时段按小时聚合，标签固定为 `HH:00`。
