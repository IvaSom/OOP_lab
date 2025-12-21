import { create } from 'zustand';
import { UserDTO } from '../types';
import { logout as apiLogout } from '../api/authApi';

interface AuthState {
  user: UserDTO | null;
  isAuthenticated: boolean;
  login: (user: UserDTO) => void;
  logout: () => Promise<void>;
  setUser: (user: UserDTO | null) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: !!localStorage.getItem('authToken'),
  login: (user) => set({ user, isAuthenticated: true }),
  logout: () => {
    localStorage.removeItem('authToken');
    set({ user: null, isAuthenticated: false });
  },

logout: async () => {
    try {
      await apiLogout(); // Вызываем API для выхода на сервере
    } catch (error) {
      console.error('Ошибка при выходе:', error);
    } finally {
      // Всегда очищаем локальное состояние
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      set({ user: null, isAuthenticated: false });
    }
  },
  setUser: (user) => set({ user, isAuthenticated: !!user }),
}));