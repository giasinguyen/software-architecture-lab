import { useState } from 'react'
import { useCart } from '../context/CartContext'
import { orderApi, voucherApi } from '../services/api'
import { useUser } from '../App'

const fmt = (n) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n)

export default function Cart() {
  const { items, removeItem, updateQuantity, clearCart, total, isOpen, setIsOpen } = useCart()
  const { currentUser } = useUser()

  const [voucherCode, setVoucherCode] = useState('')
  const [voucher, setVoucher] = useState(null)
  const [voucherMsg, setVoucherMsg] = useState('')
  const [placing, setPlacing] = useState(false)
  const [success, setSuccess] = useState(null)

  const DELIVERY_FEE = 15000
  const discount = voucher?.discountAmount || 0
  const finalAmount = total + DELIVERY_FEE - discount

  const handleVoucher = async () => {
    if (!voucherCode.trim()) return
    try {
      const { data } = await voucherApi.validate(voucherCode.trim(), total)
      setVoucher(data)
      setVoucherMsg(data.message)
    } catch {
      setVoucherMsg('Không thể kiểm tra voucher')
    }
  }

  const handlePlaceOrder = async () => {
    if (items.length === 0) return
    setPlacing(true)
    try {
      const payload = {
        userId: currentUser.id,
        items: items.map((i) => ({ foodId: i.food.id, quantity: i.quantity })),
        voucherCode: voucher?.valid ? voucherCode.trim() : null,
      }
      const { data } = await orderApi.place(payload)
      setSuccess(data)
      clearCart()
      setVoucher(null)
      setVoucherCode('')
      setVoucherMsg('')
    } catch (err) {
      alert(err.response?.data?.error || 'Đặt hàng thất bại')
    } finally {
      setPlacing(false)
    }
  }

  const handleClose = () => {
    setIsOpen(false)
    setSuccess(null)
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex justify-end">
      <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={handleClose} />
      <div className="relative w-full max-w-md bg-white h-full flex flex-col shadow-2xl">
        <div className="flex items-center justify-between px-6 py-5 border-b border-zinc-100">
          <h2 className="text-xl font-bold">Giỏ hàng</h2>
          <button onClick={handleClose} className="w-8 h-8 flex items-center justify-center rounded-full hover:bg-zinc-100 transition-colors text-xl leading-none">
            ×
          </button>
        </div>

        {success ? (
          <div className="flex-1 flex flex-col items-center justify-center px-6 text-center">
            <div className="w-16 h-16 bg-black rounded-full flex items-center justify-center mb-4">
              <span className="text-white text-3xl">✓</span>
            </div>
            <h3 className="text-xl font-bold mb-2">Đặt hàng thành công!</h3>
            <p className="text-zinc-500 text-sm mb-1">Đơn hàng #{success.id}</p>
            <p className="text-zinc-500 text-sm mb-6">Tổng thanh toán: <span className="font-bold text-black">{fmt(success.finalAmount)}</span></p>
            <button onClick={handleClose} className="bg-black text-white px-6 py-3 rounded-xl font-semibold hover:bg-zinc-800 transition-colors">
              Tiếp tục mua sắm
            </button>
          </div>
        ) : (
          <>
            <div className="flex-1 overflow-y-auto px-6 py-4 space-y-3">
              {items.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-full py-16 text-zinc-400">
                  <span className="text-5xl mb-4">🛒</span>
                  <p className="font-medium">Giỏ hàng trống</p>
                  <p className="text-sm mt-1">Thêm món để bắt đầu</p>
                </div>
              ) : (
                items.map((item) => (
                  <div key={item.food.id} className="flex items-center gap-3 p-3 bg-zinc-50 rounded-xl">
                    <img
                      src={item.food.imageUrl}
                      alt={item.food.name}
                      className="w-14 h-14 object-cover rounded-lg flex-shrink-0"
                      onError={(e) => { e.target.src = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=100&q=80' }}
                    />
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-sm truncate">{item.food.name}</p>
                      <p className="text-zinc-500 text-xs">{fmt(item.food.price)}</p>
                    </div>
                    <div className="flex items-center gap-2 flex-shrink-0">
                      <button onClick={() => updateQuantity(item.food.id, item.quantity - 1)} className="w-7 h-7 border border-zinc-300 rounded-lg flex items-center justify-center text-sm font-bold hover:bg-zinc-200 transition-colors">−</button>
                      <span className="w-6 text-center text-sm font-bold">{item.quantity}</span>
                      <button onClick={() => updateQuantity(item.food.id, item.quantity + 1)} className="w-7 h-7 bg-black text-white rounded-lg flex items-center justify-center text-sm font-bold hover:bg-zinc-800 transition-colors">+</button>
                      <button onClick={() => removeItem(item.food.id)} className="w-7 h-7 ml-1 flex items-center justify-center text-zinc-400 hover:text-zinc-700 transition-colors">✕</button>
                    </div>
                  </div>
                ))
              )}
            </div>

            {items.length > 0 && (
              <div className="border-t border-zinc-100 px-6 py-5 space-y-4">
                <div className="flex gap-2">
                  <input
                    type="text"
                    value={voucherCode}
                    onChange={(e) => { setVoucherCode(e.target.value.toUpperCase()); setVoucher(null); setVoucherMsg('') }}
                    placeholder="Nhập mã voucher..."
                    className="flex-1 border border-zinc-200 rounded-xl px-3 py-2 text-sm focus:outline-none focus:border-black transition-colors"
                  />
                  <button onClick={handleVoucher} className="bg-zinc-900 text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-black transition-colors">
                    Áp dụng
                  </button>
                </div>
                {voucherMsg && (
                  <p className={`text-xs px-1 ${voucher?.valid ? 'text-emerald-600 font-medium' : 'text-red-500'}`}>
                    {voucher?.valid ? '✓ ' : '✗ '}{voucherMsg}
                  </p>
                )}

                <div className="space-y-2 text-sm">
                  <div className="flex justify-between text-zinc-600">
                    <span>Tạm tính</span>
                    <span>{fmt(total)}</span>
                  </div>
                  <div className="flex justify-between text-zinc-600">
                    <span>Phí giao hàng</span>
                    <span>{fmt(DELIVERY_FEE)}</span>
                  </div>
                  {discount > 0 && (
                    <div className="flex justify-between text-emerald-600 font-medium">
                      <span>Giảm giá</span>
                      <span>−{fmt(discount)}</span>
                    </div>
                  )}
                  <div className="flex justify-between font-extrabold text-base pt-2 border-t border-zinc-100">
                    <span>Tổng cộng</span>
                    <span>{fmt(finalAmount)}</span>
                  </div>
                </div>

                <button
                  onClick={handlePlaceOrder}
                  disabled={placing}
                  className="w-full bg-black text-white py-3.5 rounded-xl font-bold text-sm hover:bg-zinc-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors active:scale-[0.99]"
                >
                  {placing ? 'Đang xử lý...' : `Đặt hàng · ${fmt(finalAmount)}`}
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}
