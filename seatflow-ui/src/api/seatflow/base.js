import request from '@/utils/request'

const path = type => `/seatflow/base/${type}`

export function listBase(type, query) { return request({ url: `${path(type)}/list`, method: 'get', params: query }) }
export function listOptions(type, query) { return request({ url: `${path(type)}/options`, method: 'get', params: query }) }
export function getBase(type, id) { return request({ url: `${path(type)}/${id}`, method: 'get' }) }
export function addBase(type, data) { return request({ url: path(type), method: 'post', data }) }
export function updateBase(type, data) { return request({ url: path(type), method: 'put', data }) }
export function delBase(type, id) { return request({ url: `${path(type)}/${id}`, method: 'delete' }) }
export function generateSeats(roomId) { return request({ url: `/seatflow/base/room/${roomId}/seats/generate`, method: 'post' }) }
export function updateSeatStatus(seatId, status) { return request({ url: `/seatflow/base/seat/${seatId}/status/${status}`, method: 'put' }) }

// 保留首版脚手架导出的兼容方法。
export function listCampus(query) { return listBase('campus', query) }
