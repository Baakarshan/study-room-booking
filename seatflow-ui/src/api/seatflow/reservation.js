import request from '@/utils/request'

export function listReservationCampuses() {
  return request({
    url: '/seatflow/reservation/campuses',
    method: 'get'
  })
}

export function listReservationBuildings(campusId) {
  return request({
    url: '/seatflow/reservation/buildings',
    method: 'get',
    params: { campusId }
  })
}

export function listReservationFloors(buildingId) {
  return request({
    url: '/seatflow/reservation/floors',
    method: 'get',
    params: { buildingId }
  })
}

export function listReservationRooms(floorId) {
  return request({
    url: '/seatflow/reservation/rooms',
    method: 'get',
    params: { floorId }
  })
}

export function listReservationSeats(query) {
  return request({
    url: '/seatflow/reservation/seats',
    method: 'get',
    params: query
  })
}

export function createReservation(data) {
  return request({
    url: '/seatflow/reservation',
    method: 'post',
    data
  })
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
  return request({
    url: `/seatflow/reservation/${reservationId}`,
    method: 'delete'
  })
}
