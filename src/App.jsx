// File: src/App.jsx
import { useEffect, useState } from 'react';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import BookSearch from './components/BookSearch';
import LoanList from './components/LoanList';

const getStoredAuth = () => {
  try {
    return JSON.parse(localStorage.getItem('olms_auth') || 'null');
  } catch {
    return null;
  }
};

export default function App() {
  const [auth, setAuth] = useState(getStoredAuth); // { token, username, role, memberId }
  const [tab, setTab] = useState('search');
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    if (auth) {
      localStorage.setItem('olms_auth', JSON.stringify(auth));
    } else {
      localStorage.removeItem('olms_auth');
    }
  }, [auth]);

  if (!auth) {
    return (
      <div className="container mt-4">
        <div className="text-center mb-4">
          <h2 className="mb-2">OLMS - Hệ thống Quản lý Thư viện</h2>
          <p className="text-muted">Tra cứu sách, mượn tài liệu và quản lý phiếu mượn một cách nhanh chóng.</p>
        </div>
        <div className="row g-4">
          <LoginForm onLoginSuccess={setAuth} />
          <RegisterForm />
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="mb-1">Xin chào, {auth.username}</h3>
          <small className="text-muted">Vai trò: {auth.role}</small>
        </div>
        <button className="btn btn-outline-danger btn-sm" onClick={() => setAuth(null)}>Đăng xuất</button>
      </div>
      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <button className={`nav-link ${tab === 'search' ? 'active' : ''}`} onClick={() => setTab('search')}>
            Tra cứu &amp; Mượn sách
          </button>
        </li>
        <li className="nav-item">
          <button className={`nav-link ${tab === 'loans' ? 'active' : ''}`} onClick={() => setTab('loans')}>
            Phiếu mượn
          </button>
        </li>
      </ul>
      <div>
        {tab === 'search' && (
          <BookSearch memberId={auth.memberId} onLoanCreated={() => setRefreshKey((k) => k + 1)} />
        )}
        {tab === 'loans' && <LoanList refreshKey={refreshKey} />}
      </div>
    </div>
  );
}
