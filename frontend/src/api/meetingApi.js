import axiosClient from './axiosClient'

export const getMeetings = () => axiosClient.get('/meetings')

export const getMeeting = (id) => axiosClient.get(`/meetings/${id}`)

export const createMeeting = (data) => axiosClient.post('/meetings', data)

export const updateMeeting = (id, data) => axiosClient.put(`/meetings/${id}`, data)

export const deleteMeeting = (id) => axiosClient.delete(`/meetings/${id}`)
