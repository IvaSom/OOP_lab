import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { toast } from 'react-toastify';
import { useAuthStore } from '../store/authStore';

const api: AxiosInstance = axios.create({
  baseURL: '/api',
  withCredentials: true,
});

// Interceptor для добавления токена авторизации
api.interceptors.request.use((config: AxiosRequestConfig) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers = config.headers || {};
    config.headers['Authorization'] = `Basic ${token}`;
  }
  return config;
});

// Interceptor для обработки ошибок
api.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          toast.error('Сессия истекла. Пожалуйста, войдите снова.');
          useAuthStore.getState().logout();
          window.location.href = '/login';
          break;
        case 403:
          toast.error('У вас нет прав для выполнения этой операции');
          break;
        case 400:
          toast.error('Неверные данные запроса');
          break;
        case 404:
          toast.error('Ресурс не найден');
          break;
        case 500:
          toast.error('Внутренняя ошибка сервера');
          break;
        default:
          toast.error(`Ошибка: ${error.response.status}`);
      }
    } else if (error.request) {
      toast.error('Нет соединения с сервером');
    } else {
      toast.error('Ошибка при выполнении запроса');
    }
    return Promise.reject(error);
  }
);

export default api;