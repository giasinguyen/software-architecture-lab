/**
 * Ô tìm kiếm dùng chung cho cả 3 phương án.
 * `isDebouncing` = người dùng đang gõ nhưng debounce chưa fire.
 */
export default function SearchInput({ value, onChange, isDebouncing, placeholder }) {
  return (
    <div className="relative">
      <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none">
        {isDebouncing ? (
          <svg className="w-5 h-5 text-amber-400 animate-spin"
               xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10"
                    stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor"
                  d="M4 12a8 8 0 018-8v8H4z" />
          </svg>
        ) : (
          <svg className="w-5 h-5 text-gray-400"
               xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
               stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z" />
          </svg>
        )}
      </div>
      <input
        type="text"
        value={value}
        onChange={e => onChange(e.target.value)}
        placeholder={placeholder ?? 'Tìm kiếm...'}
        className="w-full pl-10 pr-4 py-3 rounded-xl border border-gray-200 bg-white
                   shadow-sm text-gray-800 placeholder-gray-400
                   focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent
                   transition"
      />
      {isDebouncing && (
        <span className="absolute right-3 top-1/2 -translate-y-1/2
                         text-xs text-amber-500 font-medium">
          chờ 300ms…
        </span>
      )}
    </div>
  )
}
