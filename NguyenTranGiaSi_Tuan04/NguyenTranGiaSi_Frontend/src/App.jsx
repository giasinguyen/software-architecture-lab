import { useEffect, useMemo, useState } from 'react'
import './App.css'
import CmsArticles from './CmsArticles'

const overviewEndpoints = {
  plugins: '/api/overview/plugins',
  features: '/api/overview/features',
  events: '/api/overview/events',
  workflow: '/api/overview/workflow',
  schema: '/api/overview/schema',
}

const pluginActions = {
  activate: (pluginId) => `/api/plugins/${pluginId}/activate`,
  deactivate: (pluginId) => `/api/plugins/${pluginId}/deactivate`,
}

const fallbackData = {
  plugins: [],
  features: [],
  events: [],
  workflow: [],
  schema: {
    contentTypeCount: 0,
    dynamicFieldCount: 0,
    validationErrorRate: 0,
  },
}

function percent(value) {
  return `${Number(value).toFixed(1)}%`
}

function timestamp(value) {
  if (!value) {
    return 'N/A'
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }

  return date.toLocaleString('vi-VN')
}

function App() {
  const [activeTab, setActiveTab] = useState('dashboard')
  const [data, setData] = useState(fallbackData)
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)
  const [error, setError] = useState('')

  const pluginSummary = useMemo(() => {
    const total = data.plugins.length
    const active = data.plugins.filter((plugin) => plugin.status === 'ACTIVE').length
    const inactive = data.plugins.filter((plugin) => plugin.status === 'INACTIVE').length
    const errorCount = data.plugins.filter((plugin) => plugin.status === 'ERROR').length
    return { total, active, inactive, error: errorCount }
  }, [data.plugins])

  const eventSummary = useMemo(() => {
    if (data.events.length === 0) {
      return { avgSuccess: 0, avgLatency: 0 }
    }

    const avgSuccess =
      data.events.reduce((acc, eventFlow) => acc + eventFlow.successRate, 0) / data.events.length
    const avgLatency =
      data.events.reduce((acc, eventFlow) => acc + eventFlow.avgLatencyMs, 0) / data.events.length

    return {
      avgSuccess,
      avgLatency,
    }
  }, [data.events])

  const fetchOverview = async (showLoading = false) => {
    if (showLoading) {
      setLoading(true)
    } else {
      setRefreshing(true)
    }

    setError('')

    try {
      const responses = await Promise.all(
        Object.values(overviewEndpoints).map((endpoint) => fetch(endpoint)),
      )

      const failed = responses.find((response) => !response.ok)
      if (failed) {
        throw new Error(`Overview API failed with status ${failed.status}`)
      }

      const [plugins, features, events, workflow, schema] = await Promise.all(
        responses.map((response) => response.json()),
      )

      setData({ plugins, features, events, workflow, schema })
    } catch (requestError) {
      setError('Khong the ket noi backend overview. Hay chay backend o cong 8080 va thu refresh lai.')
      setData(fallbackData)
      // eslint-disable-next-line no-console
      console.error(requestError)
    } finally {
      setLoading(false)
      setRefreshing(false)
    }
  }

  const updatePluginStatus = async (pluginId, shouldActivate) => {
    try {
      const endpoint = shouldActivate
        ? pluginActions.activate(pluginId)
        : pluginActions.deactivate(pluginId)

      const response = await fetch(endpoint, { method: 'POST' })
      if (!response.ok) {
        throw new Error(`Plugin action failed with status ${response.status}`)
      }

      await fetchOverview(false)
    } catch (requestError) {
      setError('Khong the cap nhat trang thai plugin. Vui long thu lai.')
      // eslint-disable-next-line no-console
      console.error(requestError)
    }
  }

  useEffect(() => {
    fetchOverview(true)
  }, [])

  const isPluginActive = (id) => data.plugins.some((p) => p.pluginId === id && p.status === 'ACTIVE')

  return (
    <main className="dashboard-shell">
      <div className="bg-noise" aria-hidden="true" />
      <nav className="top-nav">
        <button className={activeTab === 'dashboard' ? 'nav-active' : ''} onClick={() => setActiveTab('dashboard')}>Overview Dashboard</button>
        <button className={activeTab === 'cms' ? 'nav-active' : ''} onClick={() => setActiveTab('cms')}>CMS Bài viết</button>
      </nav>

      {activeTab === 'dashboard' ? (
      <>
      <section className="hero-card">
        <div className="hero-header">
          <div>
            <p className="eyebrow">Kiến trúc Layered + Microkernel</p>
            <h1>Plugin CMS Dashboard</h1>
            <p className="hero-subtitle">
              Hệ thống được chia làm 3 thành phần rõ rệt: 1) Core Registry để quản lý lifecycle, 
              2) Event Bus để truyền thông điệp, 3) 3 plugin mở rộng nghiệp vụ.
            </p>
          </div>
          <button
            className="refresh-button"
            type="button"
            onClick={() => fetchOverview(false)}
            disabled={loading || refreshing}
          >
            {refreshing ? 'Đang cập nhật...' : 'Refresh Data'}
          </button>
        </div>

        <div className="kpi-grid">
          <article className="kpi-card">
            <h3>Tổng Plugins</h3>
            <p>{pluginSummary.total}</p>
          </article>
          <article className="kpi-card">
            <h3>Đang Hoạt Động (Active)</h3>
            <p>{pluginSummary.active}</p>
          </article>
          <article className="kpi-card">
            <h3>Tỉ Lệ Gửi Event Thành Công</h3>
            <p>{percent(eventSummary.avgSuccess)}</p>
          </article>
          <article className="kpi-card">
            <h3>Độ Trễ Event Trung Bình</h3>
            <p>{Math.round(eventSummary.avgLatency)} ms</p>
          </article>
        </div>
      </section>

      {loading && <p className="status-message">Đang tải cấu trúc hệ thống...</p>}
      {error && <p className="status-message error">{error}</p>}

      {/* MỤC 1: QUẢN LÝ KERNEL (PLUGIN REGISTRY) */}
      <section className="feature-section">
        <div className="section-header">
          <div className="section-title-wrap">
            <span className="section-number">1</span>
            <div>
              <h2>Core Microkernel (Plugin Registry)</h2>
              <p className="muted">Module quản lý vòng đời plugins. Cho phép đóng/mở tính năng lập tức tại runtime mà không cần khởi động lại server.</p>
            </div>
          </div>
        </div>
        <div className="panel full-width">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID Plugin (Module)</th>
                  <th>Trạng Thái</th>
                  <th>Phiên Bản & Kỹ Sư Phụ Trách</th>
                  <th>Giao Diện Điều Khiển (Activator)</th>
                </tr>
              </thead>
              <tbody>
                {data.plugins.map((plugin) => {
                  const isActive = plugin.status === 'ACTIVE'
                  return (
                    <tr key={plugin.pluginId}>
                      <td>
                        <strong>{plugin.pluginId}</strong>
                        <p className="muted">{plugin.description}</p>
                      </td>
                      <td>
                        <span className={`status-chip ${plugin.status.toLowerCase()}`}>
                          {plugin.status}
                        </span>
                      </td>
                      <td>
                        {plugin.version} • {plugin.owner}<br/>
                        <small className="muted">Cập nhật lúc: {timestamp(plugin.lastActivatedAt)}</small>
                      </td>
                      <td>
                        <button
                          className="row-action"
                          type="button"
                          onClick={() => updatePluginStatus(plugin.pluginId, !isActive)}
                        >
                          {isActive ? 'Tắt (Deactivate)' : 'Bật (Activate)'}
                        </button>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </div>
      </section>

      {/* MỤC 2: GIAO TIẾP EVENT BUS */}
      <section className="feature-section">
        <div className="section-header">
          <div className="section-title-wrap">
            <span className="section-number">2</span>
            <div>
              <h2>Infra Event Bus (Message Router)</h2>
              <p className="muted">Các plugin không gọi nhau trực tiếp tạo tính gắn kết lỏng (decoupling), mà Publisher đẩy sự kiện vào Bus để Subscriber bắt lấy xử lý.</p>
            </div>
          </div>
        </div>
        <div className="panel full-width">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Chủ Đề Sự Kiện (Event Topic)</th>
                  <th>Nguồn Phát (Producer)</th>
                  <th>Nơi Nhận (Consumer)</th>
                  <th>Tỉ Lệ Thành Công</th>
                  <th>Độ Trễ</th>
                </tr>
              </thead>
              <tbody>
                {data.events.map((eventFlow) => (
                  <tr key={`${eventFlow.eventName}-${eventFlow.producerPlugin}`}>
                    <td><span className="code-chip">{eventFlow.eventName}</span></td>
                    <td><strong>{eventFlow.producerPlugin}</strong></td>
                    <td><strong>{eventFlow.consumerPlugin}</strong></td>
                    <td>{percent(eventFlow.successRate)}</td>
                    <td>{eventFlow.avgLatencyMs} ms</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>

      {/* MỤC 3: HỆ SINH THÁI TÍNH NĂNG (THE PLUGINS) */}
      <section className="feature-section">
        <div className="section-header">
          <div className="section-title-wrap">
            <span className="section-number">3</span>
            <div>
              <h2>Hệ Sinh Thái 3 Năng Lực (Business Plugins)</h2>
              <p className="muted">Mỗi ô bên dưới là một hộp cát (Sandbox). Khi tắt Plugin ở bảng 1 phía trên, nghiệp vụ tương ứng bên dưới tự động khóa kết nối.</p>
            </div>
          </div>
        </div>
        
        <div className="ecosystem-grid">
          {/* CONTENT EDITOR */}
          <article className={`eco-card ${!isPluginActive('content-editor') ? 'inactive-card' : ''}`}>
            <div className="eco-header">
                <h3>1. Content Editor</h3>
                <span className={`status-dot ${isPluginActive('content-editor') ? 'on' : 'off'}`}></span>
            </div>
            {isPluginActive('content-editor') ? (
              <div className="eco-content">
                <p>Mô-đun soạn thảo văn bản, hỗ trợ sinh Rich Text và quản lý thẻ Media (hình ảnh, video).</p>
                <div className="stats-box">
                  <p>✓ Đã đăng ký {data.features.find(f => f.pluginId === 'content-editor')?.endpointCount || 0} API Endpoints gọi vào nhân.</p>
                  <p>✓ Khai báo {data.features.find(f => f.pluginId === 'content-editor')?.eventCount || 0} Schema Event Types.</p>
                </div>
              </div>
            ) : (
              <div className="eco-overlay">Tính năng Core đã ngắt kết nối</div>
            )}
          </article>

          {/* WORKFLOW ENGINE */}
          <article className={`eco-card ${!isPluginActive('workflow-engine') ? 'inactive-card' : ''}`}>
            <div className="eco-header">
                <h3>2. Workflow Engine</h3>
                <span className={`status-dot ${isPluginActive('workflow-engine') ? 'on' : 'off'}`}></span>
            </div>
            {isPluginActive('workflow-engine') ? (
              <div className="eco-content">
                <p>Khối Quản lý luồng trạng thái máy (State Machine) phục vụ Duyệt bài và Tự động xuất bản.</p>
                <div className="stats-box limit-scroll">
                  {data.workflow.map(w => (
                      <div key={w.contentType} className="mini-stat">
                        <strong>{w.contentType}</strong>
                        <div>Nháp: {w.draftCount} | Chờ Duyệt: {w.reviewCount} <br/>Đã Xuất: {w.publishedCount}</div>
                      </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="eco-overlay">Tính năng Core đã ngắt kết nối</div>
            )}
          </article>

          {/* DYNAMIC SCHEMA */}
          <article className={`eco-card ${!isPluginActive('dynamic-schema') ? 'inactive-card' : ''}`}>
            <div className="eco-header">
                <h3>3. Dynamic Schema</h3>
                <span className={`status-dot ${isPluginActive('dynamic-schema') ? 'on' : 'off'}`}></span>
            </div>
            {isPluginActive('dynamic-schema') ? (
              <div className="eco-content">
                <p>Hệ thống định nghĩa siêu dữ liệu (Meta-fields) cho phép mở rộng Database không cần migrate.</p>
                <div className="stats-box">
                  <p>Số định dạng bài (Types): {data.schema.contentTypeCount}</p>
                  <p>Trường Schema động: {data.schema.dynamicFieldCount}</p>
                  <p>Tỉ lệ lỗi Data Validation: <strong>{percent(data.schema.validationErrorRate)}</strong></p>
                </div>
              </div>
            ) : (
              <div className="eco-overlay">Tính năng Core đã ngắt kết nối</div>
            )}
          </article>
        </div>
      </section>
      </>
      ) : (
        <CmsArticles />
      )}
    </main>
  )
}

export default App
