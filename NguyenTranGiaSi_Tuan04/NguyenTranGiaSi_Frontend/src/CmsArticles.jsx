import { useState, useEffect } from 'react';
import './App.css';

const API_URL = '/api/articles';

export default function CmsArticles() {
  const [articles, setArticles] = useState([]);
  const [editingArticle, setEditingArticle] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchArticles();
  }, []);

  const fetchArticles = async () => {
    setLoading(true);
    try {
      const res = await fetch(API_URL);
      const data = await res.json();
      setArticles(data || []);
    } catch (e) {
      console.error("Lỗi lấy bài viết:", e);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const isNew = !editingArticle.id;
    const url = isNew ? API_URL : `${API_URL}/${editingArticle.id}`;
    const method = isNew ? "POST" : "PUT";
    
    try {
      const res = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(editingArticle),
      });
      if (res.ok) {
        setEditingArticle(null);
        fetchArticles();
      }
    } catch (error) {
      console.error(error);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm("Xóa bài viết này?")) return;
    try {
      await fetch(`${API_URL}/${id}`, { method: "DELETE" });
      fetchArticles();
    } catch (error) {
      console.error(error);
    }
  };

  if (editingArticle != null) {
    return (
      <div className="panel full-width">
        <h2>{editingArticle.id ? "Sửa bài viết" : "Đăng bài viết mới"}</h2>
        <form onSubmit={handleSave} className="article-form">
          <div className="form-group">
            <label>Tiêu đề</label>
            <input 
              required
              type="text" 
              value={editingArticle.title || ''} 
              onChange={e => setEditingArticle({...editingArticle, title: e.target.value})} 
            />
          </div>
          <div className="form-group">
            <label>Trạng thái</label>
            <select 
              value={editingArticle.status || 'DRAFT'} 
              onChange={e => setEditingArticle({...editingArticle, status: e.target.value})}
            >
              <option value="DRAFT">Nháp (Draft)</option>
              <option value="PUBLISHED">Xuất bản (Published)</option>
            </select>
          </div>
          <div className="form-group">
            <label>Nội dung</label>
            <textarea 
              required
              rows="10" 
              value={editingArticle.content || ''} 
              onChange={e => setEditingArticle({...editingArticle, content: e.target.value})} 
            />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn-primary">Lưu</button>
            <button type="button" onClick={() => setEditingArticle(null)} className="btn-secondary">Hủy</button>
          </div>
        </form>
      </div>
    );
  }

  return (
    <div className="feature-section">
      <div className="section-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2>Quản lý bài viết (CMS)</h2>
          <p className="muted">Thêm, sửa, xóa các bài viết trong hệ thống.</p>
        </div>
        <button className="row-action" onClick={() => setEditingArticle({})}>+ Đăng Bài Mới</button>
      </div>

      <div className="panel full-width">
        {loading ? <p>Đang tải...</p> : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Tiêu Đề</th>
                  <th>Trạng Thái</th>
                  <th>Ngày Cập Nhật</th>
                  <th>Thao Tác</th>
                </tr>
              </thead>
              <tbody>
                {articles.length === 0 ? (
                  <tr><td colSpan="5" style={{textAlign: 'center'}}>Chưa có bài viết nào</td></tr>
                ) : articles.map(art => (
                  <tr key={art.id}>
                    <td>{art.id}</td>
                    <td><strong>{art.title}</strong></td>
                    <td><span className={`status-chip ${art.status === 'PUBLISHED' ? 'active' : 'inactive'}`}>{art.status}</span></td>
                    <td>{new Date(art.updatedAt || art.createdAt).toLocaleString('vi-VN')}</td>
                    <td>
                      <button className="btn-sm" onClick={() => setEditingArticle(art)}>Sửa</button>
                      <button className="btn-sm text-danger" onClick={() => handleDelete(art.id)}>Xóa</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}