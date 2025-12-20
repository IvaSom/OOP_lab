import React, { ReactNode, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import Sidebar from './Sidebar';
import Header from './Header';
import { ToastContainer } from 'react-toastify';

interface LayoutProps {
  children: ReactNode;
  requireAuth?: boolean;
  allowedRoles?: string[];
}

const Layout: React.FC<LayoutProps> = ({
  children,
  requireAuth = false,
  allowedRoles = ['USER', 'ADMIN']
}) => {
  const { user, isAuthenticated } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (requireAuth && !isAuthenticated) {
      navigate('/login');
    }

    if (isAuthenticated && user && !allowedRoles.includes(user.role)) {
      navigate('/forbidden');
    }
  }, [requireAuth, isAuthenticated, user, allowedRoles, navigate]);

  if (requireAuth && !isAuthenticated) {
    return null; // или показать лоадер
  }

  if (isAuthenticated && user && !allowedRoles.includes(user.role)) {
    return null; // или показать сообщение об ошибке
  }

  return (
    <div className="flex min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-300">
      <Sidebar />
      <div className="flex flex-col flex-1">
        <Header />
        <main className="flex-1 p-4 md:p-6 overflow-auto">
          {children}
        </main>
        <footer className="bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 py-4 px-6 text-center text-sm text-gray-500 dark:text-gray-400">
          <p>© {new Date().getFullYear()} Анализ функций. Все права защищены.</p>
        </footer>
      </div>
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
    </div>
  );
};

export default Layout;