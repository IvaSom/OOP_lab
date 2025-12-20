import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  getTabPointsByFunction,
  createTabPoint,
  deleteTabPoint
} from '../../api/functionApi';
import { useFunctionStore } from '../../store/functionStore';
import { Button, Input } from '../../components/ui/FormElements';
import FunctionChart from '../../components/ui/FunctionChart';
import Layout from '../../components/layout/Layout';
import { TabPointsDTO } from '../../types';
import { ArrowLeftIcon, TrashIcon, PlusIcon } from '@heroicons/react/24/outline';
import { toast } from 'react-toastify';

interface RouteParams {
  id: string;
}

const TabulatedFunctionPage: React.FC = () => {
  const { id } = useParams<RouteParams>();
  const navigate = useNavigate();
  const [functionId] = useState(Number(id));
  const [isLoading, setIsLoading] = useState(true);
  const [newPoint, setNewPoint] = useState({
    x: '',
    y: '',
    derive: ''
  });
  const [addingPoint, setAddingPoint] = useState(false);

  const { tabFunctions, tabPoints, setTabPoints, addTabPoint, removeTabPoint } = useFunctionStore();

  const currentFunction = tabFunctions.find(f => f.id === functionId);

  useEffect(() => {
    if (!currentFunction) {
      navigate('/functions');
      return;
    }

    const loadData = async () => {
      try {
        setIsLoading(true);
        const points = await getTabPointsByFunction(functionId);
        setTabPoints(functionId, points);
      } catch (error) {
        toast.error('Ошибка при загрузке точек функции');
        console.error('Error loading points:', error);
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [functionId, currentFunction, navigate, setTabPoints]);

  const handleAddPoint = async () => {
    if (!newPoint.x.trim() || !newPoint.y.trim() || !newPoint.derive.trim()) return;

    try {
      setAddingPoint(true);
      const pointData = {
        x: parseFloat(newPoint.x),
        y: parseFloat(newPoint.y),
        derive: parseFloat(newPoint.derive),
        functionId: functionId
      };

      if (isNaN(pointData.x) || isNaN(pointData.y) || isNaN(pointData.derive)) {
        toast.error('Введите корректные числовые значения');
        return;
      }

      const newPoint = await createTabPoint(pointData);
      addTabPoint(functionId, newPoint);
      setNewPoint({ x: '', y: '', derive: '' });
      toast.success('Точка добавлена успешно');
    } catch (error) {
      toast.error('Ошибка при добавлении точки');
      console.error('Error adding point:', error);
    } finally {
      setAddingPoint(false);
    }
  };

  const handleDeletePoint = async (pointId: number) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту точку?')) return;

    try {
      await deleteTabPoint(pointId);
      removeTabPoint(functionId, pointId);
      toast.success('Точка удалена успешно');
    } catch (error) {
      toast.error('Ошибка при удалении точки');
      console.error('Error deleting point:', error);
    }
  };

  const functionPoints = tabPoints[functionId] || [];
  const chartPoints = functionPoints.map(p => ({ x: p.x, y: p.y }));

  if (!currentFunction) {
    return (
      <Layout requireAuth={true}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-12">
            <p className="text-gray-500 dark:text-gray-400">Функция не найдена</p>
            <Button
              onClick={() => navigate('/functions')}
              className="mt-4"
            >
              Вернуться к списку функций
            </Button>
          </div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout requireAuth={true}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6">
          <button
            onClick={() => navigate('/functions')}
            className="inline-flex items-center text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
          >
            <ArrowLeftIcon className="h-4 w-4 mr-2" />
            Назад к списку функций
          </button>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white mt-2">
            {currentFunction.name}
          </h1>
          <p className="text-gray-500 dark:text-gray-400">
            Табулированная функция
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* График функции */}
          <div>
            <FunctionChart
              functionType="tab"
              functionId={functionId}
              functionName={currentFunction.name}
              points={chartPoints}
              className="mb-6"
            />
          </div>

          {/* Таблица точек */}
          <div>
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
              <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
                <div className="flex justify-between items-end mb-4">
                  <h2 className="text-lg font-medium text-gray-900 dark:text-white">
                    Точки функции
                  </h2>
                  <Button
                    onClick={handleAddPoint}
                    loading={addingPoint}
                    disabled={!newPoint.x.trim() || !newPoint.y.trim() || !newPoint.derive.trim()}
                  >
                    <PlusIcon className="h-5 w-5 mr-1" />
                    Добавить точку
                  </Button>
                </div>

                <div className="grid grid-cols-3 gap-3 mb-4">
                  <div>
                    <label htmlFor="newPointX" className="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1">
                      X
                    </label>
                    <Input
                      id="newPointX"
                      type="number"
                      value={newPoint.x}
                      onChange={(e) => setNewPoint(prev => ({ ...prev, x: e.target.value }))}
                      placeholder="Введите X"
                    />
                  </div>
                  <div>
                    <label htmlFor="newPointY" className="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1">
                      Y
                    </label>
                    <Input
                      id="newPointY"
                      type="number"
                      value={newPoint.y}
                      onChange={(e) => setNewPoint(prev => ({ ...prev, y: e.target.value }))}
                      placeholder="Введите Y"
                    />
                  </div>
                  <div>
                    <label htmlFor="newPointDerive" className="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1">
                      Производная
                    </label>
                    <Input
                      id="newPointDerive"
                      type="number"
                      value={newPoint.derive}
                      onChange={(e) => setNewPoint(prev => ({ ...prev, derive: e.target.value }))}
                      placeholder="Введите производную"
                    />
                  </div>
                </div>
              </div>

              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                  <thead className="bg-gray-50 dark:bg-gray-700">
                    <tr>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                        X
                      </th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                        Y
                      </th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                        Производная
                      </th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                        Действия
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                    {isLoading ? (
                      <tr>
                        <td colSpan={4} className="px-6 py-4 text-center">
                          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto"></div>
                        </td>
                      </tr>
                    ) : functionPoints.length === 0 ? (
                      <tr>
                        <td colSpan={4} className="px-6 py-4 text-center text-gray-500 dark:text-gray-400">
                          Нет точек для отображения
                        </td>
                      </tr>
                    ) : (
                      functionPoints.map((point) => (
                        <tr key={point.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                            {point.x.toFixed(4)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-300">
                            {point.y.toFixed(4)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-300">
                            {point.derive.toFixed(4)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-300">
                            <button
                              onClick={() => handleDeletePoint(point.id)}
                              className="text-red-600 hover:text-red-900 dark:hover:text-red-400"
                              aria-label="Удалить точку"
                            >
                              <TrashIcon className="h-5 w-5" />
                            </button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default TabulatedFunctionPage;