import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // Убираем rewrite - Spring Boot ожидает пути с /api
        rewrite: (path) => path,
        secure: false,
        ws: true,
      },
    },
  },
  optimizeDeps: {
    include: ['react-hook-form', '@hookform/resolvers'],
  },
});