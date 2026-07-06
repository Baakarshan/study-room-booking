import request from '@/utils/request'

export function checkinReservation(data) {
  return request({
    url: '/seatflow/control/checkin',
    method: 'post',
    data
  })
}

export function listAvailableCheckins() {
  return request({ url: '/seatflow/control/checkin/available', method: 'get' })
}

export function listMyViolations() {
  return request({ url: '/seatflow/control/violations/mine', method: 'get' })
}

export function listViolations(query) {
  return request({ url: '/seatflow/control/violations', method: 'get', params: query })
}

export function listBlacklist(query) {
  return request({ url: '/seatflow/control/blacklist', method: 'get', params: query })
}

export function completeReservation(data) {
  return request({ url: '/seatflow/control/complete', method: 'post', data })
}

export function getControlProfile() {
  return request({ url: '/seatflow/control/profile', method: 'get' })
}

export function releaseBlacklist(id) {
  return request({ url: `/seatflow/control/blacklist/${id}/release`, method: 'put' })
}
