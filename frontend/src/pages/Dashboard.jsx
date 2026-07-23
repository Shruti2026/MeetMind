import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getDashboard } from '../api/dashboardApi'
import { useAuth } from '../context/AuthContext'

function StatCard({ label, value }) {
  return (
    <div className="bg-white p-5 rounded-lg shadow">
      <p className="text-sm text-gray-500">{label}</p>
      <p className="text-2xl font-bold text-gray-900 mt-1">{value}</p>
    </div>
  )
}

function Dashboard() {
  const [stats, setStats] = useState(null)
  const [error, setError] = useState('')
  const { user, logout } = useAuth()

  useEffect(() => {
    getDashboard()
      .then(({ data }) => setStats(data))
      .catch(() => setError('Failed to load dashboard'))
  }, [])

  return (
    <div className="min-h-screen bg-gray-50 px-6 py-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">MeetMind AI</h1>
            <p className="text-gray-500 mt-1">Welcome back, {user?.name}</p>
          </div>
          <div className="flex gap-4 items-center">
            <Link to="/meetings" className="text-sm text-indigo-600 hover:underline">
              Meetings
            </Link>
            <Link to="/tasks" className="text-sm text-indigo-600 hover:underline">
              Tasks
            </Link>
            <button onClick={logout} className="text-sm text-gray-500 hover:underline">
              Log out
            </button>
          </div>
        </div>

        {error && <p className="text-red-600">{error}</p>}
        {!stats && !error && <p className="text-gray-500">Loading...</p>}

        {stats && (
          <>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
              <StatCard label="Total Meetings" value={stats.totalMeetings} />
              <StatCard label="Total Tasks" value={stats.totalTasks} />
              <StatCard label="Pending Tasks" value={stats.pendingTasks} />
              <StatCard label="Completed Tasks" value={stats.completedTasks} />
            </div>

            <div className="grid md:grid-cols-2 gap-6">
              <div className="bg-white p-5 rounded-lg shadow">
                <h2 className="font-semibold text-gray-900 mb-3">Upcoming Deadlines</h2>
                {stats.upcomingDeadlines.length === 0 && (
                  <p className="text-sm text-gray-500">No upcoming deadlines.</p>
                )}
                <ul className="space-y-2">
                  {stats.upcomingDeadlines.map((item) => (
                    <li key={item.id} className="text-sm text-gray-700">
                      {item.description}{' '}
                      <span className="text-gray-400">· Due {item.dueDate}</span>
                    </li>
                  ))}
                </ul>
              </div>

              <div className="bg-white p-5 rounded-lg shadow">
                <h2 className="font-semibold text-gray-900 mb-3">Recent Meetings</h2>
                {stats.recentMeetings.length === 0 && (
                  <p className="text-sm text-gray-500">No meetings yet.</p>
                )}
                <ul className="space-y-2">
                  {stats.recentMeetings.map((meeting) => (
                    <li key={meeting.id} className="text-sm">
                      <Link to={`/meetings/${meeting.id}`} className="text-gray-900 hover:underline">
                        {meeting.title}
                      </Link>
                      <span className="text-gray-400"> · {meeting.status}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  )
}

export default Dashboard
