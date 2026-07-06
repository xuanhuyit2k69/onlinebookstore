// File: src/components/LoanList.jsx
import { useEffect, useState } from 'react';
import client from '../api/client';

// UC004 (danh sách) - GET /api/loans | UC005 - PATCH /confirm | UC006 - PATCH /return
export default function LoanList({ refreshKey }) {
  const [loans, setLoans] = useState([]);
  const [error, setError] = useState('');
  const auth = (() => {
    try { return JSON.parse(localStorage.getItem('olms_auth') || 'null'); } catch { return null; }
  })();
  const role = auth?.role;

  const loadLoans = async () => {
    try {
      setError('');
      const res = await client.get('/loans');
      setLoans(res.data);
    } catch (err) {
      console.error('Load loans failed', err);
      setLoans([]);
      setError(err.response?.data?.message || err.message || 'Không thể tải danh sách phiếu mượn');
    }
  };

  useEffect(() => { loadLoans(); }, [refreshKey]);

  const handleConfirm = async (id) => {
    try {
      setError('');
      await client.patch(`/loans/${id}/confirm`);
      loadLoans();
    } catch (err) {
      console.error('Confirm loan failed', err);
      setError(err.response?.data?.message || err.message || 'Xác nhận thất bại');
    }
  };

  const handleReturn = async (id) => {
    await client.patch(`/loans/${id}/return`);
    loadLoans();
  };

  const badgeClass = {
    CHO_XAC_NHAN: 'warning',
    DANG_MUON: 'primary',
    QUA_HAN: 'danger',
    DA_TRA: 'secondary',
    HUY: 'dark',
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h4 className="mb-0">Danh sách phiếu mượn</h4>
          <p className="text-muted mb-0">Xem và xử lý trạng thái phiếu mượn của bạn.</p>
        </div>
        <button className="btn btn-sm btn-outline-secondary" onClick={loadLoans}>Làm mới</button>
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      <div className="table-responsive">
        <table className="table align-middle">
          <thead>
            <tr>
              <th>Thành viên</th>
              <th>Sách</th>
              <th>Ngày mượn</th>
              <th>Hạn trả</th>
              <th>Trạng thái</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {loans.map((l) => (
              <tr key={l.id}>
                <td>{l.memberName}</td>
                <td>{l.bookTitle}</td>
                <td>{l.loanDate}</td>
                <td>{l.dueDate}</td>
                <td><span className={`badge badge-${l.status}`}>{l.status}</span></td>
                <td>
                  {l.status === 'CHO_XAC_NHAN' && role === 'LIBRARIAN' && (
                    <button className="btn btn-sm btn-primary me-1" onClick={() => handleConfirm(l.id)}>Xác nhận</button>
                  )}
                  {(l.status === 'DANG_MUON' || l.status === 'QUA_HAN') && (
                    <button className="btn btn-sm btn-success" onClick={() => handleReturn(l.id)}>Trả sách</button>
                  )}
                </td>
              </tr>
            ))}
            {loans.length === 0 && (
              <tr><td colSpan={6} className="text-center text-muted">Chưa có phiếu mượn nào</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
