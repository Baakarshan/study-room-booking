import request from '@/utils/request'

export function getUsageSummary(query) {
  return request({
    url: '/seatflow/report/summary',
    method: 'get',
    params: query
  })
}
