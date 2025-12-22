import React, { useState } from 'react';
import { useOperationsStore } from '../../store/operationsStore';
import { useFunctionStore } from '../../store/functionStore';
import FunctionSelector from './FunctionSelector';
import FunctionChart from '../ui/FunctionChart';
import { Button } from '../ui/FormElements';
import { TabFunDTO } from '../../types';

interface FunctionOperationProps {
  onResultCreated?: () => void;
}

const FunctionOperation: React.FC<FunctionOperationProps> = ({
  onResultCreated
}) => {
  const [showPreview, setShowPreview] = useState(false);

  const {
    tabFunctions,
    tabPoints,
    setTabFunctions,
    setTabPoints
  } = useFunctionStore();

  const {
    selectedFunction1,
    selectedFunction2,
    operationType,
    operationResult,
    isLoading,
    error,
    selectFunction1,
    selectFunction2,
    setOperationType,
    performOperation,
    clearError,
    resetSelection
  } = useOperationsStore();

  // Получаем точки для выбранных функций
  const points1 = selectedFunction1 ? tabPoints[selectedFunction1] || [] : [];
  const points2 = selectedFunction2 ? tabPoints[selectedFunction2] || [] : [];

  const handlePerformOperation = async () => {
    await performOperation();
    if (onResultCreated) {
      onResultCreated();
    }
  };

  const handleReset = () => {
    resetSelection();
    setShowPreview(false);
  };

  const getOperationSymbol = () => {
    switch (operationType) {
      case 'sum': return '+';
      case 'subtract': return '−';
      case 'multiply': return '×';
      case 'divide': return '÷';
      default: return '?';
    }
  };

  const getOperationName = () => {
    switch (operationType) {
      case 'sum': return 'Сложение';
      case 'subtract': return 'Вычитание';
      case 'multiply': return 'Умножение';
      case 'divide': return 'Деление';
      default: return 'Операция';
    }
  };

  return (
    <div className="space-y-6">
      {/* Заголовок */}
      <div>
        <h2 className="text-xl font-bold text-gray-900 dark:text-white">
          Операции над функциями
        </h2>
        <p className="mt-1 text-gray-500 dark:text-gray-400">
          Выполнение арифметических операций над табулированными функциями
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

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Левая панель: выбор функций */}
        <div className="space-y-4">
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
              Выбор функций
            </h3>

            <div className="space-y-4">
              <FunctionSelector
                label="Первая функция"
                selectedFunctionId={selectedFunction1}
                functions={tabFunctions}
                onSelect={selectFunction1}
              />

              <FunctionSelector
                label="Вторая функция"
                selectedFunctionId={selectedFunction2}
                functions={tabFunctions}
                onSelect={selectFunction2}
              />

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Операция
                </label>
                <div className="grid grid-cols-4 gap-2">
                  {(['sum', 'subtract', 'multiply', 'divide'] as const).map((op) => (
                    <button
                      key={op}
                      type="button"
                      onClick={() => setOperationType(op)}
                      className={`
                        py-2 px-3 text-sm font-medium rounded-md transition-colors
                        ${operationType === op
                          ? 'bg-primary-600 text-white'
                          : 'bg-gray-100 text-gray-700 hover:bg-gray-200 dark:bg-gray-700 dark:text-gray-300 dark:hover:bg-gray-600'}
                      `}
                    >
                      {getOperationSymbol()}
                    </button>
                  ))}
                </div>
                <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
                  {getOperationName()}
                </p>
              </div>

              <div className="flex space-x-3 pt-2">
                <Button
                  onClick={handlePerformOperation}
                  disabled={!selectedFunction1 || !selectedFunction2 || isLoading}
                  loading={isLoading}
                  className="flex-1"
                >
                  Выполнить операцию
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

          {/* Предпросмотр функций */}
          {showPreview && (selectedFunction1 || selectedFunction2) && (
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
              <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                Предпросмотр функций
              </h3>

              <div className="space-y-4">
                {selectedFunction1 && points1.length > 0 && (
                  <div>
                    <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      Функция 1
                    </h4>
                    <FunctionChart
                      functionType="tab"
                      functionId={selectedFunction1}
                      functionName={tabFunctions.find(f => f.id === selectedFunction1)?.name || ''}
                      points={points1}
                      height={150}
                      showLegend={false}
                    />
                  </div>
                )}

                {selectedFunction2 && points2.length > 0 && (
                  <div>
                    <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      Функция 2
                    </h4>
                    <FunctionChart
                      functionType="tab"
                      functionId={selectedFunction2}
                      functionName={tabFunctions.find(f => f.id === selectedFunction2)?.name || ''}
                      points={points2}
                      height={150}
                      showLegend={false}
                    />
                  </div>
                )}
              </div>
            </div>
          )}
        </div>

        {/* Правая панель: результат */}
        <div>
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-medium text-gray-900 dark:text-white">
                Результат
              </h3>

              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowPreview(!showPreview)}
              >
                {showPreview ? 'Скрыть предпросмотр' : 'Показать предпросмотр'}
              </Button>
            </div>

            {operationResult ? (
              <div className="space-y-4">
                <div className="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-md p-4">
                  <div className="flex items-center">
                    <svg className="h-5 w-5 text-green-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                    <span className="text-green-700 dark:text-green-300 font-medium">
                      Операция выполнена успешно
                    </span>
                  </div>
                  <p className="mt-2 text-sm text-green-600 dark:text-green-400">
                    Создана новая функция: "{operationResult.function.name}"
                  </p>
                </div>

                {/* График результата */}
                <div>
                  <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    График результата
                  </h4>
                  <FunctionChart
                    functionType="tab"
                    functionId={operationResult.function.id}
                    functionName={operationResult.function.name}
                    points={operationResult.points.map(p => ({ x: p.x, y: p.y }))}
                    height={250}
                  />
                </div>

                {/* Таблица точек результата */}
                <div>
                  <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Точки результата ({operationResult.points.length})
                  </h4>
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                      <thead>
                        <tr className="bg-gray-50 dark:bg-gray-700">
                          <th className="px-3 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                            X
                          </th>
                          <th className="px-3 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                            Y
                          </th>
                        </tr>
                      </thead>
                      <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                        {operationResult.points.map((point, index) => (
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
                  {selectedFunction1 && selectedFunction2
                    ? 'Нажмите "Выполнить операцию" для получения результата'
                    : 'Выберите две функции для выполнения операции'}
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default FunctionOperation;