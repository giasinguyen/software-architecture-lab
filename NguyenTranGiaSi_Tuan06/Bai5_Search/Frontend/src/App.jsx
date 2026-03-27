import { useState } from 'react'
import ClientSearch    from './components/ClientSearch'
import BackendSearch   from './components/BackendSearch'
import StoredProcSearch from './components/StoredProcSearch'

const TABS = [
  {
    id: 'client',
    label: '① Client-side',
    sub: 'Tải hết → filter tại browser',
    activeClass: 'border-blue-500 text-blue-600',
    component: <ClientSearch />,
  },
  {
    id: 'backend',
    label: '② Backend (JPA)',
    sub: 'Debounce → LIKE query',
    activeClass: 'border-green-500 text-green-600',
    component: <BackendSearch />,
  },
  {
    id: 'sp',
    label: '③ Stored Procedure',
    sub: 'Debounce → CALL sp_…()',
    activeClass: 'border-purple-500 text-purple-600',
    component: <StoredProcSearch />,
  },
]

export default function App() {
  const [activeId, setActiveId] = useState('client')
  const active = TABS.find(t => t.id === activeId)

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-indigo-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 shadow-sm">
        <div className="max-w-5xl mx-auto px-6 py-4">
          <h1 className="text-xl font-semibold text-gray-800">
            🔍 Search Demo
          </h1>
          <p className="text-sm text-gray-500 mt-0.5">
            ReactJS + Spring Boot + MariaDB · debounce 300ms
          </p>
        </div>
      </header>

      {/* Tabs */}
      <div className="max-w-5xl mx-auto px-6 mt-6">
        <div className="flex gap-1 border-b border-gray-200">
          {TABS.map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveId(tab.id)}
              className={`px-5 py-3 text-sm font-medium border-b-2 transition-colors
                ${
                  activeId === tab.id
                    ? tab.activeClass + ' bg-white'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
            >
              <div>{tab.label}</div>
              <div className="text-xs font-normal opacity-70">{tab.sub}</div>
            </button>
          ))}
        </div>

        {/* Content */}
        <div className="bg-white rounded-b-2xl rounded-tr-2xl shadow p-6 mb-10">
          {active?.component}
        </div>
      </div>
    </div>
  )
}
