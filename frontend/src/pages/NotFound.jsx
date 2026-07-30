import { Link } from 'react-router-dom'

function NotFound() {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center text-center px-6">
      <h1 className="text-4xl font-bold text-gray-900">404</h1>
      <p className="text-gray-500 mt-2">This page doesn't exist.</p>
      <Link to="/" className="text-indigo-600 hover:underline mt-4">
        Back to dashboard
      </Link>
    </div>
  )
}

export default NotFound
