import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../../components/layout/Layout';
import { Button } from '../../components/ui/FormElements';
import { useFunctionStore } from '../../store/functionStore';
import FunctionOperation from '../../components/operations/FunctionOperation';
import FunctionDifferentiation from '../../components/operations/FunctionDifferentiation';
import { getTabFunctions, getTabPointsByFunction } from '../../api/functionApi';
import { toast } from 'react-toastify';

const FunctionOperationsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'operations' | 'differentiation'>('operations');
  const navigate = useNavigate();

  const {
    tabFunctions,
    tabPoints,
    setTabFunctions,
    setTabPoints
  } = useFunctionStore();

  useEffect(() => {
    loadFunctions();
  }, []);

  const loadFunctions = async () => {
    try {
      const functions = await getTabFunctions();
      setTabFunctions(functions);

      // Загружаем точки для всех функций (опционально)
      for (const func of functions) {
        try {
          const points = await getTabPointsByFunction(func.id);
          setTabPoints(func.id, points);
        } catch (error) {
          console.error(`Error loading points for function ${func.id}:`, error);
        }
      }
    } catch (error) {
      toast.error('Ошибка при загрузке функций');
      console.error('Error loading functions:', error);
    }
  };

  const handleResultCreated = () => {
    // Перезагружаем список функций, чтобы показать новую созданную
    loadFunctions();
    toast.success('Новая функция создана успешно!');
  };

  return (
    <Layout requireAuth={true}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Заголовок и навигация */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-8 gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
              Математические операции
            </h1>
            <p className="mt-2 text-gray-500 dark:text-gray-400">
              Выполнение операций над табулированными функциями
            </p>
          </div>

          <div className="flex space-x-2">
            <Button
              variant="secondary"
              onClick={() => navigate('/functions')}
            >
              Назад к списку
            </Button>
            <Button
              onClick={loadFunctions}
            >
              Обновить
            </Button>
          </div>
        </div>

        {/* Статистика */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <div className="flex items-center">
              <div className="p-2 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 mr-3">
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M12 7a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0V8.414l-4.293 4.293a1 1 0 01-1.414 0L8 10.414l-4.293 4.293a1 1 0 01-1.414-1.414l5-5a1 1 0 011.414 0L11 10.586 14.586 7H12z" clipRule="evenodd" />
                </svg>
              </div>
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">Доступно функций</p>
                <p className="text-2xl font-bold">{tabFunctions.length}</p>
              </div>
            </div>
          </div>

          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <div className="flex items-center">
              <div className="p-2 rounded-full bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400 mr-3">
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v3.586L7.707 9.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 10.586V7z" clipRule="evenodd" />
                </svg>
              </div>
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">Функции с точками</p>
                <p className="text-2xl font-bold">
                  {tabFunctions.filter(f => tabPoints[f.id]?.length > 0).length}
                </p>
              </div>
            </div>
          </div>

          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <div className="flex items-center">
              <div className="p-2 rounded-full bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400 mr-3">
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M12.316 3.051a1 1 0 01.633 1.265l-4 12a1 1 0 11-1.898-.632l4-12a1 1 0 011.265-.633zM5.707 6.293a1 1 0 010 1.414L3.414 10l2.293 2.293a1 1 0 11-1.414 1.414l-3-3a1 1 0 010-1.414l3-3a1 1 0 011.414 0zm8.586 0a1 1 0 011.414 0l3 3a1 1 0 010 1.414l-3 3a1 1 0 11-1.414-1.414L16.586 10l-2.293-2.293a1 1 0 010-1.414z" clipRule="evenodd" />
                </svg>
              </div>
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">Операции</p>
                <p className="text-2xl font-bold">4</p>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">(+, -, ×, ÷)</p>
              </div>
            </div>
          </div>
        </div>

        {/* Табы для выбора типа операций */}
        <div className="border-b border-gray-200 dark:border-gray-700 mb-8">
          <nav className="-mb-px flex space-x-8" aria-label="Tabs">
            <button
              onClick={() => setActiveTab('operations')}
              className={`
                ${activeTab === 'operations'
                  ? 'border-primary-500 text-primary-600 dark:text-primary-400'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300'}
                whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm flex items-center
              `}
            >
              <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v3.586L7.707 9.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 10.586V7z" clipRule="evenodd" />
              </svg>
              Арифметические операции
            </button>

            <button
              onClick={() => setActiveTab('differentiation')}
              className={`
                ${activeTab === 'differentiation'
                  ? 'border-primary-500 text-primary-600 dark:text-primary-400'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300'}
                whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm flex items-center
              `}
            >
              <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
              Дифференцирование
            </button>
          </nav>
        </div>

        {/* Контент вкладок */}
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4 md:p-6">
          {activeTab === 'operations' ? (
            <FunctionOperation onResultCreated={handleResultCreated} />
          ) : (
            <FunctionDifferentiation onResultCreated={handleResultCreated} />
          )}
        </div>

        {/* Информация о требованиях */}
        <div className="mt-8 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
          <div className="flex">
            <div className="flex-shrink-0">
              <svg className="h-5 w-5 text-blue-400" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
              </svg>
            </div>
            <div className="ml-3">
              <h3 className="text-sm font-medium text-blue-800 dark:text-blue-300">
                Требования к функциям
              </h3>
              <div className="mt-2 text-sm text-blue-700 dark:text-blue-400 space-y-1">
                <p>• Для арифметических операций функции должны иметь одинаковое количество точек</p>
                <p>• Значения X должны совпадать в соответствующих точках</p>
                <p>• Функции должны содержать хотя бы 2 точки</p>
                <p>• Для деления значения Y второй функции не должны быть равны 0</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default FunctionOperationsPage;