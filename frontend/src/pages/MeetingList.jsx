import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMeetings, deleteMeeting } from '../api/meetingApi'

function MeetingList() {
  const [meetings, setMeetings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadMeetings = async () => {
    setLoading(true)
    try {
      const { data } = await getMeetings()
      setMeetings(data)
    } catch (err) {
      setError('Failed to load meetings')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadMeetings()
  }, [])

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this meeting? This cannot be undone.')) return
    await deleteMeeting(id)
    setMeetings((prev) => prev.filter((m) => m.id !== id))
  }

  return (
    <div className="min-h-screen bg-gray-50 px-6 py-8">
      <div className="max-w-3xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Meetings</h1>
          <Link
            to="/meetings/new"
            className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
          >
            New Meeting
          </Link>
        </div>

        {loading && <p className="text-gray-500">Loading...</p>}
        {error && <p className="text-red-600">{error}</p>}

        {!loading && meetings.length === 0 && (
          <p className="text-gray-500">No meetings yet. Create your first one.</p>
        )}

        <ul className="space-y-3">
          {meetings.map((meeting) => (
            <li
              key={meeting.id}
              className="bg-white p-4 rounded-lg shadow flex items-center justify-between"
            >
              <div>
                <Link to={`/meetings/${meeting.id}`} className="font-medium text-gray-900 hover:underline">
                  {meeting.title}
                </Link>
                <p className="text-sm text-gray-500">{meeting.status}</p>
              </div>
              <div className="flex gap-3">
                <Link to={`/meetings/${meeting.id}/edit`} className="text-sm text-indigo-600 hover:underline">
                  Edit
                </Link>
                <button
                  onClick={() => handleDelete(meeting.id)}
                  className="text-sm text-red-600 hover:underline"
                >
                  Delete
                </button>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}

export default MeetingList
