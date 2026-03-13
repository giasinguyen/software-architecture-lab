import { useEffect, useMemo, useState } from 'react'
import './App.css'

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

  return (
    <main className="dashboard-shell">
      <div className="bg-noise" aria-hidden="true" />
      <section className="hero-card">
        <p className="eyebrow">Plugin CMS - Layered + Microkernel</p>
        <h1>Feature Overview Dashboard</h1>
        <p className="hero-subtitle">
          Dashboard tong hop plugin status, event flow, workflow backlog va dynamic schema theo
          thoi gian thuc.
        </p>

        <div className="kpi-grid">
          <article className="kpi-card">
            <h3>Total Plugins</h3>
            <p>{pluginSummary.total}</p>
          </article>
          <article className="kpi-card">
            <h3>Active</h3>
            <p>{pluginSummary.active}</p>
          </article>
          <article className="kpi-card">
            <h3>Event Success</h3>
            <p>{percent(eventSummary.avgSuccess)}</p>
          </article>
          <article className="kpi-card">
            <h3>Avg Latency</h3>
            <p>{Math.round(eventSummary.avgLatency)} ms</p>
          </article>
        </div>

        <button
          className="refresh-button"
          type="button"
          onClick={() => fetchOverview(false)}
          disabled={loading || refreshing}
        >
          {refreshing ? 'Dang cap nhat...' : 'Refresh Overview'}
        </button>
      </section>

      {loading && <p className="status-message">Dang tai du lieu overview...</p>}
      {error && <p className="status-message error">{error}</p>}

      <section className="panel-grid">
        <article className="panel">
          <div className="panel-header">
            <h2>Plugin Registry</h2>
            <span>
              INACTIVE: {pluginSummary.inactive} | ERROR: {pluginSummary.error}
            </span>
          </div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Plugin</th>
                  <th>Owner</th>
                  <th>Status</th>
                  <th>Version</th>
                  <th>Last Activated</th>
                  <th>Action</th>
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
                      <td>{plugin.owner}</td>
                      <td>
                        <span className={`status-chip ${plugin.status.toLowerCase()}`}>
                          {plugin.status}
                        </span>
                      </td>
                      <td>{plugin.version}</td>
                      <td>{timestamp(plugin.lastActivatedAt)}</td>
                      <td>
                        <button
                          className="row-action"
                          type="button"
                          onClick={() => updatePluginStatus(plugin.pluginId, !isActive)}
                        >
                          {isActive ? 'Deactivate' : 'Activate'}
                        </button>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </article>

        <article className="panel">
          <div className="panel-header">
            <h2>Feature Landscape</h2>
            <span>{data.features.length} capabilities</span>
          </div>
          <div className="capability-grid">
            {data.features.map((feature) => (
              <div key={feature.featureId} className="capability-card">
                <h3>{feature.featureId}</h3>
                <p>Plugin: {feature.pluginId}</p>
                <p>Category: {feature.category}</p>
                <p>Endpoints: {feature.endpointCount}</p>
                <p>Events: {feature.eventCount}</p>
                <span className={`status-chip ${feature.active ? 'active' : 'inactive'}`}>
                  {feature.active ? 'Enabled' : 'Disabled'}
                </span>
              </div>
            ))}
          </div>
        </article>

        <article className="panel">
          <div className="panel-header">
            <h2>Event Topology</h2>
            <span>{data.events.length} streams</span>
          </div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Event</th>
                  <th>Producer</th>
                  <th>Consumer</th>
                  <th>Success Rate</th>
                  <th>Latency</th>
                </tr>
              </thead>
              <tbody>
                {data.events.map((eventFlow) => (
                  <tr key={`${eventFlow.eventName}-${eventFlow.producerPlugin}`}>
                    <td>{eventFlow.eventName}</td>
                    <td>{eventFlow.producerPlugin}</td>
                    <td>{eventFlow.consumerPlugin}</td>
                    <td>{percent(eventFlow.successRate)}</td>
                    <td>{eventFlow.avgLatencyMs} ms</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </article>

        <article className="panel">
          <div className="panel-header">
            <h2>Workflow and Schema</h2>
          </div>
          <div className="workflow-grid">
            {data.workflow.map((workflowRow) => (
              <div className="workflow-card" key={workflowRow.contentType}>
                <h3>{workflowRow.contentType}</h3>
                <p>Draft: {workflowRow.draftCount}</p>
                <p>Review: {workflowRow.reviewCount}</p>
                <p>Approved: {workflowRow.approvedCount}</p>
                <p>Published: {workflowRow.publishedCount}</p>
              </div>
            ))}
          </div>
          <div className="schema-box">
            <p>Content Types: {data.schema.contentTypeCount}</p>
            <p>Dynamic Fields: {data.schema.dynamicFieldCount}</p>
            <p>Validation Error Rate: {percent(data.schema.validationErrorRate)}</p>
          </div>
        </article>
      </section>
    </main>
  )
}

export default App
