import { create } from 'zustand';
import { UserDTO } from '../types';

interface AuthState {
  user: UserDTO | null;
  isAuthenticated: boolean;
  login: (user: UserDTO) => void;
  logout: () => void;
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
  setUser: (user) => set({ user, isAuthenticated: !!user }),
}));