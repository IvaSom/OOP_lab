// Утилиты для работы с аутентификацией

export interface AuthData {
  token: string | null;
  user: any | null;
}

/**
 * Очищает все данные аутентификации
 */
export const clearAuthData = (): void => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
  sessionStorage.removeItem('authToken');
  sessionStorage.removeItem('user');
};

/**
 * Сохраняет данные аутентификации
 */
export const setAuthData = (token: string, user: any, remember: boolean = false): void => {
  const storage = remember ? localStorage : sessionStorage;
  storage.setItem('authToken', token);
  storage.setItem('user', JSON.stringify(user));
};

/**
 * Получает данные аутентификации
 */
export const getAuthData = (): AuthData => {
  // Проверяем localStorage, затем sessionStorage
  let token = localStorage.getItem('authToken');
  let userStr = localStorage.getItem('user');

  if (!token) {
    token = sessionStorage.getItem('authToken');
    userStr = sessionStorage.getItem('user');
  }

  return {
    token,
    user: userStr ? JSON.parse(userStr) : null
  };
};

/**
 * Проверяет, авторизован ли пользователь
 */
export const isAuthenticated = (): boolean => {
  const { token, user } = getAuthData();
  return !!token && !!user;
};

/**
 * Получает информацию о текущем пользователе
 */
export const getCurrentUser = (): any | null => {
  const { user } = getAuthData();
  return user;
};

/**
 * Получает роль текущего пользователя
 */
export const getUserRole = (): string => {
  const user = getCurrentUser();
  return user?.role || 'GUEST';
};

/**
 * Проверяет, имеет ли пользователь указанную роль
 */
export const hasRole = (role: string): boolean => {
  return getUserRole() === role;
};

/**
 * Проверяет, является ли пользователь администратором
 */
export const isAdmin = (): boolean => {
  return hasRole('ADMIN');
};