import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getAnalFunctions,
  getTabFunctions,
  getCompFunctions,
  createAnalFunction,
  createTabFunction,
  createCompFunction
} from '../../api/functionApi';
import { useFunctionStore } from '../../store/functionStore';
import { Button } from '../../components/ui/FormElements';
import FunctionChart from '../../components/ui/FunctionChart';
import Layout from '../../components/layout/Layout';
import { AnalFunDTO, TabFunDTO, CompFunDTO } from '../../types';
import { PlusIcon, FunnelIcon } from '@heroicons/react/24/outline';
import { toast } from 'react-toastify';

const FunctionsPage: React.FC = () => {
  const [isLoading, setIsLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'anal' | 'tab' | 'comp'>('anal');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [functionName, setFunctionName] = useState('');
  const [functionType, setFunctionType] = useState<number>(1);
  const navigate = useNavigate();

  const {
    analFunctions,
    tabFunctions,
    compFunctions,
    setAnalFunctions,
    setTabFunctions,
    setCompFunctions,
    addAnalFunction,
    addTabFunction,
    addCompFunction
  } = useFunctionStore();

  useEffect(() => {
    const loadData = async () => {
      try {
        setIsLoading(true);

        // Загружаем все типы функций параллельно
        const [analData, tabData, compData] = await Promise.all([
          getAnalFunctions(),
          getTabFunctions(),
          getCompFunctions()
        ]);

        setAnalFunctions(analData);
        setTabFunctions(tabData);
        setCompFunctions(compData);
      } catch (error) {
        toast.error('Ошибка при загрузке функций');
        console.error('Error loading functions:', error);
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [setAnalFunctions, setTabFunctions, setCompFunctions]);

  const handleCreateFunction = async () => {
    try {
      if (!functionName.trim()) {
        toast.error('Введите название функции');
        return;
      }

      if (activeTab === 'anal') {
        if (!functionType) {
          toast.error('Выберите тип функции');
          return;
        }

        const newFunc: AnalFunDTO = {
          id: 0, // Will be set by server
          name: functionName.trim(),
          type: functionType
        };

        const created = await createAnalFunction(newFunc);
        addAnalFunction(created);
        toast.success('Аналитическая функция создана успешно');
      } else if (activeTab === 'tab') {
        const newFunc: TabFunDTO = {
          id: 0,
          name: functionName.trim()
        };

        const created = await createTabFunction(newFunc);
        addTabFunction(created);
        toast.success('Табулированная функция создана успешно');
      } else if (activeTab === 'comp') {
        const newFunc: CompFunDTO = {
          id: 0,
          name: functionName.trim()
        };

        const created = await createCompFunction(newFunc);
        addCompFunction(created);
        toast.success('Композитная функция создана успешно');
        // Перенаправляем на страницу редактирования структуры
        navigate(`/functions/composite/${created.id}`);
      }

      setShowCreateModal(false);
      setFunctionName('');
      setFunctionType(1);
    } catch (error) {
      toast.error('Ошибка при создании функции');
      console.error('Error creating function:', error);
    }
  };

  const getFunctionTypeName = (type: number): string => {
    const types = {
      1: 'Синус',
      2: 'Косинус',
      3: 'Квадрат',
      4: 'Константа',
      5: 'Ноль',
      6: 'Натуральный логарифм',
      7: 'Тождественная',
      8: 'Единица'
    };
    return types[type as keyof typeof types] || `Тип ${type}`;
  };

  const renderFunctionList = () => {
    if (isLoading) {
      return (
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      );
    }

    if (activeTab === 'anal') {
      if (analFunctions.length === 0) {
        return (
          <div className="text-center py-12">
            <p className="text-gray-500 dark:text-gray-400">Нет аналитических функций</p>
            <Button
              onClick={() => setShowCreateModal(true)}
              className="mt-4"
            >
              Создать первую функцию
            </Button>
          </div>
        );
      }

      return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {analFunctions.map(func => (
            <div
              key={func.id}
              className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
              onClick={() => navigate(`/functions/analytic/${func.id}`)}
            >
              <div className="p-4">
                <h3 className="text-lg font-medium text-gray-900 dark:text-white">{func.name}</h3>
                <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                  Тип: {getFunctionTypeName(func.type)}
                </p>
              </div>
              <div className="border-t border-gray-200 dark:border-gray-700 p-4">
                <FunctionChart
                  functionType="anal"
                  functionId={func.id}
                  functionName={func.name}
                  functionTypeCode={func.type}
                  height={150}
                />
              </div>
            </div>
          ))}
        </div>
      );
    }

    if (activeTab === 'tab') {
      if (tabFunctions.length === 0) {
        return (
          <div className="text-center py-12">
            <p className="text-gray-500 dark:text-gray-400">Нет табулированных функций</p>
            <Button
              onClick={() => setShowCreateModal(true)}
              className="mt-4"
            >
              Создать первую функцию
            </Button>
          </div>
        );
      }

      return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {tabFunctions.map(func => (
            <div
              key={func.id}
              className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
              onClick={() => navigate(`/functions/tabulated/${func.id}`)}
            >
              <div className="p-4">
                <h3 className="text-lg font-medium text-gray-900 dark:text-white">{func.name}</h3>
              </div>
              <div className="border-t border-gray-200 dark:border-gray-700 p-4">
                <FunctionChart
                  functionType="tab"
                  functionId={func.id}
                  functionName={func.name}
                  height={150}
                />
              </div>
            </div>
          ))}
        </div>
      );
    }

    if (activeTab === 'comp') {
      if (compFunctions.length === 0) {
        return (
          <div className="text-center py-12">
            <p className="text-gray-500 dark:text-gray-400">Нет композитных функций</p>
            <Button
              onClick={() => setShowCreateModal(true)}
              className="mt-4"
            >
              Создать первую функцию
            </Button>
          </div>
        );
      }

      return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {compFunctions.map(func => (
            <div
              key={func.id}
              className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
              onClick={() => navigate(`/functions/composite/${func.id}`)}
            >
              <div className="p-4">
                <h3 className="text-lg font-medium text-gray-900 dark:text-white">{func.name}</h3>
              </div>
              <div className="border-t border-gray-200 dark:border-gray-700 p-4">
                <FunctionChart
                  functionType="comp"
                  functionId={func.id}
                  functionName={func.name}
                  height={150}
                />
              </div>
            </div>
          ))}
        </div>
      );
    }

    return null;
  };

  return (
    <Layout requireAuth={true}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Функции</h1>
            <p className="mt-2 text-gray-500 dark:text-gray-400">
              Управление аналитическими, табулированными и композитными функциями
            </p>
          </div>
          <Button onClick={() => setShowCreateModal(true)}>
            <PlusIcon className="h-5 w-5 mr-2" />
            Создать функцию
          </Button>
        </div>

        {/* Tabs */}
        <div className="border-b border-gray-200 dark:border-gray-700 mb-8">
          <nav className="-mb-px flex space-x-8" aria-label="Tabs">
            {[
              { id: 'anal', name: 'Аналитические', count: analFunctions.length },
              { id: 'tab', name: 'Табулированные', count: tabFunctions.length },
              { id: 'comp', name: 'Композитные', count: compFunctions.length }
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as 'anal' | 'tab' | 'comp')}
                className={`
                  ${activeTab === tab.id
                    ? 'border-primary-500 text-primary-600 dark:text-primary-400'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300'}
                  whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm
                `}
                aria-current={activeTab === tab.id ? 'page' : undefined}
              >
                {tab.name}
                <span
                  className={`
                    ${activeTab === tab.id
                      ? 'bg-primary-100 text-primary-600 dark:bg-primary-900/30 dark:text-primary-400'
                      : 'bg-gray-100 text-gray-900 dark:bg-gray-700 dark:text-gray-300'}
                    hidden ml-2 py-0.5 px-2.5 rounded-full text-xs font-medium md:inline-block
                  `}
                >
                  {tab.count}
                </span>
              </button>
            ))}
          </nav>
        </div>

        {/* Function List */}
        {renderFunctionList()}

        {/* Create Function Modal */}
        {showCreateModal && (
          <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center z-50 p-4">
            <div className="bg-white dark:bg-gray-800 rounded-lg max-w-md w-full p-6">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-medium text-gray-900 dark:text-white">
                  Создание {activeTab === 'anal' ? 'аналитической' : activeTab === 'tab' ? 'табулированной' : 'композитной'} функции
                </h3>
                <button
                  onClick={() => setShowCreateModal(false)}
                  className="text-gray-400 hover:text-gray-500 dark:hover:text-gray-300"
                >
                  <span className="sr-only">Закрыть</span>
                  <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <div className="space-y-4">
                <div>
                  <label htmlFor="functionName" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Название функции
                  </label>
                  <input
                    id="functionName"
                    type="text"
                    value={functionName}
                    onChange={(e) => setFunctionName(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:text-white"
                    placeholder="Введите название"
                  />
                </div>

                {activeTab === 'anal' && (
                  <div>
                    <label htmlFor="functionType" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                      Тип функции
                    </label>
                    <select
                      id="functionType"
                      value={functionType}
                      onChange={(e) => setFunctionType(Number(e.target.value))}
                      className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:text-white"
                    >
                      <option value={1}>Синус (sin)</option>
                      <option value={2}>Косинус (cos)</option>
                      <option value={3}>Квадрат (x²)</option>
                      <option value={4}>Константа</option>
                      <option value={5}>Ноль</option>
                      <option value={6}>Натуральный логарифм (ln)</option>
                      <option value={7}>Тождественная (x)</option>
                      <option value={8}>Единица</option>
                    </select>
                  </div>
                )}

                <div className="flex justify-end space-x-3 pt-4">
                  <Button
                    variant="secondary"
                    onClick={() => setShowCreateModal(false)}
                  >
                    Отмена
                  </Button>
                  <Button
                    onClick={handleCreateFunction}
                    loading={isLoading}
                  >
                    Создать
                  </Button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </Layout>
  );
};

export default FunctionsPage;