import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getAnalFunctions,
  getTabFunctions,
  getCompFunctions,
  getTabPointsByFunction,
  createAnalFunction,
  createTabFunction,
  createCompFunction
} from '../../api/functionApi';
import { useFunctionStore } from '../../store/functionStore';
import { Button } from '../../components/ui/FormElements';
import FunctionChart from '../../components/ui/FunctionChart';
import TabulatedMiniChart from '../../components/ui/TabulatedMiniChart';
import CompFunMiniChart from '../../components/ui/CompFunMiniChart';
import Layout from '../../components/layout/Layout';
import { AnalFunDTO, TabFunDTO, CompFunDTO, TabPointsDTO } from '../../types';
import { PlusIcon } from '@heroicons/react/24/outline';
import { toast } from 'react-toastify';

const FunctionsPage: React.FC = () => {
  const [isLoading, setIsLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'anal' | 'tab' | 'comp'>('anal');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [functionName, setFunctionName] = useState('');
  const [functionType, setFunctionType] = useState<number>(1);
  const [tabFunctionsData, setTabFunctionsData] = useState<{
    [key: number]: TabPointsDTO[];
  }>({});
  const [tabFunctionsLoading, setTabFunctionsLoading] = useState<{
    [key: number]: boolean;
  }>({});
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

        // Для табулированных функций загружаем точки для мини-графиков
        await loadTabFunctionsPoints(tabData);
      } catch (error) {
        toast.error('Ошибка при загрузке функций');
        console.error('Error loading functions:', error);
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [setAnalFunctions, setTabFunctions, setCompFunctions]);

  const loadTabFunctionsPoints = async (tabFunctions: TabFunDTO[]) => {
    try {
      const pointsData: { [key: number]: TabPointsDTO[] } = {};
      const loadingState: { [key: number]: boolean } = {};

      // Устанавливаем загрузку для всех функций
      tabFunctions.forEach(func => {
        loadingState[func.id] = true;
      });

      setTabFunctionsLoading(loadingState);

      // Загружаем точки для каждой табулированной функции
      const promises = tabFunctions.map(async (func) => {
        try {
          const points = await getTabPointsByFunction(func.id);
          pointsData[func.id] = points;
          return { id: func.id, points };
        } catch (error) {
          console.error(`Error loading points for function ${func.id}:`, error);
          pointsData[func.id] = [];
          return { id: func.id, points: [] };
        }
      });

      await Promise.all(promises);
      setTabFunctionsData(pointsData);

      // Сбрасываем состояние загрузки
      const resetLoading = { ...loadingState };
      tabFunctions.forEach(func => {
        resetLoading[func.id] = false;
      });
      setTabFunctionsLoading(resetLoading);
    } catch (error) {
      console.error('Error loading tab functions points:', error);
    }
  };

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
        
        // Обновляем данные точек
        setTabFunctionsData(prev => ({ ...prev, [created.id]: [] }));
        setTabFunctionsLoading(prev => ({ ...prev, [created.id]: false }));
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

  const renderAnalFunctionCard = (func: AnalFunDTO) => (
    <div
      key={func.id}
      className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow cursor-pointer group"
      onClick={() => navigate(`/functions/analytic/${func.id}`)}
    >
      <div className="p-4">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="text-lg font-medium text-gray-900 dark:text-white group-hover:text-primary-600 dark:group-hover:text-primary-400">
              {func.name}
            </h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
              Тип: {getFunctionTypeName(func.type)}
            </p>
          </div>
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200">
            Аналитическая
          </span>
        </div>
      </div>
      <div className="border-t border-gray-200 dark:border-gray-700 p-4">
        <FunctionChart
          functionType="anal"
          functionId={func.id}
          functionName={func.name}
          functionTypeCode={func.type}
          height={150}
          showLegend={false}
          showTooltip={false}
        />
      </div>
    </div>
  );

  const renderTabFunctionCard = (func: TabFunDTO) => {
    const points = tabFunctionsData[func.id] || [];
    const isLoading = tabFunctionsLoading[func.id] || false;
    const hasPoints = points.length > 0;

    return (
      <div
        key={func.id}
        className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow cursor-pointer group"
        onClick={() => navigate(`/functions/tabulated/${func.id}`)}
      >
        <div className="p-4">
          <div className="flex justify-between items-start">
            <div>
              <h3 className="text-lg font-medium text-gray-900 dark:text-white group-hover:text-primary-600 dark:group-hover:text-primary-400">
                {func.name}
              </h3>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                {hasPoints 
                  ? `${points.length} точек` 
                  : 'Нет данных'
                }
              </p>
            </div>
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200">
              Табулированная
            </span>
          </div>
        </div>
        <div className="border-t border-gray-200 dark:border-gray-700 p-4">
          {isLoading ? (
            <div className="flex justify-center items-center h-[150px]">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
            </div>
          ) : hasPoints ? (
            <TabulatedMiniChart
              points={points}
              functionName={func.name}
              height={150}
            />
          ) : (
            <div className="flex flex-col items-center justify-center h-[150px] text-gray-400 dark:text-gray-500">
              <svg className="w-12 h-12 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
              <p className="text-sm">Добавьте точки для отображения графика</p>
            </div>
          )}
        </div>
      </div>
    );
  };

  const renderCompFunctionCard = (func: CompFunDTO) => (
  <div
    key={func.id}
    className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow cursor-pointer group"
    onClick={() => navigate(`/functions/composite/${func.id}`)}
  >
    <div className="p-4">
      <div className="flex justify-between items-start">
        <div>
          <h3 className="text-lg font-medium text-gray-900 dark:text-white group-hover:text-primary-600 dark:group-hover:text-primary-400">
            {func.name}
          </h3>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            Композитная функция
          </p>
        </div>
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200">
          Композитная
        </span>
      </div>
    </div>
    <div className="border-t border-gray-200 dark:border-gray-700 p-4">
      {/* ЗАМЕНИТЕ ЭТОТ БЛОК */}
      <CompFunMiniChart
        functionId={func.id}
        functionName={func.name}
        height={150}
      />
    </div>
  </div>
  );

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
            <div className="inline-block p-4 rounded-full bg-gray-100 dark:bg-gray-800 mb-4">
              <svg className="w-12 h-12 text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
            </div>
            <p className="text-gray-500 dark:text-gray-400">Нет аналитических функций</p>
            <p className="text-sm text-gray-400 dark:text-gray-500 mt-1 mb-4">
              Создайте аналитическую функцию для визуализации
            </p>
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
          {analFunctions.map(renderAnalFunctionCard)}
        </div>
      );
    }

    if (activeTab === 'tab') {
      if (tabFunctions.length === 0) {
        return (
          <div className="text-center py-12">
            <div className="inline-block p-4 rounded-full bg-gray-100 dark:bg-gray-800 mb-4">
              <svg className="w-12 h-12 text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <p className="text-gray-500 dark:text-gray-400">Нет табулированных функций</p>
            <p className="text-sm text-gray-400 dark:text-gray-500 mt-1 mb-4">
              Создайте табулированную функцию и добавьте точки данных
            </p>
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
          {tabFunctions.map(renderTabFunctionCard)}
        </div>
      );
    }

    if (activeTab === 'comp') {
      if (compFunctions.length === 0) {
        return (
          <div className="text-center py-12">
            <div className="inline-block p-4 rounded-full bg-gray-100 dark:bg-gray-800 mb-4">
              <svg className="w-12 h-12 text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
            <p className="text-gray-500 dark:text-gray-400">Нет композитных функций</p>
            <p className="text-sm text-gray-400 dark:text-gray-500 mt-1 mb-4">
              Создайте композитную функцию из других функций
            </p>
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
          {compFunctions.map(renderCompFunctionCard)}
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