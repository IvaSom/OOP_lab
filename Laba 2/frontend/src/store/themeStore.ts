import { create } from 'zustand';

interface ThemeState {
  darkMode: boolean;
  toggleDarkMode: () => void;
  setDarkMode: (darkMode: boolean) => void;
}

export const useThemeStore = create<ThemeState>((set) => {
  // Проверяем настройки системы и localStorage
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  const savedTheme = localStorage.getItem('theme');

  const initialDarkMode = savedTheme
    ? savedTheme === 'dark'
    : prefersDark;

  // Применяем тему
  if (initialDarkMode) {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }

  return {
    darkMode: initialDarkMode,
    toggleDarkMode: () => set((state) => {
      const newDarkMode = !state.darkMode;
      localStorage.setItem('theme', newDarkMode ? 'dark' : 'light');

      if (newDarkMode) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }

      return { darkMode: newDarkMode };
    }),
    setDarkMode: (darkMode) => {
      set({ darkMode });
      localStorage.setItem('theme', darkMode ? 'dark' : 'light');

      if (darkMode) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    }
  };
});