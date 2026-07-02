import request from '@/utils/request'

export function listMyReservations(query) {
  return request({
    url: '/seatflow/reservation/mine',
    method: 'get',
    params: query
  })
}
