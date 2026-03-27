import { useState, useEffect } from 'react'
import SearchInput from './SearchInput'
import ProductTable from './ProductTable'
import { useDebounce } from '../hooks/useDebounce'

/**
 * Phương án 3 – Stored Procedure search
 *
 * Flow: user gõ → debounce 300ms → fetch /api/products/search-sp?q=
 *       → BE gọi: CALL sp_search_products(p_keyword)
 *       → MariaDB thực thi SP, trả result set → BE map → JSON → FE render
 *
 * Ưu điểm : logic search đóng gói trong DB, tái dụng được từ nhiều BE khác nhau
 *            DBA có thể tối ưu (index, execution plan) độc lập với code BE
 * Nhược điểm: khó version-control SP, debugging phức tạp hơn
 */
export default function StoredProcSearch() {
  const [inputValue, setInputValue] = useState('')
  const [products, setProducts]     = useState([])
  const [loading, setLoading]       = useState(false)
  const [error, setError]           = useState(null)
  const [requestCount, setRequestCount] = useState(0)

  const debouncedQuery = useDebounce(inputValue, 300)
  const isDebouncing   = inputValue !== debouncedQuery

  useEffect(() => {
    const controller = new AbortController()
    setLoading(true)
    setError(null)

    fetch(`/api/products/search-sp?q=${encodeURIComponent(debouncedQuery)}`,
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

    return () => controller.abort()
  }, [debouncedQuery])

  return (
    <div>
      {/* Flow diagram */}
      <div className="mb-4 flex items-center gap-2 text-sm flex-wrap">
        <FlowBadge color="purple" label="Browser" />
        <Arrow />
        <FlowBadge color="purple" label="debounce 300ms" />
        <Arrow />
        <FlowBadge color="purple" label="GET /api/products/search-sp?q=" />
        <Arrow />
        <FlowBadge color="purple" label="EntityManager.createNamedStoredProcedureQuery" />
        <Arrow />
        <FlowBadge color="slate"  label="CALL sp_search_products()" dim />
        <Arrow />
        <FlowBadge color="slate"  label="MariaDB" dim />
      </div>

      {/* SP definition box */}
      <pre className="mb-4 p-3 rounded-lg bg-gray-900 text-green-400 text-xs
                      overflow-x-auto leading-relaxed">{
`-- sp_search_products được tạo lúc Spring Boot khởi động (DatabaseInitializer)
CREATE PROCEDURE sp_search_products(IN p_keyword VARCHAR(255))
BEGIN
    SELECT * FROM products
    WHERE name        LIKE CONCAT('%', p_keyword, '%')
       OR description LIKE CONCAT('%', p_keyword, '%')
       OR category    LIKE CONCAT('%', p_keyword, '%');
END`
      }</pre>

      <SearchInput
        value={inputValue}
        onChange={setInputValue}
        isDebouncing={isDebouncing}
        placeholder="Tìm theo tên, danh mục, mô tả…"
      />

      <p className="mt-2 text-xs text-gray-400">
        Số lần gọi SP: <b>{requestCount}</b>
        {isDebouncing && <span className="ml-2 text-amber-500">· đang chờ debounce…</span>}
      </p>

      <ProductTable products={products} loading={loading} error={error} />
    </div>
  )
}

function FlowBadge({ color, label, dim }) {
  const colors = {
    purple: 'bg-purple-100 text-purple-700 border-purple-200',
    slate:  'bg-gray-100 text-gray-500 border-gray-200',
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
