import request from '@/utils/request'

export function listCampus(query) {
  return request({
    url: '/seatflow/base/campus/list',
    method: 'get',
    params: query
  })
}
