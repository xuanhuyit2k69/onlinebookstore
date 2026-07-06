// File: src/components/BookSearch.jsx
import { useEffect, useState } from 'react';
import client from '../api/client';

// UC003 - GET /api/books?keyword=...  |  UC004 - POST /api/loans
export default function BookSearch({ memberId, onLoanCreated }) {
  const [keyword, setKeyword] = useState('');
  const [books, setBooks] = useState([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadBooks = async (kw = '') => {
    try {
      setError('');
      const res = await client.get('/books', { params: { keyword: kw || undefined } });
      const bookContent = Array.isArray(res.data) ? res.data : res.data?.content ?? [];
      setBooks(bookContent);
    } catch (err) {
      console.error('Load books failed', err);
      setBooks([]);
      setError(err.response?.data?.message || err.message || 'Không thể tải danh sách sách');
    }
  };

  useEffect(() => { loadBooks(''); }, []); // load toàn bộ sách khi vào trang

  const handleSearch = (e) => {
    e.preventDefault();
    loadBooks(keyword);
  };

  const handleBorrow = async (bookId) => {
    setMessage('');
    try {
      await client.post('/loans', { memberId, bookId });
      setMessage('Tạo phiếu mượn thành công (trạng thái PENDING, chờ thủ thư xác nhận).');
      loadBooks(keyword);
      onLoanCreated?.();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Mượn sách thất bại');
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 className="mb-0">Tra cứu tài liệu</h4>
      </div>
      <form onSubmit={handleSearch} className="d-flex mb-3" style={{ maxWidth: 600 }}>
        <input className="form-control me-2" placeholder="Tên sách hoặc tác giả..."
          value={keyword} onChange={(e) => setKeyword(e.target.value)} />
        <button className="btn btn-primary">Tìm kiếm</button>
      </form>
      {message && <div className="alert alert-info py-2">{message}</div>}
      {error && <div className="alert alert-danger py-2">{error}</div>}
      <div className="table-responsive">
        <table className="table align-middle">
          <thead>
            <tr><th>Tên sách</th><th>Tác giả</th><th>Còn lại</th><th></th></tr>
          </thead>
          <tbody>
            {books.map((b) => (
              <tr key={b.id}>
                <td><strong>{b.title}</strong></td>
                <td>{b.author}</td>
                <td>{b.availableCopies}</td>
                <td>
                  <button className="btn btn-sm btn-outline-primary"
                    disabled={b.availableCopies === 0 || !memberId}
                    onClick={() => handleBorrow(b.id)}>
                    Mượn
                  </button>
                </td>
              </tr>
            ))}
            {books.length === 0 && (
              <tr><td colSpan={4} className="text-center text-muted">Không có tài liệu phù hợp</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
