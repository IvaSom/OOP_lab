import React, { useState, useEffect } from 'react';
import Layout from '../components/layout/Layout';
import { Button } from '../components/ui/FormElements';
import FunctionChart from '../components/ui/FunctionChart';
import { ArrowPathIcon, CalculatorIcon, ChartBarIcon, FunnelIcon } from '@heroicons/react/24/outline';
import { toast } from 'react-toastify';

const MainPage: React.FC = () => {
  const [activeChart, setActiveChart] = useState<'sin' | 'cos' | 'sqr'>('sin');
  const [randomValue, setRandomValue] = useState(Math.random() * 20 - 10);

  useEffect(() => {
    const interval = setInterval(() => {
      setRandomValue(Math.random() * 20 - 10);
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  const getChartData = () => {
    const points = [];
    const step = 0.1;

    for (let x = -10; x <= 10; x += step) {
      let y = 0;
      if (activeChart === 'sin') {
        y = Math.sin(x);
      } else if (activeChart === 'cos') {
        y = Math.cos(x);
      } else if (activeChart === 'sqr') {
        y = x * x;
      }

      points.push({ x, y });
    }

    return points;
  };

  const handleRefresh = () => {
    setRandomValue(Math.random() * 20 - 10);
    toast.info('Данные обновлены');
  };

  return (
    <Layout requireAuth={true}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center mb-12">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
            Анализ математических функций
          </h1>
          <p className="mt-4 text-xl text-gray-500 dark:text-gray-400">
            Мощный инструмент для работы с аналитическими, табулированными и композитными функциями
          </p>
        </div>

        {/* Статистика */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
          {[
            { title: 'Аналитических функций', value: '24', icon: ChartBarIcon, color: 'text-primary-500' },
            { title: 'Табулированных функций', value: '18', icon: CalculatorIcon, color: 'text-green-500' },
            { title: 'Композитных функций', value: '12', icon: FunnelIcon, color: 'text-purple-500' },
            { title: 'Пользователей', value: '42', icon: ArrowPathIcon, color: 'text-amber-500' }
          ].map((stat, index) => (
            <div key={index} className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
              <div className="flex items-center">
                <div className={`flex-shrink-0 ${stat.color}`}>
                  <stat.icon className="h-8 w-8" aria-hidden="true" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-500 dark:text-gray-400 truncate">{stat.title}</p>
                  <p className="text-2xl font-semibold text-gray-900 dark:text-white">{stat.value}</p>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Основной контент */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* График */}
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
                График функции
              </h2>
              <div className="flex space-x-2">
                {['sin', 'cos', 'sqr'].map((type) => (
                  <Button
                    key={type}
                    variant={activeChart === type ? 'primary' : 'secondary'}
                    onClick={() => setActiveChart(type as 'sin' | 'cos' | 'sqr')}
                    className="px-3 py-1 text-sm"
                  >
                    {type === 'sin' ? 'sin(x)' : type === 'cos' ? 'cos(x)' : 'x²'}
                  </Button>
                ))}
                <Button
                  variant="outline"
                  onClick={handleRefresh}
                  className="px-3 py-1 text-sm"
                >
                  <ArrowPathIcon className="h-4 w-4 mr-1" />
                  Обновить
                </Button>
              </div>
            </div>
            <div className="h-80">
              <FunctionChart
                functionType="anal"
                functionId={1}
                functionName={activeChart === 'sin' ? 'sin(x)' : activeChart === 'cos' ? 'cos(x)' : 'x²'}
                functionTypeCode={activeChart === 'sin' ? 1 : activeChart === 'cos' ? 2 : 3}
                points={getChartData()}
                height={300}
              />
            </div>
          </div>

          {/* Карточка с информацией */}
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
              Случайное значение
            </h2>
            <div className="text-center mb-8">
              <p className="text-5xl font-bold text-primary-600 dark:text-primary-400">
                {randomValue.toFixed(4)}
              </p>
              <p className="mt-2 text-gray-500 dark:text-gray-400">
                Сгенерировано в {new Date().toLocaleTimeString()}
              </p>
            </div>

            <div className="space-y-4">
              <div>
                <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                  Основные возможности
                </h3>
                <ul className="space-y-2">
                  {[
                    'Создание и анализ аналитических функций',
                    'Работа с табулированными функциями',
                    'Построение композитных функций',
                    'Дифференцирование и интегрирование',
                    'Математические операции над функциями'
                  ].map((feature, index) => (
                    <li key={index} className="flex items-start">
                      <ArrowPathIcon className="h-5 w-5 text-primary-500 mt-0.5 mr-2" />
                      <span className="text-gray-600 dark:text-gray-300">{feature}</span>
                    </li>
                  ))}
                </ul>
              </div>

              <Button
                onClick={() => toast.info('Переход к функциям...')}
                className="w-full"
              >
                Перейти к функциям
              </Button>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default MainPage;