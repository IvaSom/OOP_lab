import React from 'react';
import { useOperationsStore } from '../../store/operationsStore';
import { useFunctionStore } from '../../store/functionStore';
import FunctionSelector from './FunctionSelector';
import FunctionChart from '../ui/FunctionChart';
import { Button } from '../ui/FormElements';

interface FunctionDifferentiationProps {
  onResultCreated?: () => void;
}

const FunctionDifferentiation: React.FC<FunctionDifferentiationProps> = ({
  onResultCreated
}) => {
  const {
    tabFunctions,
    tabPoints
  } = useFunctionStore();

  const {
    selectedFunctionForDiff,
    differentiationResult,
    isLoading,
    error,
    selectFunctionForDiff,
    performDifferentiation,
    clearError,
    resetSelection
  } = useOperationsStore();

  // Получаем точки для выбранной функции
  const selectedPoints = selectedFunctionForDiff ?
    tabPoints[selectedFunctionForDiff] || [] : [];

  const handleDifferentiate = async () => {
    await performDifferentiation();
    if (onResultCreated) {
      onResultCreated();
    }
  };

  const handleReset = () => {
    resetSelection();
  };

  return (
    <div className="space-y-6">
      {/* Заголовок */}
      <div>
        <h2 className="text-xl font-bold text-gray-900 dark:text-white">
          Дифференцирование функций
        </h2>
        <p className="mt-1 text-gray-500 dark:text-gray-400">
          Вычисление производной табулированной функции
        </p>
      </div>

      {/* Сообщение об ошибке */}
      {error && (
        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-md p-4">
          <div className="flex justify-between items-start">
            <div className="flex items-center">
              <svg className="h-5 w-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
              <span className="text-red-700 dark:text-red-300 font-medium">Ошибка</span>
            </div>
            <button
              onClick={clearError}
              className="text-red-400 hover:text-red-600 dark:hover:text-red-300"
            >
              <svg className="h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
              </svg>
            </button>
          </div>
          <p className="mt-2 text-sm text-red-600 dark:text-red-400">{error}</p>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Левая панель: выбор функции и управление */}
        <div className="space-y-4">
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
              Выбор функции
            </h3>

            <div className="space-y-4">
              <FunctionSelector
                label="Функция для дифференцирования"
                selectedFunctionId={selectedFunctionForDiff}
                functions={tabFunctions}
                onSelect={selectFunctionForDiff}
              />

              <div className="pt-2">
                <div className="flex items-center text-sm text-gray-500 dark:text-gray-400 mb-2">
                  <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                  </svg>
                  <span>Вычисляется левая производная</span>
                </div>

                <div className="flex space-x-3">
                  <Button
                    onClick={handleDifferentiate}
                    disabled={!selectedFunctionForDiff || isLoading}
                    loading={isLoading}
                    className="flex-1"
                  >
                    Вычислить производную
                  </Button>

                  <Button
                    variant="secondary"
                    onClick={handleReset}
                    disabled={isLoading}
                  >
                    Сбросить
                  </Button>
                </div>
              </div>
            </div>
          </div>

          {/* Информация о выбранной функции */}
          {selectedFunctionForDiff && selectedPoints.length > 0 && (
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
              <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                Исходная функция
              </h3>

              <div className="space-y-4">
                <FunctionChart
                  functionType="tab"
                  functionId={selectedFunctionForDiff}
                  functionName={tabFunctions.find(f => f.id === selectedFunctionForDiff)?.name || ''}
                  points={selectedPoints.map(p => ({ x: p.x, y: p.y }))}
                  height={200}
                />

                <div>
                  <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Характеристики
                  </h4>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    <div className="bg-gray-50 dark:bg-gray-700 rounded p-2">
                      <div className="text-gray-500 dark:text-gray-400">Точек</div>
                      <div className="font-medium">{selectedPoints.length}</div>
                    </div>
                    <div className="bg-gray-50 dark:bg-gray-700 rounded p-2">
                      <div className="text-gray-500 dark:text-gray-400">X min</div>
                      <div className="font-medium">
                        {Math.min(...selectedPoints.map(p => p.x)).toFixed(2)}
                      </div>
                    </div>
                    <div className="bg-gray-50 dark:bg-gray-700 rounded p-2">
                      <div className="text-gray-500 dark:text-gray-400">X max</div>
                      <div className="font-medium">
                        {Math.max(...selectedPoints.map(p => p.x)).toFixed(2)}
                      </div>
                    </div>
                    <div className="bg-gray-50 dark:bg-gray-700 rounded p-2">
                      <div className="text-gray-500 dark:text-gray-400">Область определения</div>
                      <div className="font-medium">
                        {Math.min(...selectedPoints.map(p => p.x)).toFixed(2)} ... {Math.max(...selectedPoints.map(p => p.x)).toFixed(2)}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Правая панель: результат */}
        <div>
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
              Результат дифференцирования
            </h3>

            {differentiationResult ? (
              <div className="space-y-4">
                <div className="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-md p-4">
                  <div className="flex items-center">
                    <svg className="h-5 w-5 text-green-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                    <span className="text-green-700 dark:text-green-300 font-medium">
                      Производная вычислена успешно
                    </span>
                  </div>
                  <p className="mt-2 text-sm text-green-600 dark:text-green-400">
                    Создана новая функция: "{differentiationResult.function.name}"
                  </p>
                </div>

                {/* График производной */}
                <div>
                  <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    График производной
                  </h4>
                  <FunctionChart
                    functionType="tab"
                    functionId={differentiationResult.function.id}
                    functionName={differentiationResult.function.name}
                    points={differentiationResult.points.map(p => ({ x: p.x, y: p.y }))}
                    height={250}
                  />
                </div>

                {/* Таблица значений производной */}
                <div>
                  <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Значения производной ({differentiationResult.points.length})
                  </h4>
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                      <thead>
                        <tr className="bg-gray-50 dark:bg-gray-700">
                          <th className="px-3 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                            X
                          </th>
                          <th className="px-3 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                            f'(x)
                          </th>
                        </tr>
                      </thead>
                      <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                        {differentiationResult.points.map((point, index) => (
                          <tr key={index} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                            <td className="px-3 py-2 whitespace-nowrap text-sm text-gray-900 dark:text-gray-300">
                              {point.x.toFixed(4)}
                            </td>
                            <td className="px-3 py-2 whitespace-nowrap text-sm text-gray-900 dark:text-gray-300">
                              {point.y.toFixed(4)}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-center py-12">
                <div className="inline-block p-4 rounded-full bg-gray-100 dark:bg-gray-700 mb-4">
                  <svg className="w-12 h-12 text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                  </svg>
                </div>
                <p className="text-gray-500 dark:text-gray-400">
                  {selectedFunctionForDiff
                    ? 'Нажмите "Вычислить производную" для получения результата'
                    : 'Выберите функцию для дифференцирования'}
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default FunctionDifferentiation;