import { useCart } from '../context/CartContext'

const fmt = (n) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n)

export default function FoodCard({ food }) {
  const { addItem } = useCart()

  return (
    <div className="bg-white border border-zinc-100 rounded-2xl overflow-hidden shadow-sm hover:shadow-lg transition-all duration-300 group flex flex-col">
      <div className="relative overflow-hidden h-44">
        <img
          src={food.imageUrl || 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500&q=80'}
          alt={food.name}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
          onError={(e) => {
            e.target.src = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500&q=80'
          }}
        />
        <span className="absolute top-3 left-3 bg-black text-white text-xs px-2.5 py-1 rounded-full font-medium">
          {food.category}
        </span>
        {food.orderCount > 0 && (
          <span className="absolute top-3 right-3 bg-white text-black text-xs px-2 py-1 rounded-full font-medium border border-zinc-200">
            🔥 {food.orderCount}
          </span>
        )}
      </div>

      <div className="p-4 flex flex-col flex-1">
        <h3 className="font-bold text-[15px] text-zinc-900 leading-tight mb-1">{food.name}</h3>
        <p className="text-zinc-500 text-xs leading-relaxed mb-3 flex-1 line-clamp-2">
          {food.description}
        </p>
        <div className="flex items-center justify-between mt-auto">
          <span className="text-base font-extrabold text-black">{fmt(food.price)}</span>
          <button
            onClick={() => addItem(food)}
            className="flex items-center gap-1.5 bg-black text-white text-xs font-semibold px-3 py-2 rounded-xl hover:bg-zinc-800 active:scale-95 transition-all"
          >
            <span className="text-base leading-none">+</span> Thêm
          </button>
        </div>
      </div>
    </div>
  )
}
