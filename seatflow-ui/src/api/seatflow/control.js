import request from '@/utils/request'

export function checkinReservation(data) {
  return request({
    url: '/seatflow/control/checkin',
    method: 'post',
    data
  })
}
