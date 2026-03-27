import { useState, useEffect } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { notificationApi, userApi } from '../services/api'
import { useUser } from '../App'

export default function Navbar() {
  const location = useLocation()
  const { itemCount, setIsOpen } = useCart()
  const { currentUser, setCurrentUser } = useUser()
  const [notifications, setNotifications] = useState([])
  const [showNotifs, setShowNotifs] = useState(false)
  const [users, setUsers] = useState([])

  const unreadCount = notifications.filter((n) => !n.read).length

  useEffect(() => {
    userApi.getAll().then(({ data }) => setUsers(data)).catch(() => {})
  }, [])

  useEffect(() => {
    if (!currentUser) return
    notificationApi.getByUser(currentUser.id).then(({ data }) => setNotifications(data)).catch(() => {})
  }, [currentUser])

  const handleMarkRead = async () => {
    if (unreadCount === 0) return
    await notificationApi.markAllRead(currentUser.id)
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })))
  }

  const navLink = (to, label) => {
    const active = location.pathname === to
    return (
      <Link to={to} className={`text-sm font-medium px-1 pb-1 border-b-2 transition-all ${active ? 'border-black text-black' : 'border-transparent text-zinc-500 hover:text-black'}`}>
        {label}
      </Link>
    )
  }

  return (
    <nav className="sticky top-0 z-40 bg-white border-b border-zinc-200 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 flex items-center justify-between h-16 gap-4">
        <Link to="/" className="flex items-center gap-2.5 flex-shrink-0">
          <div className="w-8 h-8 bg-black rounded-lg flex items-center justify-center">
            <span className="text-white text-lg">🍜</span>
          </div>
          <span className="font-extrabold text-[17px] tracking-tight">FoodDash</span>
        </Link>

        <div className="hidden sm:flex items-center gap-6">
          {navLink('/', 'Menu')}
          {navLink('/orders', 'Đơn hàng')}
          {navLink('/admin', 'Quản trị')}
        </div>

        <div className="flex items-center gap-3">
          <select
            value={currentUser?.id || ''}
            onChange={(e) => {
              const u = users.find((u) => u.id === Number(e.target.value))
              if (u) setCurrentUser({ id: u.id, name: u.name, role: u.role })
            }}
            className="text-xs border border-zinc-200 rounded-lg px-2 py-1.5 focus:outline-none focus:border-black bg-zinc-50 max-w-[130px] truncate"
          >
            {users.map((u) => (
              <option key={u.id} value={u.id}>{u.name}</option>
            ))}
          </select>

          <div className="relative">
            <button
              onClick={() => { setShowNotifs(!showNotifs); if (!showNotifs) handleMarkRead() }}
              className="relative w-9 h-9 flex items-center justify-center rounded-xl hover:bg-zinc-100 transition-colors"
            >
              <span className="text-lg">🔔</span>
              {unreadCount > 0 && (
                <span className="absolute -top-0.5 -right-0.5 w-4 h-4 bg-black text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                  {unreadCount > 9 ? '9+' : unreadCount}
                </span>
              )}
            </button>

            {showNotifs && (
              <div className="absolute right-0 top-11 w-80 bg-white border border-zinc-100 rounded-2xl shadow-xl overflow-hidden z-50">
                <div className="px-4 py-3 border-b border-zinc-100 flex items-center justify-between">
                  <span className="font-bold text-sm">Thông báo</span>
                  <button onClick={() => setShowNotifs(false)} className="text-zinc-400 hover:text-zinc-700 text-lg leading-none">×</button>
                </div>
                <div className="max-h-80 overflow-y-auto">
                  {notifications.length === 0 ? (
                    <div className="px-4 py-8 text-center text-zinc-400 text-sm">Chưa có thông báo</div>
                  ) : (
                    notifications.map((n) => (
                      <div key={n.id} className={`px-4 py-3 border-b border-zinc-50 last:border-0 ${!n.read ? 'bg-zinc-50' : ''}`}>
                        <p className={`text-sm leading-relaxed ${!n.read ? 'font-medium text-black' : 'text-zinc-600'}`}>{n.message}</p>
                        <p className="text-xs text-zinc-400 mt-1">{new Date(n.createdAt).toLocaleString('vi-VN')}</p>
                      </div>
                    ))
                  )}
                </div>
              </div>
            )}
          </div>

          <button
            onClick={() => setIsOpen(true)}
            className="relative flex items-center gap-2 bg-black text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-zinc-800 transition-colors"
          >
            <span>🛒</span>
            {itemCount > 0 && (
              <span className="absolute -top-1.5 -right-1.5 w-5 h-5 bg-white text-black text-[10px] font-bold rounded-full border-2 border-black flex items-center justify-center">
                {itemCount}
              </span>
            )}
            <span className="hidden sm:inline">Giỏ hàng</span>
            {itemCount > 0 && <span className="hidden sm:inline text-zinc-300 text-xs">({itemCount})</span>}
          </button>
        </div>
      </div>

      <div className="sm:hidden flex items-center gap-4 px-4 pb-2">
        {navLink('/', 'Menu')}
        {navLink('/orders', 'Đơn hàng')}
        {navLink('/admin', 'Quản trị')}
      </div>
    </nav>
  )
}
