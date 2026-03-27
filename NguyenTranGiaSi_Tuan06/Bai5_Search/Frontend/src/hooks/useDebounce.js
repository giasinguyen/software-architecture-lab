import { useState, useEffect } from 'react'

/**
 * Trả về giá trị "đã ổn định" sau khi người dùng ngừng gõ `delay` ms.
 * Component cha re-render mỗi phím → useDebounce chỉ propagate sau delay.
 */
export function useDebounce(value, delay = 300) {
  const [debouncedValue, setDebouncedValue] = useState(value)

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedValue(value), delay)
    // Cleanup: huỷ timer nếu value thay đổi trước khi hết delay
    return () => clearTimeout(timer)
  }, [value, delay])

  return debouncedValue
}
