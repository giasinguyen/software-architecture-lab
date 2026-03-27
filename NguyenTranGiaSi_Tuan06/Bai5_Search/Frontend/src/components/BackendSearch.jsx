import { useState, useEffect } from 'react'
import SearchInput from './SearchInput'
import ProductTable from './ProductTable'
import { useDebounce } from '../hooks/useDebounce'

/**
 * Phương án 2 – Backend search (Spring Data JPA LIKE query)
 *
 * Flow: user gõ → debounce 300ms → fetch /api/products/search?q=
 *       → BE chạy: SELECT * WHERE name LIKE %q% OR …
 *       → MariaDB trả kết quả → BE trả JSON → FE render
 *
 * Ưu điểm : chỉ gửi dữ liệu cần thiết, phù hợp với dataset lớn
 * Nhược điểm: mỗi keystroke (sau debounce) là 1 HTTP round-trip
 */
export default function BackendSearch() {
  const [inputValue, setInputValue] = useState('')
  const [products, setProducts]     = useState([])
  const [loading, setLoading]       = useState(false)
  const [error, setError]           = useState(null)
  const [requestCount, setRequestCount] = useState(0)

  const debouncedQuery = useDebounce(inputValue, 300)
  const isDebouncing   = inputValue !== debouncedQuery

  // ── Gọi API mỗi khi debounced query thay đổi ────────────────────────────
  useEffect(() => {
    const controller = new AbortController()
    setLoading(true)
    setError(null)

    fetch(`/api/products/search?q=${encodeURIComponent(debouncedQuery)}`,
          { signal: controller.signal })
      .then(r => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`)
        return r.json()
      })
      .then(data => {
        setProducts(data)
        setRequestCount(c => c + 1)
      })
      .catch(e => { if (e.name !== 'AbortError') setError(e.message) })
      .finally(() => setLoading(false))

    // Cleanup: huỷ request cũ nếu query thay đổi trước khi nhận response
    return () => controller.abort()
  }, [debouncedQuery])

  return (
    <div>
      {/* Flow diagram */}
      <div className="mb-4 flex items-center gap-2 text-sm flex-wrap">
        <FlowBadge color="green"  label="Browser" />
        <Arrow />
        <FlowBadge color="green"  label="debounce 300ms" />
        <Arrow />
        <FlowBadge color="green"  label="GET /api/products/search?q=" />
        <Arrow />
        <FlowBadge color="green"  label="JPA LIKE query" />
        <Arrow />
        <FlowBadge color="slate"  label="MariaDB" dim />
      </div>

      <SearchInput
        value={inputValue}
        onChange={setInputValue}
        isDebouncing={isDebouncing}
        placeholder="Tìm theo tên, danh mục, mô tả…"
      />

      <p className="mt-2 text-xs text-gray-400">
        Số lần gọi API: <b>{requestCount}</b>
        {isDebouncing && <span className="ml-2 text-amber-500">· đang chờ debounce…</span>}
      </p>

      <ProductTable products={products} loading={loading} error={error} />
    </div>
  )
}

function FlowBadge({ color, label, dim }) {
  const colors = {
    green: 'bg-green-100 text-green-700 border-green-200',
    slate: 'bg-gray-100 text-gray-500 border-gray-200',
  }
  return (
    <span className={`px-2 py-1 rounded-md border text-xs font-mono
                      ${colors[color] ?? colors.slate}
                      ${dim ? 'opacity-60' : ''}`}>
      {label}
    </span>
  )
}
function Arrow() {
  return <span className="text-gray-300 font-bold">→</span>
}
