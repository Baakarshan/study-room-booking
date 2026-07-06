import request from '@/utils/request'

export function listCampuses() {
  return request({ url: '/seatflow/reservation/campuses', method: 'get' })
}

export function listBuildings(campusId) {
  return request({ url: '/seatflow/reservation/buildings', method: 'get', params: { campusId } })
}

export function listFloors(buildingId) {
  return request({ url: '/seatflow/reservation/floors', method: 'get', params: { buildingId } })
}

export function listRooms(floorId) {
  return request({ url: '/seatflow/reservation/rooms', method: 'get', params: { floorId } })
}

export function listAvailableSeats(query) {
  return request({ url: '/seatflow/reservation/seats', method: 'get', params: query })
}

export function createReservation(data) {
  return request({ url: '/seatflow/reservation', method: 'post', data })
}

export function listMyReservations(query) {
  return request({
    url: '/seatflow/reservation/mine',
    method: 'get',
    params: query
  })
}

export function listManagedReservations(query) {
  return request({
    url: '/seatflow/reservation/manage',
    method: 'get',
    params: query
  })
}

export function cancelReservation(reservationId) {
  return request({ url: `/seatflow/reservation/${reservationId}`, method: 'delete' })
}
