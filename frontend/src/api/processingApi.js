import axiosClient from './axiosClient'

export const saveTextTranscript = (meetingId, content) =>
  axiosClient.post(`/meetings/${meetingId}/transcript`, { content })

export const saveAudioTranscript = (meetingId, file) => {
  const formData = new FormData()
  formData.append('file', file)
  return axiosClient.post(`/meetings/${meetingId}/audio`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export const processMeeting = (meetingId) =>
  axiosClient.post(`/meetings/${meetingId}/process`)

export const getSummary = (meetingId) =>
  axiosClient.get(`/meetings/${meetingId}/summary`)
