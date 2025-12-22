import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  getCompPointsByFunction,
  createCompPoint,
  updateCompPoint,
  deleteCompPoint,
  getAnalFunctions,
  getCompStructuresByFunction,
  createCompStructure,
  deleteCompStructure
} from '../../api/functionApi';
import { useFunctionStore } from '../../store/functionStore';
import { Button, Input, Select } from '../../components/ui/FormElements';
import FunctionChart from '../../components/ui/FunctionChart';
import Layout from '../../components/layout/Layout';
import { CompPointsDTO, CompFunDTO, AnalFunDTO, CompositeStructureDTO } from '../../types';
import {
  ArrowLeftIcon,
  TrashIcon,
  PlusIcon,
  PencilIcon,
  CheckIcon,
  XMarkIcon,
  LinkIcon,
  DocumentPlusIcon,
  TableCellsIcon
} from '@heroicons/react/24/outline';
import { toast } from 'react-toastify';

interface RouteParams {
  id: string;
}

interface EditablePoint extends CompPointsDTO {
  isEditing?: boolean;
  originalX?: number;
}

const CompositeFunctionPage: React.FC = () => {
  const { id } = useParams<RouteParams>();
  const navigate = useNavigate();
  const [functionId] = useState(Number(id));
  const [isLoading, setIsLoading] = useState(true);
  const [points, setPoints] = useState<EditablePoint[]>([]);
  const [newPointX, setNewPointX] = useState('');
  const [addingPoint, setAddingPoint] = useState(false);
  const [showAddStructure, setShowAddStructure] = useState(false);
  const [availableFunctions, setAvailableFunctions] = useState<AnalFunDTO[]>([]);
  const [selectedFunctionId, setSelectedFunctionId] = useState<number | ''>('');
  const [executionOrder, setExecutionOrder] = useState<number | ''>('');
  const [structures, setStructures] = useState<CompositeStructureDTO[]>([]);

  const { compFunctions, analFunctions, setCompPoints, addCompPoint, removeCompPoint } = useFunctionStore();
  const currentFunction = compFunctions.find(f => f.id === functionId);

  useEffect(() => {
    if (!currentFunction) {
      navigate('/functions');
      return;
    }

    loadData();
  }, [functionId, currentFunction]);

  useEffect(() => {
    loadAnalyticFunctions();
  }, []);

  const calculateNextOrder = () => {
    if (structures.length === 0) return 1;
    const maxOrder = Math.max(...structures.map(s => s.executionOrder));
    return maxOrder + 1;
  };

  const loadData = async () => {
    try {
      setIsLoading(true);

      // Загружаем точки и структуры параллельно
      const [pointsData, structuresData] = await Promise.all([
        getCompPointsByFunction(functionId),
        getCompStructuresByFunction(functionId)

      ]);

      setPoints(pointsData.map(p => ({ ...p, isEditing: false })));
      setStructures(structuresData);
      setCompPoints(functionId, pointsData);
    } catch (error) {
      //toast.error('Ошибка при загрузке данных функции');
      console.error('Error loading data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const loadAnalyticFunctions = async () => {
    try {
      const functions = await getAnalFunctions();
      setAvailableFunctions(functions);
    } catch (error) {
      console.error('Error loading analytic functions:', error);
    }
  };

  const validatePoint = (x: number): string | null => {
    if (isNaN(x)) {
      return 'X должен быть числом';
    }

    if (points.some(p => !p.isEditing && p.x === x)) {
      return 'Точка с таким X уже существует';
    }

    if (structures.length === 0) {
      return 'Добавьте аналитические функции в композитную структуру';
    }

    return null;
  };

  const handleAddPoint = async () => {
    if (!newPointX.trim()) {
      toast.error('Введите значение X');
      return;
    }

    const x = parseFloat(newPointX);
    const error = validatePoint(x);
    if (error) {
      toast.error(error);
      return;
    }

    try {
      setAddingPoint(true);

      // Для композитных функций мы передаем только X и functionId
      // Y будет вычислен на сервере автоматически
      const pointData = {
        x: x,
        functionId: functionId
      };

      const newPointData = await createCompPoint(pointData);

      setPoints(prev => [...prev, { ...newPointData, isEditing: false }]);
      addCompPoint(functionId, newPointData);
      setNewPointX('');
      toast.success('Точка добавлена успешно');
    } catch (error: any) {
      //toast.error(error.response?.data?.message || 'Ошибка при добавлении точки');
      console.error('Error adding point:', error);
    } finally {
      setAddingPoint(false);
    }
  };

  const handleEditPoint = (index: number) => {
    setPoints(prev => prev.map((p, i) =>
      i === index ? { ...p, isEditing: true, originalX: p.x } : { ...p, isEditing: false }
    ));
  };

  const handleSavePoint = async (index: number) => {
    const point = points[index];
    if (!point) return;

    const error = validatePoint(point.x);
    if (error) {
      toast.error(error);
      return;
    }

    try {
      const pointData = {
        x: point.x,
        functionId: functionId
      };

      await updateCompPoint(point.id, pointData);
      setPoints(prev => prev.map((p, i) =>
        i === index ? { ...p, isEditing: false } : p
      ));
      toast.success('Точка обновлена успешно');
      loadData(); // Перезагружаем для обновления Y
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Ошибка при обновлении точки');
      console.error('Error updating point:', error);
    }
  };

  const handleCancelEdit = (index: number) => {
    const point = points[index];
    if (point?.originalX !== undefined) {
      setPoints(prev => prev.map((p, i) =>
        i === index ? { ...p, isEditing: false, x: p.originalX! } : p
      ));
    }
  };

  const handleDeletePoint = async (pointId: number) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту точку?')) return;

    try {
      await deleteCompPoint(pointId);
      setPoints(prev => prev.filter(p => p.id !== pointId));
      removeCompPoint(functionId, pointId);
      toast.success('Точка удалена успешно');
    } catch (error) {
      //toast.error('Ошибка при удалении точки');
      console.error('Error deleting point:', error);
    }
  };

  const handleAddStructure = async () => {
    if (!selectedFunctionId || !executionOrder) {
      toast.error('Выберите функцию и порядок выполнения');
      return;
    }

    try {
      const structureData = {
        compositeFunctionId: functionId,
        analyticFunctionId: Number(selectedFunctionId),
        executionOrder: Number(executionOrder)
      };

      const newStructure = await createCompStructure(structureData);
      setStructures(prev => [...prev, newStructure]);
      setSelectedFunctionId('');
      setExecutionOrder('');
      setShowAddStructure(false);
      toast.success('Функция добавлена в композитную структуру');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Ошибка при добавлении функции');
      console.error('Error adding structure:', error);
    }
  };

  const handleDeleteStructure = async (structureId: number) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту функцию из композиции?')) return;

    try {
      await deleteCompStructure(structureId);
      setStructures(prev => prev.filter(s => s.id !== structureId));
      toast.success('Функция удалена из композитной структуры');
    } catch (error) {
      //('Ошибка при удалении функции из композиции');
      console.error('Error deleting structure:', error);
    }
  };

  const getFunctionTypeName = (type: number): string => {
    const types: { [key: number]: string } = {
      1: 'sin(x)',
      2: 'cos(x)',
      3: 'x²',
      4: 'const',
      5: '0',
      6: 'ln(x)',
      7: 'x',
      8: '1'
    };
    return types[type] || `Тип ${type}`;
  };

  const chartPoints = points.map(p => ({ x: p.x, y: p.y }));

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
        {/* Header */}
        <div className="mb-6">
          <button
            onClick={() => navigate('/functions')}
            className="inline-flex items-center text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white mb-4"
          >
            <ArrowLeftIcon className="h-4 w-4 mr-2" />
            Назад к списку функций
          </button>
          <div className="flex justify-between items-start">
            <div>
              <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
                {currentFunction.name}
              </h1>
              <p className="text-gray-500 dark:text-gray-400">
                Композитная функция • {points.length} точек • {structures.length} компонентов
              </p>
            </div>
            <Button
              variant="outline"
              onClick={() => setShowAddStructure(true)}
            >
              <LinkIcon className="h-4 w-4 mr-2" />
              Добавить функцию в композицию
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Left Column: Chart and Points Table */}
          <div className="space-y-6">
            {/* Chart */}
            <FunctionChart
              functionType="comp"
              functionId={functionId}
              functionName={currentFunction.name}
              points={chartPoints}
              className="mb-6"
              height={350}
            />

            {/* Add Point Form */}
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
              <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                Добавить точку композитной функции
              </h3>
              <div className="flex space-x-4 items-end">
                <div className="flex-grow">
                  <label htmlFor="newPointX" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    X <span className="text-red-500">*</span>
                    <span className="text-xs text-gray-500 dark:text-gray-400 ml-2">
                      Y будет вычислен автоматически
                    </span>
                  </label>
                  <Input
                    id="newPointX"
                    type="number"
                    step="any"
                    value={newPointX}
                    onChange={(e) => setNewPointX(e.target.value)}
                    placeholder="0.0"
                    className="w-full"
                  />
                </div>
                <Button
                  onClick={handleAddPoint}
                  loading={addingPoint}
                  disabled={!newPointX.trim()}
                  className="h-10"
                >
                  <PlusIcon className="h-5 w-5 mr-1" />
                  Добавить
                </Button>
              </div>
            </div>
          </div>

          {/* Right Column: Structure and Points */}
          <div className="space-y-6">
            {/* Composite Structure */}
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
              <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
                <h2 className="text-lg font-medium text-gray-900 dark:text-white">
                  Композитная структура ({structures.length})
                </h2>
                <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                  Порядок выполнения: от 1 к {structures.length}
                </p>
              </div>

              <div className="overflow-y-auto max-h-80">
                {structures.length === 0 ? (
                  <div className="p-6 text-center text-gray-500 dark:text-gray-400">
                    <DocumentPlusIcon className="h-12 w-12 mx-auto mb-2" />
                    <p>Нет функций в композиции</p>
                    <p className="text-sm mt-1">Добавьте аналитические функции для создания композиции</p>
                  </div>
                ) : (
                  <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                    <thead className="bg-gray-50 dark:bg-gray-700">
                      <tr>
                        <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                          Порядок
                        </th>
                        <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                          Функция
                        </th>
                        <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                          Тип
                        </th>
                        <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                          Действия
                        </th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
                      {structures.sort((a, b) => a.executionOrder - b.executionOrder).map((structure) => {
                        const analFunc = analFunctions.find(f => f.id === structure.analyticFunctionId);
                        return (
                          <tr key={structure.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                            <td className="px-4 py-3">
                              <span className="inline-flex items-center justify-center w-6 h-6 rounded-full bg-primary-100 text-primary-600 dark:bg-primary-900/30 dark:text-primary-400 text-sm font-medium">
                                {structure.executionOrder}
                              </span>
                            </td>
                            <td className="px-4 py-3">
                              <span className="font-medium text-gray-900 dark:text-white">
                                {analFunc?.name || `Функция ${structure.analyticFunctionId}`}
                              </span>
                            </td>
                            <td className="px-4 py-3">
                              <span className="text-sm text-gray-500 dark:text-gray-400">
                                {analFunc ? getFunctionTypeName(analFunc.type) : 'Неизвестно'}
                              </span>
                            </td>
                            <td className="px-4 py-3">
                              <button
                                onClick={() => handleDeleteStructure(structure.id)}
                                className="p-1 text-red-600 hover:text-red-900 dark:hover:text-red-400"
                                aria-label="Удалить из композиции"
                              >
                                <TrashIcon className="h-5 w-5" />
                              </button>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                )}
              </div>
            </div>

            {/* Points Table */}
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
              <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
                <div className="flex justify-between items-center">
                  <h2 className="text-lg font-medium text-gray-900 dark:text-white">
                    Точки функции ({points.length})
                  </h2>
                  <span className="text-sm text-gray-500 dark:text-gray-400">
                    Y вычислен автоматически
                  </span>
                </div>
              </div>

              <div className="overflow-x-auto max-h-96">
                <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                  <thead className="bg-gray-50 dark:bg-gray-700 sticky top-0">
                    <tr>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                        X
                      </th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                        Y (вычислено)
                      </th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                        Действия
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
                    {isLoading ? (
                      <tr>
                        <td colSpan={3} className="px-4 py-8 text-center">
                          <div className="flex flex-col items-center">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mb-2"></div>
                            <p className="text-gray-500 dark:text-gray-400">Загрузка точек...</p>
                          </div>
                        </td>
                      </tr>
                    ) : points.length === 0 ? (
                      <tr>
                        <td colSpan={3} className="px-4 py-8 text-center">
                          <div className="flex flex-col items-center">
                            <TableCellsIcon className="h-12 w-12 text-gray-300 dark:text-gray-600 mb-2" />
                            <p className="text-gray-500 dark:text-gray-400">Нет точек</p>
                            <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">
                              Добавьте точки для вычисления значений композитной функции
                            </p>
                          </div>
                        </td>
                      </tr>
                    ) : (
                      points.sort((a, b) => a.x - b.x).map((point, index) => (
                        <tr key={point.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                          <td className="px-4 py-3 whitespace-nowrap">
                            {point.isEditing ? (
                              <Input
                                type="number"
                                step="any"
                                value={point.x}
                                onChange={(e) => setPoints(prev => prev.map((p, i) =>
                                  i === index ? { ...p, x: parseFloat(e.target.value) || 0 } : p
                                ))}
                                className="w-24"
                              />
                            ) : (
                              <span className="text-sm font-medium text-gray-900 dark:text-white">
                                {point.x.toFixed(4)}
                              </span>
                            )}
                          </td>
                          <td className="px-4 py-3 whitespace-nowrap">
                            <span className="text-sm text-gray-500 dark:text-gray-300">
                              {point.y.toFixed(4)}
                            </span>
                          </td>
                          <td className="px-4 py-3 whitespace-nowrap">
                            <div className="flex items-center space-x-2">
                              {point.isEditing ? (
                                <>
                                  <button
                                    onClick={() => handleSavePoint(index)}
                                    className="p-1 text-green-600 hover:text-green-900 dark:hover:text-green-400"
                                    aria-label="Сохранить"
                                  >
                                    <CheckIcon className="h-5 w-5" />
                                  </button>
                                  <button
                                    onClick={() => handleCancelEdit(index)}
                                    className="p-1 text-gray-600 hover:text-gray-900 dark:hover:text-gray-400"
                                    aria-label="Отмена"
                                  >
                                    <XMarkIcon className="h-5 w-5" />
                                  </button>
                                </>
                              ) : (
                                <>
                                  <button
                                    onClick={() => handleEditPoint(index)}
                                    className="p-1 text-blue-600 hover:text-blue-900 dark:hover:text-blue-400"
                                    aria-label="Редактировать"
                                  >
                                    <PencilIcon className="h-5 w-5" />
                                  </button>
                                  <button
                                    onClick={() => handleDeletePoint(point.id)}
                                    className="p-1 text-red-600 hover:text-red-900 dark:hover:text-red-400"
                                    aria-label="Удалить"
                                  >
                                    <TrashIcon className="h-5 w-5" />
                                  </button>
                                </>
                              )}
                            </div>
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

        {/* Add Structure Modal */}
        {showAddStructure && (
          <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center z-50 p-4">
            <div className="bg-white dark:bg-gray-800 rounded-lg max-w-md w-full p-6">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-medium text-gray-900 dark:text-white">
                  Добавить функцию в композицию
                </h3>
                <button
                  onClick={() => setShowAddStructure(false)}
                  className="text-gray-400 hover:text-gray-500 dark:hover:text-gray-300"
                >
                  <XMarkIcon className="h-6 w-6" />
                </button>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Аналитическая функция
                  </label>
                  <Select
                    options={[
                      { value: '', label: 'Выберите функцию', disabled: true },
                      ...availableFunctions.map(func => ({
                        value: func.id,
                        label: `${func.name} (${getFunctionTypeName(func.type)})`
                      }))
                    ]}
                    value={selectedFunctionId}
                    onChange={(e) => setSelectedFunctionId(Number(e.target.value) || '')}
                    className="w-full"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Порядок выполнения
                  </label>
                  <Input
                    type="number"
                    min="1"
                    value={executionOrder}
                    onChange={(e) => setExecutionOrder(Number(e.target.value) || '')}
                    placeholder="1, 2, 3, ..."
                    className="w-full"
                  />
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                    Определяет последовательность применения функций
                  </p>
                </div>

                <div className="flex justify-end space-x-3 pt-4">
                  <Button
                    variant="secondary"
                    onClick={() => setShowAddStructure(false)}
                  >
                    Отмена
                  </Button>
                  <Button
                    onClick={handleAddStructure}
                    disabled={!selectedFunctionId || !executionOrder}
                  >
                    Добавить
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

export default CompositeFunctionPage;