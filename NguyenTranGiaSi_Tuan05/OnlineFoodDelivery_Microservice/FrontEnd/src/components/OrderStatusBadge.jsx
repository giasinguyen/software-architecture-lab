const STATUS_CONFIG = {
  PENDING: { label: 'Chờ xác nhận', style: 'bg-zinc-100 text-zinc-600 border border-zinc-300' },
  CONFIRMED: { label: 'Đã xác nhận', style: 'bg-zinc-800 text-white' },
  PREPARING: { label: 'Đang chuẩn bị', style: 'bg-zinc-600 text-white' },
  DELIVERING: { label: 'Đang giao', style: 'bg-black text-white' },
  COMPLETED: { label: 'Hoàn thành', style: 'bg-zinc-900 text-zinc-100 border border-zinc-700' },
  CANCELLED: { label: 'Đã hủy', style: 'bg-zinc-100 text-zinc-400 line-through' },
}

export default function OrderStatusBadge({ status, size = 'sm' }) {
  const config = STATUS_CONFIG[status] || { label: status, style: 'bg-zinc-100 text-zinc-700' }
  const sizeClass = size === 'lg' ? 'px-4 py-1.5 text-sm font-semibold' : 'px-2.5 py-1 text-xs font-medium'

  return (
    <span className={`inline-flex items-center rounded-full ${sizeClass} ${config.style}`}>
      {config.label}
    </span>
  )
}
