import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import MfaSetup from './pages/MfaSetup'
import Roles from './pages/Roles'
import RoleForm from './pages/RoleForm'
import Users from './pages/Users'
import UserRoles from './pages/UserRoles'
import Pazaryerleri from './pages/Pazaryerleri'
import Urunler from './pages/Urunler'
import Siparisler from './pages/Siparisler'
import SenkronizasyonLoglari from './pages/SenkronizasyonLoglari'
import Raporlar from './pages/Raporlar'
import ProtectedRoute from './components/ProtectedRoute'
import './App.css'

function App() {
  return (
    <AuthProvider>
      <Router basename="/admin_panel">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/mfa-setup"
            element={
              <ProtectedRoute>
                <MfaSetup />
              </ProtectedRoute>
            }
          />
          <Route
            path="/roles"
            element={
              <ProtectedRoute>
                <Roles />
              </ProtectedRoute>
            }
          />
          <Route
            path="/roles/new"
            element={
              <ProtectedRoute>
                <RoleForm />
              </ProtectedRoute>
            }
          />
          <Route
            path="/roles/:id/edit"
            element={
              <ProtectedRoute>
                <RoleForm />
              </ProtectedRoute>
            }
          />
          <Route
            path="/users"
            element={
              <ProtectedRoute>
                <Users />
              </ProtectedRoute>
            }
          />
          <Route
            path="/users/:id/roles"
            element={
              <ProtectedRoute>
                <UserRoles />
              </ProtectedRoute>
            }
          />
          <Route
            path="/pazaryerleri"
            element={
              <ProtectedRoute>
                <Pazaryerleri />
              </ProtectedRoute>
            }
          />
          <Route
            path="/urunler"
            element={
              <ProtectedRoute>
                <Urunler />
              </ProtectedRoute>
            }
          />
          <Route
            path="/siparisler"
            element={
              <ProtectedRoute>
                <Siparisler />
              </ProtectedRoute>
            }
          />
          <Route
            path="/senkronizasyon-loglari"
            element={
              <ProtectedRoute>
                <SenkronizasyonLoglari />
              </ProtectedRoute>
            }
          />
          <Route
            path="/raporlar"
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <Raporlar />
              </ProtectedRoute>
            }
          />
          <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  )
}

export default App
