import api from './api';
import { UserAuthDTO, UserCreateDTO, UserDTO } from '../types';
export const registerUser = async (userData: UserCreateDTO): Promise<UserDTO> => {
  // Убедимся, что роль всегда передается
  const registrationData = {
    ...userData,
    role: userData.role || 'USER' // По умолчанию USER
  };

  const response = await api.post('/users/register', registrationData);
  return response.data;
};

export const checkLoginExists = async (login: string): Promise<boolean> => {
  const response = await api.post('/users/check-login', null, {
    params: { login }
  });
  return response.data;
};

export const checkEmailExists = async (email: string): Promise<boolean> => {
  const response = await api.post('/users/check-email', null, {
    params: { email }
  });
  return response.data;
};

export const login = async (credentials: UserAuthDTO): Promise<void> => {
  const token = btoa(`${credentials.login}:${credentials.password}`);
  localStorage.setItem('authToken', token);

  // Проверяем аутентификацию
  await api.get('/users');
};