import { Routes, Route } from 'react-router-dom'
import Dashboard from './pages/Dashboard.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import MeetingList from './pages/MeetingList.jsx'
import MeetingForm from './pages/MeetingForm.jsx'
import MeetingDetail from './pages/MeetingDetail.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/meetings"
        element={
          <ProtectedRoute>
            <MeetingList />
          </ProtectedRoute>
        }
      />
      <Route
        path="/meetings/new"
        element={
          <ProtectedRoute>
            <MeetingForm />
          </ProtectedRoute>
        }
      />
      <Route
        path="/meetings/:id"
        element={
          <ProtectedRoute>
            <MeetingDetail />
          </ProtectedRoute>
        }
      />
      <Route
        path="/meetings/:id/edit"
        element={
          <ProtectedRoute>
            <MeetingForm />
          </ProtectedRoute>
        }
      />
    </Routes>
  )
}

export default App
