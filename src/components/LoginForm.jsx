// File: src/components/LoginForm.jsx
import { useState } from 'react';
import client from '../api/client';

// UC002 - POST /api/auth/login
export default function LoginForm({ onLoginSuccess }) {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await client.post('/auth/login', form);
      localStorage.setItem('olms_auth', JSON.stringify(res.data));
      onLoginSuccess(res.data); // { token, username, role, memberId }
    } catch (err) {
      setError(err.response?.data?.message || 'Đăng nhập thất bại');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="col-md-5">
      <div className="card shadow-sm border-0 p-4">
        <h4 className="mb-3">Đăng nhập</h4>
        {error && <div className="alert alert-danger py-2">{error}</div>}
        <input className="form-control mb-3" name="username" placeholder="Username"
          value={form.username} onChange={handleChange} required />
        <input className="form-control mb-3" type="password" name="password" placeholder="Mật khẩu"
          value={form.password} onChange={handleChange} required />
        <button className="btn btn-primary w-100" type="submit">Đăng nhập</button>
      </div>
    </form>
  );
}
