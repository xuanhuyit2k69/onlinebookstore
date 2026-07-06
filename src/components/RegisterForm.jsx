// File: src/components/RegisterForm.jsx
import { useState } from 'react';
import client from '../api/client';

// UC001 - POST /api/auth/register
export default function RegisterForm() {
  const [form, setForm] = useState({ fullName: '', email: '', username: '', password: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      const res = await client.post('/auth/register', form);
      setSuccess(`Tạo tài khoản "${res.data.username}" thành công, hãy đăng nhập.`);
      setForm({ fullName: '', email: '', username: '', password: '' });
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Đăng ký thất bại');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="col-md-5">
      <div className="card shadow-sm border-0 p-4">
        <h4 className="mb-3">Đăng ký tài khoản</h4>
        {error && <div className="alert alert-danger py-2">{error}</div>}
        {success && <div className="alert alert-success py-2">{success}</div>}
        <input className="form-control mb-3" name="fullName" placeholder="Họ tên"
          value={form.fullName} onChange={handleChange} required />
        <input className="form-control mb-3" type="email" name="email" placeholder="Email"
          value={form.email} onChange={handleChange} required />
        <input className="form-control mb-3" name="username" placeholder="Username"
          value={form.username} onChange={handleChange} required />
        <input className="form-control mb-3" type="password" name="password" placeholder="Mật khẩu"
          value={form.password} onChange={handleChange} required />
        <button className="btn btn-success w-100" type="submit">Đăng ký</button>
      </div>
    </form>
  );
}
