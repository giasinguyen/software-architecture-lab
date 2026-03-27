import { useState, useEffect, useMemo } from 'react'
import SearchInput from './SearchInput'
import ProductTable from './ProductTable'
import { useDebounce } from '../hooks/useDebounce'

/**
 * Phương án 1 – Client-side search
 *
 * Flow: mount → fetch /api/products/all (1 lần duy nhất)
 *       → lưu toàn bộ vào state
 *       → debounce input → filter trong JS → render
 *
 * Ưu điểm : không có thêm API call khi tìm kiếm → rất nhanh
 * Nhược điểm: tải toàn bộ dữ liệu lên FE → không phù hợp khi dữ liệu lớn
 */
export default function ClientSearch() {
  const [allProducts, setAllProducts]   = useState([])
  const [loading, setLoading]           = useState(true)
  const [error, setError]               = useState(null)
  const [inputValue, setInputValue]     = useState('')

  // debounce 300ms: chỉ tính lại kết quả sau khi ngừng gõ
  const debouncedQuery = useDebounce(inputValue, 300)
  const isDebouncing   = inputValue !== debouncedQuery

  // ── Load toàn bộ dữ liệu 1 lần khi mount ─────────────────────────────────
  useEffect(() => {
    fetch('/api/products/all')
      .then(r => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`)
        return r.json()
      })
      .then(data => setAllProducts(data))
      .catch(e  => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  // ── Filter tại client bằng JS thuần ─────────────────────────────────────
  const results = useMemo(() => {
    const q = debouncedQuery.toLowerCase().trim()
    if (!q) return allProducts
    return allProducts.filter(p =>
      p.name.toLowerCase().includes(q)       ||
      p.description?.toLowerCase().includes(q) ||
      p.category?.toLowerCase().includes(q)
    )
  }, [debouncedQuery, allProducts])

  return (
    <div>
      {/* Flow diagram */}
      <div className="mb-4 flex items-center gap-2 text-sm flex-wrap">
        <FlowBadge color="blue"   label="Browser"    />
        <Arrow />
        <FlowBadge color="slate"  label="fetch /api/products/all  (1×)" dim />
        <Arrow />
        <FlowBadge color="blue"   label="JS .filter()" />
        <Arrow />
        <FlowBadge color="blue"   label="Render" />
      </div>

      <SearchInput
        value={inputValue}
        onChange={setInputValue}
        isDebouncing={isDebouncing}
        placeholder="Tìm theo tên, danh mục, mô tả…"
      />

      {!loading && !error && (
        <p className="mt-2 text-xs text-gray-400">
          Đã tải <b>{allProducts.length}</b> sản phẩm · tìm kiếm tại browser
        </p>
      )}

      <ProductTable products={results} loading={loading} error={error} />
    </div>
  )
}

function FlowBadge({ color, label, dim }) {
  const colors = {
    blue:  'bg-blue-100 text-blue-700 border-blue-200',
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
