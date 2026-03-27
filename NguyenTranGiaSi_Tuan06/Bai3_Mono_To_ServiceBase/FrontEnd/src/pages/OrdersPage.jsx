import { useState, useEffect } from 'react'
import { orderApi, recommendationApi } from '../services/api'
import { useUser } from '../App'
import OrderStatusBadge from '../components/OrderStatusBadge'

const fmt = (n) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n)

const STATUS_STEPS = ['PENDING', 'CONFIRMED', 'PREPARING', 'DELIVERING', 'COMPLETED']

function StatusTimeline({ status }) {
  if (status === 'CANCELLED') {
    return (
      <div className="flex items-center gap-2 text-sm text-zinc-400">
        <span className="w-2 h-2 bg-zinc-400 rounded-full" />
        Đơn hàng đã bị hủy
      </div>
    )
  }
  const currentIdx = STATUS_STEPS.indexOf(status)
  const labels = { PENDING: 'Chờ xác nhận', CONFIRMED: 'Xác nhận', PREPARING: 'Chuẩn bị', DELIVERING: 'Đang giao', COMPLETED: 'Hoàn thành' }

  return (
    <div className="flex items-center gap-1 overflow-x-auto scrollbar-thin">
      {STATUS_STEPS.map((step, idx) => {
        const isDone = idx <= currentIdx
        return (
          <div key={step} className="flex items-center gap-1 flex-shrink-0">
            <div className={`flex flex-col items-center gap-1`}>
              <div className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold transition-all ${isDone ? 'bg-black text-white' : 'bg-zinc-100 text-zinc-400'}`}>
                {isDone && idx < currentIdx ? '✓' : idx + 1}
              </div>
              <span className={`text-[10px] font-medium whitespace-nowrap ${isDone ? 'text-black' : 'text-zinc-400'}`}>{labels[step]}</span>
            </div>
            {idx < STATUS_STEPS.length - 1 && (
              <div className={`w-8 h-0.5 mb-4 flex-shrink-0 ${idx < currentIdx ? 'bg-black' : 'bg-zinc-200'}`} />
            )}
          </div>
        )
      })}
    </div>
  )
}

function OrderCard({ order }) {
  const [expanded, setExpanded] = useState(false)

  return (
    <div className="bg-white border border-zinc-100 rounded-2xl overflow-hidden shadow-sm hover:shadow-md transition-all">
      <div
        className="flex items-center justify-between px-5 py-4 cursor-pointer"
        onClick={() => setExpanded(!expanded)}
      >
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-zinc-900 text-white rounded-xl flex items-center justify-center font-extrabold text-sm">
            #{order.id}
          </div>
          <div>
            <p className="font-semibold text-sm">{order.items?.length || 0} món</p>
            <p className="text-xs text-zinc-400">{new Date(order.createdAt).toLocaleString('vi-VN')}</p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <OrderStatusBadge status={order.status} />
          <span className="font-extrabold text-sm">{fmt(order.finalAmount)}</span>
          <span className={`text-zinc-400 text-sm transition-transform ${expanded ? 'rotate-180' : ''}`}>▾</span>
        </div>
      </div>

      {expanded && (
        <div className="px-5 pb-5 border-t border-zinc-50 pt-4 space-y-4">
          <StatusTimeline status={order.status} />

          <div className="space-y-2">
            {order.items?.map((item) => (
              <div key={item.id} className="flex items-center gap-3">
                <img
                  src={item.foodImageUrl}
                  alt={item.foodName}
                  className="w-10 h-10 object-cover rounded-lg"
                  onError={(e) => { e.target.src = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=100&q=80' }}
                />
                <div className="flex-1">
                  <p className="text-sm font-medium">{item.foodName}</p>
                  <p className="text-xs text-zinc-400">×{item.quantity}</p>
                </div>
                <span className="text-sm font-semibold">{fmt(item.subtotal)}</span>
              </div>
            ))}
          </div>

          <div className="bg-zinc-50 rounded-xl p-4 space-y-1.5 text-sm">
            <div className="flex justify-between text-zinc-500">
              <span>Tạm tính</span><span>{fmt(order.totalAmount)}</span>
            </div>
            <div className="flex justify-between text-zinc-500">
              <span>Phí giao hàng</span><span>{fmt(order.deliveryFee)}</span>
            </div>
            {order.discountAmount > 0 && (
              <div className="flex justify-between text-emerald-600 font-medium">
                <span>Giảm giá {order.voucherCode && `(${order.voucherCode})`}</span>
                <span>−{fmt(order.discountAmount)}</span>
              </div>
            )}
            <div className="flex justify-between font-extrabold text-base pt-1.5 border-t border-zinc-200">
              <span>Tổng cộng</span><span>{fmt(order.finalAmount)}</span>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default function OrdersPage() {
  const { currentUser } = useUser()
  const [orders, setOrders] = useState([])
  const [recommended, setRecommended] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!currentUser) return
    Promise.all([
      orderApi.getByUser(currentUser.id),
      recommendationApi.getPersonalized(currentUser.id, 4),
    ])
      .then(([ordersRes, recRes]) => {
        setOrders(ordersRes.data)
        setRecommended(recRes.data)
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [currentUser])

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-8">
      <div className="flex items-center gap-3 mb-8">
        <div className="w-10 h-10 bg-black rounded-xl flex items-center justify-center">
          <span className="text-white text-lg">📦</span>
        </div>
        <div>
          <h1 className="text-2xl font-extrabold">Đơn hàng của tôi</h1>
          <p className="text-zinc-500 text-sm">{currentUser?.name}</p>
        </div>
      </div>

      {loading ? (
        <div className="space-y-3">
          {Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="bg-zinc-100 rounded-2xl h-20 animate-pulse" />
          ))}
        </div>
      ) : orders.length === 0 ? (
        <div className="text-center py-20 bg-white border border-zinc-100 rounded-2xl">
          <p className="text-5xl mb-4">📭</p>
          <p className="font-bold text-lg mb-1">Chưa có đơn hàng</p>
          <p className="text-zinc-400 text-sm">Hãy đặt món ngon đầu tiên của bạn!</p>
        </div>
      ) : (
        <div className="space-y-3">
          {orders.map((order) => (
            <OrderCard key={order.id} order={order} />
          ))}
        </div>
      )}

      {recommended.length > 0 && (
        <section className="mt-10">
          <h2 className="text-lg font-extrabold mb-4">✨ Gợi ý cho bạn</h2>
          <div className="grid grid-cols-2 gap-4">
            {recommended.map((food) => (
              <div key={food.id} className="flex items-center gap-3 bg-white border border-zinc-100 rounded-xl p-3 shadow-sm">
                <img
                  src={food.imageUrl}
                  alt={food.name}
                  className="w-14 h-14 object-cover rounded-lg flex-shrink-0"
                  onError={(e) => { e.target.src = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=100&q=80' }}
                />
                <div className="min-w-0">
                  <p className="font-semibold text-sm truncate">{food.name}</p>
                  <p className="text-xs text-zinc-400 font-medium">{fmt(food.price)}</p>
                </div>
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  )
}
