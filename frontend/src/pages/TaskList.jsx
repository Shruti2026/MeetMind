import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getTasks, updateTaskStatus, deleteTask } from '../api/taskApi'

function TaskList() {
  const [tasks, setTasks] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadTasks = async () => {
    setLoading(true)
    try {
      const { data } = await getTasks()
      setTasks(data)
    } catch (err) {
      setError('Failed to load tasks')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadTasks()
  }, [])

  const toggleStatus = async (task) => {
    const newStatus = task.status === 'PENDING' ? 'COMPLETED' : 'PENDING'
    const { data } = await updateTaskStatus(task.id, newStatus)
    setTasks((prev) => prev.map((t) => (t.id === task.id ? data : t)))
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this task?')) return
    await deleteTask(id)
    setTasks((prev) => prev.filter((t) => t.id !== id))
  }

  return (
    <div className="min-h-screen bg-gray-50 px-6 py-8">
      <div className="max-w-3xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Tasks</h1>
          <Link to="/" className="text-sm text-indigo-600 hover:underline">
            &larr; Dashboard
          </Link>
        </div>

        {loading && <p className="text-gray-500">Loading...</p>}
        {error && <p className="text-red-600">{error}</p>}
        {!loading && tasks.length === 0 && (
          <p className="text-gray-500">No tasks yet. Process a meeting to generate tasks.</p>
        )}

        <ul className="space-y-3">
          {tasks.map((task) => (
            <li
              key={task.id}
              className="bg-white p-4 rounded-lg shadow flex items-start justify-between gap-4"
            >
              <div className="flex items-start gap-3">
                <input
                  type="checkbox"
                  checked={task.status === 'COMPLETED'}
                  onChange={() => toggleStatus(task)}
                  className="mt-1"
                />
                <div>
                  <p
                    className={
                      task.status === 'COMPLETED'
                        ? 'text-gray-400 line-through'
                        : 'text-gray-900'
                    }
                  >
                    {task.description}
                  </p>
                  <p className="text-sm text-gray-500 mt-1">
                    {task.assignee ? `Assigned to ${task.assignee}` : 'Unassigned'}
                    {task.dueDate ? ` · Due ${task.dueDate}` : ''}
                  </p>
                </div>
              </div>
              <button
                onClick={() => handleDelete(task.id)}
                className="text-sm text-red-600 hover:underline shrink-0"
              >
                Delete
              </button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}

export default TaskList
