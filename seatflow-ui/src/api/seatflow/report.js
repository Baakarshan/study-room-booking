import request from '@/utils/request'

export function getUsageSummary(query) {
  return request({
    url: '/seatflow/report/summary',
    method: 'get',
    params: query
  })
}

export function getSeatHeatmap(query) {
  return request({
    url: '/seatflow/report/heatmap',
    method: 'get',
    params: query
  })
}

export function getUsageRate(query) {
  return request({
    url: '/seatflow/report/usage-rate',
    method: 'get',
    params: query
  })
}

export function getPopularSlots(query) {
  return request({
    url: '/seatflow/report/popular-slots',
    method: 'get',
    params: query
  })
}

export function getRoomRanking(query) {
  return request({
    url: '/seatflow/report/room-ranking',
    method: 'get',
    params: query
  })
}
