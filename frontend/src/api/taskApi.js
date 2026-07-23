import axiosClient from './axiosClient'

export const getTasks = () => axiosClient.get('/tasks')

export const updateTask = (id, data) => axiosClient.put(`/tasks/${id}`, data)

export const updateTaskStatus = (id, status) =>
  axiosClient.patch(`/tasks/${id}/status`, { status })

export const deleteTask = (id) => axiosClient.delete(`/tasks/${id}`)
