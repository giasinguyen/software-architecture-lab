import { useState, useEffect, useCallback } from 'react'
import { orderApi, foodApi } from '../services/api'
import OrderStatusBadge from '../components/OrderStatusBadge'

const fmt = (n) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n)

const STATUS_TRANSITIONS = {
  PENDING: { next: 'CONFIRMED', label: 'Xác nhận đơn' },
  CONFIRMED: { next: 'PREPARING', label: 'Bắt đầu chuẩn bị' },
  PREPARING: { next: 'DELIVERING', label: 'Giao cho shipper' },
  DELIVERING: { next: 'COMPLETED', label: 'Hoàn thành' },
}

function OrderRow({ order, onStatusUpdate }) {
  const [expanded, setExpanded] = useState(false)
  const [updating, setUpdating] = useState(false)
  const transition = STATUS_TRANSITIONS[order.status]

  const handleUpdate = async () => {
    if (!transition) return
    setUpdating(true)
    try {
      const { data } = await orderApi.updateStatus(order.id, transition.next)
      onStatusUpdate(data)
    } catch (err) {
      alert(err.response?.data?.error || 'Cập nhật thất bại')
    } finally {
      setUpdating(false)
    }
  }

  return (
    <div className="bg-white border border-zinc-100 rounded-2xl overflow-hidden">
      <div className="flex items-center gap-3 px-5 py-4 cursor-pointer" onClick={() => setExpanded(!expanded)}>
        <div className="w-10 h-10 bg-zinc-900 text-white rounded-xl flex items-center justify-center font-extrabold text-sm flex-shrink-0">
          #{order.id}
        </div>
        <div className="flex-1 min-w-0">
          <p className="font-semibold text-sm truncate">{order.userName}</p>
          <p className="text-xs text-zinc-400">{new Date(order.createdAt).toLocaleString('vi-VN')}</p>
        </div>
        <div className="flex items-center gap-2 flex-shrink-0">
          <OrderStatusBadge status={order.status} />
          <span className="font-bold text-sm hidden sm:block">{fmt(order.finalAmount)}</span>
        </div>
        <span className={`text-zinc-400 text-sm transition-transform ${expanded ? 'rotate-180' : ''}`}>▾</span>
      </div>

      {expanded && (
        <div className="px-5 pb-5 border-t border-zinc-50 pt-4 space-y-4">
          <div className="space-y-2">
            {order.items?.map((item) => (
              <div key={item.id} className="flex items-center justify-between py-1.5 border-b border-zinc-50 last:border-0">
                <span className="text-sm">{item.foodName}</span>
                <span className="text-xs text-zinc-500">×{item.quantity} · {fmt(item.subtotal)}</span>
              </div>
            ))}
          </div>

          <div className="flex flex-wrap gap-2 justify-between items-center bg-zinc-50 rounded-xl px-4 py-3">
            <div className="text-sm">
              <span className="text-zinc-500">Tổng: </span>
              <span className="font-extrabold">{fmt(order.finalAmount)}</span>
              {order.voucherCode && (
                <span className="ml-2 text-xs bg-black text-white px-2 py-0.5 rounded-full">{order.voucherCode}</span>
              )}
            </div>
            {transition && (
              <button
                onClick={handleUpdate}
                disabled={updating}
                className="bg-black text-white text-xs font-bold px-4 py-2 rounded-xl hover:bg-zinc-800 disabled:opacity-50 transition-colors"
              >
                {updating ? '...' : transition.label}
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  )
}

function AddFoodForm({ onAdded }) {
  const [form, setForm] = useState({ name: '', description: '', price: '', category: '', imageUrl: '' })
  const [saving, setSaving] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.name || !form.price || !form.category) return
    setSaving(true)
    try {
      const { data } = await foodApi.create({ ...form, price: Number(form.price) })
      onAdded(data)
      setForm({ name: '', description: '', price: '', category: '', imageUrl: '' })
    } catch (err) {
      alert(err.response?.data?.error || 'Thêm món thất bại')
    } finally {
      setSaving(false)
    }
  }

  const field = (key, placeholder, type = 'text') => (
    <input
      type={type}
      placeholder={placeholder}
      value={form[key]}
      onChange={(e) => setForm({ ...form, [key]: e.target.value })}
      className="border border-zinc-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:border-black transition-colors"
      required={['name', 'price', 'category'].includes(key)}
    />
  )

  return (
    <form onSubmit={handleSubmit} className="bg-white border border-zinc-100 rounded-2xl p-5 shadow-sm">
      <h3 className="font-bold mb-4 flex items-center gap-2"><span className="text-lg">➕</span> Thêm món ăn mới</h3>
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-4">
        {field('name', 'Tên món *')}
        {field('category', 'Danh mục *')}
        {field('price', 'Giá (VNĐ) *', 'number')}
        {field('imageUrl', 'URL hình ảnh')}
        <textarea
          placeholder="Mô tả"
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
          className="sm:col-span-2 border border-zinc-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:border-black transition-colors resize-none h-20"
        />
      </div>
      <button
        type="submit"
        disabled={saving}
        className="bg-black text-white px-5 py-2.5 rounded-xl text-sm font-bold hover:bg-zinc-800 disabled:opacity-50 transition-colors"
      >
        {saving ? 'Đang lưu...' : 'Thêm món'}
      </button>
    </form>
  )
}

export default function AdminPage() {
  const [tab, setTab] = useState('orders')
  const [orders, setOrders] = useState([])
  const [foods, setFoods] = useState([])
  const [filterStatus, setFilterStatus] = useState('ALL')
  const [loading, setLoading] = useState(true)

  const loadData = useCallback(async () => {
    setLoading(true)
    try {
      const [ordersRes, foodsRes] = await Promise.all([orderApi.getAll(), foodApi.getAllAdmin()])
      setOrders(ordersRes.data)
      setFoods(foodsRes.data)
    } catch {}
    finally { setLoading(false) }
  }, [])

  useEffect(() => { loadData() }, [loadData])

  const handleStatusUpdate = (updatedOrder) => {
    setOrders((prev) => prev.map((o) => (o.id === updatedOrder.id ? updatedOrder : o)))
  }

  const handleFoodAdded = (food) => {
    setFoods((prev) => [...prev, food])
  }

  const handleToggle = async (id) => {
    try {
      const { data } = await foodApi.toggle(id)
      setFoods((prev) => prev.map((f) => (f.id === id ? data : f)))
    } catch {}
  }

  const STATUSES = ['ALL', 'PENDING', 'CONFIRMED', 'PREPARING', 'DELIVERING', 'COMPLETED', 'CANCELLED']
  const filteredOrders = filterStatus === 'ALL' ? orders : orders.filter((o) => o.status === filterStatus)

  const stats = {
    total: orders.length,
    pending: orders.filter((o) => o.status === 'PENDING').length,
    active: orders.filter((o) => ['CONFIRMED', 'PREPARING', 'DELIVERING'].includes(o.status)).length,
    completed: orders.filter((o) => o.status === 'COMPLETED').length,
    revenue: orders.filter((o) => o.status === 'COMPLETED').reduce((s, o) => s + Number(o.finalAmount), 0),
  }

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-8">
      <div className="flex items-center gap-3 mb-8">
        <div className="w-10 h-10 bg-black rounded-xl flex items-center justify-center">
          <span className="text-white text-lg">⚙️</span>
        </div>
        <h1 className="text-2xl font-extrabold">Quản trị nhà hàng</h1>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-8">
        {[
          { label: 'Tổng đơn', value: stats.total, icon: '📋' },
          { label: 'Chờ xử lý', value: stats.pending, icon: '⏳' },
          { label: 'Đang giao', value: stats.active, icon: '🚀' },
          { label: 'Doanh thu', value: fmt(stats.revenue), icon: '💰', small: true },
        ].map((stat) => (
          <div key={stat.label} className="bg-white border border-zinc-100 rounded-2xl p-5 shadow-sm">
            <p className="text-2xl mb-1">{stat.icon}</p>
            <p className={`font-extrabold ${stat.small ? 'text-base' : 'text-3xl'}`}>{stat.value}</p>
            <p className="text-xs text-zinc-400 mt-1 font-medium">{stat.label}</p>
          </div>
        ))}
      </div>

      <div className="flex gap-1 mb-6 p-1 bg-zinc-100 rounded-xl w-fit">
        {['orders', 'foods'].map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-5 py-2 rounded-lg text-sm font-semibold transition-all ${
              tab === t ? 'bg-white text-black shadow-sm' : 'text-zinc-500 hover:text-black'
            }`}
          >
            {t === 'orders' ? '📦 Đơn hàng' : '🍽️ Thực đơn'}
          </button>
        ))}
      </div>

      {tab === 'orders' && (
        <>
          <div className="flex items-center gap-2 flex-wrap mb-4">
            {STATUSES.map((s) => (
              <button
                key={s}
                onClick={() => setFilterStatus(s)}
                className={`px-3 py-1.5 rounded-full text-xs font-medium transition-all ${
                  filterStatus === s ? 'bg-black text-white' : 'bg-white border border-zinc-200 text-zinc-600 hover:border-zinc-400'
                }`}
              >
                {s === 'ALL' ? 'Tất cả' : s}
                {s !== 'ALL' && (
                  <span className="ml-1 opacity-60">({orders.filter((o) => o.status === s).length})</span>
                )}
              </button>
            ))}
            <button onClick={loadData} className="ml-auto text-xs text-zinc-400 hover:text-black transition-colors flex items-center gap-1">
              ↻ Làm mới
            </button>
          </div>

          {loading ? (
            <div className="space-y-3">
              {Array.from({ length: 4 }).map((_, i) => (
                <div key={i} className="bg-zinc-100 rounded-2xl h-16 animate-pulse" />
              ))}
            </div>
          ) : filteredOrders.length === 0 ? (
            <div className="text-center py-16 bg-white border border-zinc-100 rounded-2xl text-zinc-400">
              <p className="text-4xl mb-3">📭</p>
              <p>Không có đơn hàng nào</p>
            </div>
          ) : (
            <div className="space-y-3">
              {filteredOrders.map((order) => (
                <OrderRow key={order.id} order={order} onStatusUpdate={handleStatusUpdate} />
              ))}
            </div>
          )}
        </>
      )}

      {tab === 'foods' && (
        <div className="space-y-6">
          <AddFoodForm onAdded={handleFoodAdded} />

          <div className="bg-white border border-zinc-100 rounded-2xl overflow-hidden shadow-sm">
            <div className="px-5 py-4 border-b border-zinc-50 font-bold text-sm flex items-center justify-between">
              <span>Danh sách món ăn ({foods.length})</span>
            </div>
            <div className="divide-y divide-zinc-50">
              {foods.map((food) => (
                <div key={food.id} className="flex items-center gap-3 px-5 py-3">
                  <img
                    src={food.imageUrl}
                    alt={food.name}
                    className="w-12 h-12 object-cover rounded-xl flex-shrink-0"
                    onError={(e) => { e.target.src = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=100&q=80' }}
                  />
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold text-sm truncate">{food.name}</p>
                    <div className="flex items-center gap-2 mt-0.5">
                      <span className="text-xs text-zinc-400">{food.category}</span>
                      <span className="text-xs text-zinc-400">·</span>
                      <span className="text-xs font-semibold">{fmt(food.price)}</span>
                      {food.orderCount > 0 && (
                        <span className="text-xs text-zinc-400">· 🔥 {food.orderCount}</span>
                      )}
                    </div>
                  </div>
                  <button
                    onClick={() => handleToggle(food.id)}
                    className={`text-xs font-semibold px-3 py-1.5 rounded-xl transition-all ${
                      food.available
                        ? 'bg-zinc-100 text-zinc-600 hover:bg-zinc-200'
                        : 'bg-black text-white hover:bg-zinc-800'
                    }`}
                  >
                    {food.available ? 'Ẩn món' : 'Hiện món'}
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
