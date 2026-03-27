/** Bảng hiển thị danh sách sản phẩm — dùng chung cho 3 phương án */
export default function ProductTable({ products, loading, error }) {
  if (error) {
    return (
      <div className="mt-4 p-4 rounded-lg bg-red-50 border border-red-200 text-red-600 text-sm">
        {error}
      </div>
    )
  }

  if (loading) {
    return (
      <div className="mt-8 flex justify-center">
        <div className="w-8 h-8 border-4 border-indigo-300 border-t-indigo-600
                        rounded-full animate-spin" />
      </div>
    )
  }

  if (!products || products.length === 0) {
    return (
      <div className="mt-8 text-center text-gray-400 text-sm">
        Không tìm thấy sản phẩm nào.
      </div>
    )
  }

  return (
    <div className="mt-4 overflow-x-auto rounded-xl border border-gray-200 shadow-sm">
      <table className="min-w-full divide-y divide-gray-100 text-sm">
        <thead className="bg-gray-50 text-gray-500 uppercase text-xs tracking-wide">
          <tr>
            <th className="px-4 py-3 text-left">ID</th>
            <th className="px-4 py-3 text-left">Tên sản phẩm</th>
            <th className="px-4 py-3 text-left">Danh mục</th>
            <th className="px-4 py-3 text-right">Giá (USD)</th>
            <th className="px-4 py-3 text-left">Mô tả</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100 bg-white">
          {products.map(p => (
            <tr key={p.id} className="hover:bg-indigo-50 transition-colors">
              <td className="px-4 py-3 text-gray-400">{p.id}</td>
              <td className="px-4 py-3 font-medium text-gray-800">{p.name}</td>
              <td className="px-4 py-3">
                <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium
                  ${p.category === 'Electronics' ? 'bg-blue-100 text-blue-700'
                  : p.category === 'Food' ? 'bg-green-100 text-green-700'
                  : p.category === 'Clothing' ? 'bg-purple-100 text-purple-700'
                  : 'bg-amber-100 text-amber-700'}`}>
                  {p.category}
                </span>
              </td>
              <td className="px-4 py-3 text-right tabular-nums text-gray-700">
                ${p.price?.toFixed(2)}
              </td>
              <td className="px-4 py-3 text-gray-500 max-w-xs truncate">{p.description}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="px-4 py-2 text-xs text-gray-400 bg-gray-50 border-t">
        {products.length} kết quả
      </div>
    </div>
  )
}
