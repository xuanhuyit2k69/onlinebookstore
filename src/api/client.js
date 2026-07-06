// File: src/api/client.js
import axios from 'axios';

const getStoredAuth = () => {
  try {
    return JSON.parse(localStorage.getItem('olms_auth') || 'null');
  } catch {
    return null;
  }
};

const baseURL = import.meta.env.VITE_API_URL || '/api';

const client = axios.create({ baseURL });

client.interceptors.request.use((config) => {
  const auth = getStoredAuth();
  const token = auth?.accessToken || auth?.token;
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

client.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      const auth = getStoredAuth();
      if (auth?.refreshToken) {
        original._retry = true;
        try {
          const { data } = await axios.post(`${baseURL}/auth/refresh`, {
            refreshToken: auth.refreshToken,
          });
          const updated = {
            ...auth,
            accessToken: data.accessToken,
            token: data.accessToken,
            refreshToken: data.refreshToken,
            expiresIn: data.expiresIn,
          };
          localStorage.setItem('olms_auth', JSON.stringify(updated));
          original.headers.Authorization = `Bearer ${data.accessToken}`;
          return client(original);
        } catch {
          localStorage.removeItem('olms_auth');
        }
      }
    }
    return Promise.reject(error);
  }
);

export default client;
