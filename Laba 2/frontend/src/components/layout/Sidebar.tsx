import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  ChartBarIcon,
  DocumentTextIcon,
  CalculatorIcon,
  UserGroupIcon,
  CogIcon,
  MoonIcon,
  SunIcon
} from '@heroicons/react/24/outline';
import { useThemeStore } from '../../store/themeStore';
import { useAuthStore } from '../../store/authStore';
import logo from '../../assets/logo.svg';

const navigation = [
  { name: 'Дашборд', href: '/', icon: ChartBarIcon },
  { name: 'Функции', href: '/functions', icon: DocumentTextIcon },
  { name: 'Операции', href: '/operations', icon: CalculatorIcon },
  { name: 'Пользователи', href: '/users', icon: UserGroupIcon, roles: ['ADMIN'] },
  { name: 'Настройки', href: '/settings', icon: CogIcon },
];

const Sidebar: React.FC = () => {
  const location = useLocation();
  const { darkMode, toggleDarkMode } = useThemeStore();
  const { user } = useAuthStore();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleNavigationClick = () => {
    if (window.innerWidth < 768) {
      setSidebarOpen(false);
    }
  };

  return (
    <>
      {/* Мобильное меню */}
      <div className={`fixed inset-y-0 left-0 z-50 w-64 bg-white dark:bg-gray-800 shadow-lg transform ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'} transition-transform duration-300 ease-in-out md:hidden`}>
        <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <img className="h-8 w-auto" src={logo} alt="Логотип" />
          <button onClick={() => setSidebarOpen(false)} className="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200">
            <span className="sr-only">Закрыть меню</span>
            <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <nav className="mt-5 px-2 space-y-1">
          {navigation.map((item) => {
            if (item.roles && user && !item.roles.includes(user.role)) return null;

            const isActive = location.pathname === item.href;
            return (
              <Link
                key={item.name}
                to={item.href}
                onClick={handleNavigationClick}
                className={`${
                  isActive
                    ? 'bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400'
                    : 'text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700'
                } group flex items-center px-2 py-2 text-base font-medium rounded-md`}
              >
                <item.icon
                  className={`${
                    isActive ? 'text-primary-600 dark:text-primary-400' : 'text-gray-400 group-hover:text-gray-500 dark:group-hover:text-gray-300'
                  } mr-4 h-6 w-6`}
                  aria-hidden="true"
                />
                {item.name}
              </Link>
            );
          })}
        </nav>
        <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-200 dark:border-gray-700">
          <button
            onClick={toggleDarkMode}
            className="flex items-center w-full text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 px-2 py-2 rounded-md"
          >
            {darkMode ? (
              <>
                <SunIcon className="mr-4 h-6 w-6 text-yellow-400" />
                Светлая тема
              </>
            ) : (
              <>
                <MoonIcon className="mr-4 h-6 w-6 text-gray-400" />
                Темная тема
              </>
            )}
          </button>
        </div>
      </div>

      {/* Стационарное меню */}
      <div className="hidden md:flex md:flex-col md:w-64 md:fixed md:inset-y-0 md:border-r md:border-gray-200 dark:md:border-gray-700 md:bg-white dark:md:bg-gray-800">
        <div className="flex flex-col flex-grow pt-5 pb-4 overflow-y-auto">
          <div className="flex items-center justify-between px-4">
            <img className="h-8 w-auto" src={logo} alt="Логотип" />
          </div>
          <nav className="mt-5 flex-1 px-2 space-y-1">
            {navigation.map((item) => {
              if (item.roles && user && !item.roles.includes(user.role)) return null;

              const isActive = location.pathname === item.href;
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`${
                    isActive
                      ? 'bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400'
                      : 'text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700'
                  } group flex items-center px-2 py-2 text-sm font-medium rounded-md`}
                >
                  <item.icon
                    className={`${
                      isActive ? 'text-primary-600 dark:text-primary-400' : 'text-gray-400 group-hover:text-gray-500 dark:group-hover:text-gray-300'
                    } mr-3 h-6 w-6`}
                    aria-hidden="true"
                  />
                  {item.name}
                </Link>
              );
            })}
          </nav>
          <div className="mt-auto p-4 border-t border-gray-200 dark:border-gray-700">
            <button
              onClick={toggleDarkMode}
              className="flex items-center w-full text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 px-2 py-2 rounded-md text-sm"
            >
              {darkMode ? (
                <>
                  <SunIcon className="mr-3 h-5 w-5 text-yellow-400" />
                  Светлая тема
                </>
              ) : (
                <>
                  <MoonIcon className="mr-3 h-5 w-5 text-gray-400" />
                  Темная тема
                </>
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Кнопка для мобильного меню */}
      <button
        onClick={() => setSidebarOpen(true)}
        className="md:hidden fixed top-4 left-4 z-40 p-2 rounded-md text-gray-700 dark:text-gray-200 bg-white dark:bg-gray-800 shadow-lg"
      >
        <span className="sr-only">Открыть меню</span>
        <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
    </>
  );
};

export default Sidebar;