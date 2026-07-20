import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getMeeting } from '../api/meetingApi'

function MeetingDetail() {
  const { id } = useParams()
  const [meeting, setMeeting] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getMeeting(id)
      .then(({ data }) => setMeeting(data))
      .catch(() => setError('Failed to load meeting'))
  }, [id])

  if (error) return <p className="text-center mt-10 text-red-600">{error}</p>
  if (!meeting) return <p className="text-center mt-10 text-gray-500">Loading...</p>

  return (
    <div className="min-h-screen bg-gray-50 px-6 py-8">
      <div className="max-w-2xl mx-auto bg-white p-8 rounded-lg shadow">
        <Link to="/meetings" className="text-sm text-indigo-600 hover:underline">
          &larr; Back to meetings
        </Link>
        <h1 className="text-2xl font-bold text-gray-900 mt-2">{meeting.title}</h1>
        <p className="text-sm text-gray-500 mt-1">Status: {meeting.status}</p>

        {meeting.meetingDate && (
          <p className="text-sm text-gray-500 mt-1">
            Date: {new Date(meeting.meetingDate).toLocaleString()}
          </p>
        )}
        {meeting.participants && (
          <p className="text-sm text-gray-500 mt-1">Participants: {meeting.participants}</p>
        )}

        <div className="mt-6 border-t pt-6">
          <h2 className="text-lg font-semibold text-gray-900">Transcript &amp; AI Summary</h2>
          <p className="text-sm text-gray-500 mt-2">
            Not processed yet. Paste a transcript or upload audio to generate a summary,
            action items, and key decisions.
          </p>
        </div>
      </div>
    </div>
  )
}

export default MeetingDetail
