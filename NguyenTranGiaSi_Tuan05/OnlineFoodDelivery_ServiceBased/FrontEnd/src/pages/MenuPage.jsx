import { useState, useEffect } from 'react'
import { foodApi, recommendationApi } from '../services/api'
import FoodCard from '../components/FoodCard'
import Cart from '../components/Cart'

const CATEGORIES = ['Tất cả', 'Phở', 'Bún', 'Cơm', 'Bánh Mì', 'Gà', 'Pizza', 'Burger', 'Mì', 'Đồ Uống', 'Chè']

export default function MenuPage() {
  const [foods, setFoods] = useState([])
  const [popular, setPopular] = useState([])
  const [selectedCategory, setSelectedCategory] = useState('Tất cả')
  const [searchQuery, setSearchQuery] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([foodApi.getAll(), recommendationApi.getPopular(4)])
      .then(([foodRes, popRes]) => {
        setFoods(foodRes.data)
        setPopular(popRes.data)
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const filtered = foods.filter((f) => {
    const matchesCategory = selectedCategory === 'Tất cả' || f.category === selectedCategory
    const matchesSearch = f.name.toLowerCase().includes(searchQuery.toLowerCase())
    return matchesCategory && matchesSearch
  })

  return (
    <>
      <Cart />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
        <div className="relative overflow-hidden rounded-3xl bg-black text-white mb-8 px-8 py-12">
          <div className="relative z-10">
            <p className="text-zinc-400 text-sm font-medium mb-2 tracking-widest uppercase">Online Food Delivery</p>
            <h1 className="text-4xl sm:text-5xl font-extrabold leading-tight mb-3">
              Đặt món ngon,<br />giao tận nơi 🚀
            </h1>
            <p className="text-zinc-400 text-base max-w-md">
              Hàng trăm món ngon từ các nhà hàng uy tín, giao hàng nhanh chóng đến tận tay bạn.
            </p>
          </div>
          <div className="absolute -right-8 -top-8 w-64 h-64 bg-white/5 rounded-full" />
          <div className="absolute -right-4 -bottom-12 w-48 h-48 bg-white/5 rounded-full" />
        </div>

        {popular.length > 0 && (
          <section className="mb-10">
            <h2 className="text-xl font-extrabold mb-4 flex items-center gap-2">
              🔥 Phổ biến nhất
            </h2>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
              {popular.map((food) => (
                <FoodCard key={food.id} food={food} />
              ))}
            </div>
          </section>
        )}

        <div className="flex flex-col sm:flex-row gap-4 mb-6">
          <input
            type="text"
            placeholder="Tìm kiếm món ăn..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="flex-1 border border-zinc-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:border-black transition-colors bg-white"
          />
        </div>

        <div className="flex items-center gap-2 flex-wrap mb-6">
          {CATEGORIES.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-4 py-1.5 rounded-full text-sm font-medium transition-all ${
                selectedCategory === cat
                  ? 'bg-black text-white'
                  : 'bg-white border border-zinc-200 text-zinc-600 hover:border-zinc-400 hover:text-black'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {loading ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="bg-zinc-100 rounded-2xl h-64 animate-pulse" />
            ))}
          </div>
        ) : filtered.length === 0 ? (
          <div className="text-center py-20 text-zinc-400">
            <p className="text-4xl mb-3">🍽️</p>
            <p className="font-medium">Không tìm thấy món nào</p>
          </div>
        ) : (
          <>
            <p className="text-sm text-zinc-400 mb-4">{filtered.length} món</p>
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
              {filtered.map((food) => (
                <FoodCard key={food.id} food={food} />
              ))}
            </div>
          </>
        )}
      </div>
    </>
  )
}
