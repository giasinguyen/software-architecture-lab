import { createContext, useContext, useState } from 'react'
import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import MenuPage from './pages/MenuPage'
import OrdersPage from './pages/OrdersPage'
import AdminPage from './pages/AdminPage'

export const UserContext = createContext()
export const useUser = () => useContext(UserContext)

function App() {
  const [currentUser, setCurrentUser] = useState({ id: 1, name: 'Nguyễn Trần Gia Sĩ', role: 'ADMIN' })

  return (
    <UserContext.Provider value={{ currentUser, setCurrentUser }}>
      <div className="min-h-screen bg-zinc-50">
        <Navbar />
        <Routes>
          <Route path="/" element={<MenuPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/admin" element={<AdminPage />} />
        </Routes>
      </div>
    </UserContext.Provider>
  )
}

export default App

