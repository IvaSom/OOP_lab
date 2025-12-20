import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import { getAnalFunctions, getTabFunctions, getCompFunctions } from './api/functionApi';
import { useFunctionStore } from './store/functionStore';
import MainPage from './pages/MainPage';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import FunctionsPage from './pages/functions/FunctionsPage';
import AnalyticFunctionPage from './pages/functions/AnalyticFunctionPage';
import TabulatedFunctionPage from './pages/functions/TabulatedFunctionPage';
// Импортируйте другие страницы по мере необходимости

const App: React.FC = () => {
  const { user, isAuthenticated, setUser } = useAuthStore();
  const { setAnalFunctions, setTabFunctions, setCompFunctions } = useFunctionStore();

  // Проверяем аутентификацию при загрузке приложения
  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      // Здесь можно сделать запрос к API для получения информации о пользователе
      // Пока что используем заглушку
      const savedUser = JSON.parse(localStorage.getItem('user') || 'null');
      if (savedUser) {
        setUser(savedUser);
      }
    }
  }, [setUser]);

  // Загружаем функции при авторизации пользователя
  useEffect(() => {
    if (isAuthenticated) {
      const loadData = async () => {
        try {
          const [analData, tabData, compData] = await Promise.all([
            getAnalFunctions(),
            getTabFunctions(),
            getCompFunctions()
          ]);

          setAnalFunctions(analData);
          setTabFunctions(tabData);
          setCompFunctions(compData);
        } catch (error) {
          console.error('Error loading functions:', error);
        }
      };

      loadData();
    }
  }, [isAuthenticated, setAnalFunctions, setTabFunctions, setCompFunctions]);

  return (
    <Router>
      <Routes>
        {/* Публичные маршруты */}
        <Route path="/login" element={!isAuthenticated ? <LoginPage /> : <Navigate to="/" replace />} />
        <Route path="/register" element={!isAuthenticated ? <RegisterPage /> : <Navigate to="/" replace />} />

        {/* Защищенные маршруты */}
        <Route path="/" element={isAuthenticated ? <MainPage /> : <Navigate to="/login" replace />} />
        <Route path="/functions" element={isAuthenticated ? <FunctionsPage /> : <Navigate to="/login" replace />} />
        <Route path="/functions/analytic/:id" element={isAuthenticated ? <AnalyticFunctionPage /> : <Navigate to="/login" replace />} />
        <Route path="/functions/tabulated/:id" element={isAuthenticated ? <TabulatedFunctionPage /> : <Navigate to="/login" replace />} />

        {/* Перенаправление на логин по умолчанию */}
        <Route path="*" element={<Navigate to={isAuthenticated ? "/" : "/login"} replace />} />
      </Routes>
    </Router>
  );
};

export default App;