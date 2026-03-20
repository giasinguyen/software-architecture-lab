import { createContext, useContext, useState } from 'react'

const CartContext = createContext()

export function CartProvider({ children }) {
  const [items, setItems] = useState([])
  const [isOpen, setIsOpen] = useState(false)

  const addItem = (food) => {
    setItems((prev) => {
      const existing = prev.find((i) => i.food.id === food.id)
      if (existing) {
        return prev.map((i) =>
          i.food.id === food.id ? { ...i, quantity: i.quantity + 1 } : i
        )
      }
      return [...prev, { food, quantity: 1 }]
    })
  }

  const removeItem = (foodId) => {
    setItems((prev) => prev.filter((i) => i.food.id !== foodId))
  }

  const updateQuantity = (foodId, quantity) => {
    if (quantity <= 0) {
      removeItem(foodId)
      return
    }
    setItems((prev) =>
      prev.map((i) => (i.food.id === foodId ? { ...i, quantity } : i))
    )
  }

  const clearCart = () => setItems([])

  const total = items.reduce((sum, i) => sum + Number(i.food.price) * i.quantity, 0)
  const itemCount = items.reduce((sum, i) => sum + i.quantity, 0)

  return (
    <CartContext.Provider
      value={{ items, addItem, removeItem, updateQuantity, clearCart, total, itemCount, isOpen, setIsOpen }}
    >
      {children}
    </CartContext.Provider>
  )
}

export const useCart = () => useContext(CartContext)
