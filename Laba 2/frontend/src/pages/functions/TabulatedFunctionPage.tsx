import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  getTabPointsByFunction,
  createTabPoint,
  updateTabPoint,
  deleteTabPoint
} from '../../api/functionApi';
import { useFunctionStore } from '../../store/functionStore';
import { Button, Input } from '../../components/ui/FormElements';
import FunctionChart from '../../components/ui/FunctionChart';
import Layout from '../../components/layout/Layout';
import { TabPointsDTO, TabPointsCreateDTO } from '../../types';
import {
  ArrowLeftIcon,
  TrashIcon,
  PlusIcon,
  PencilIcon,
  CheckIcon,
  XMarkIcon,
  CloudArrowUpIcon,
  TableCellsIcon
} from '@heroicons/react/24/outline';
import { toast } from 'react-toastify';
import * as XLSX from 'xlsx';

interface RouteParams {
  id: string;
}

interface EditablePoint extends TabPointsDTO {
  isEditing?: boolean;
  originalX?: number;
}

const TabulatedFunctionPage: React.FC = () => {
  const { id } = useParams<RouteParams>();
  const navigate = useNavigate();
  const [functionId] = useState(Number(id));
  const [isLoading, setIsLoading] = useState(true);
  const [points, setPoints] = useState<EditablePoint[]>([]);
  const [newPoint, setNewPoint] = useState({
    x: '',
    y: '',
    derive: ''
  });
  const [addingPoint, setAddingPoint] = useState(false);
  const [showBulkImport, setShowBulkImport] = useState(false);
  const [bulkData, setBulkData] = useState<string>('');
  const [importing, setImporting] = useState(false);

  const { tabFunctions, setTabPoints, addTabPoint, removeTabPoint } = useFunctionStore();
  const currentFunction = tabFunctions.find(f => f.id === functionId);

  useEffect(() => {
    if (!currentFunction) {
      navigate('/functions');
      return;
    }

    loadPoints();
  }, [functionId, currentFunction]);

  const loadPoints = async () => {
    try {
      setIsLoading(true);
      const pointsData = await getTabPointsByFunction(functionId);
      setPoints(pointsData.map(p => ({ ...p, isEditing: false })));
      setTabPoints(functionId, pointsData);
    } catch (error) {
      toast.error('Ошибка при загрузке точек функции');
      console.error('Error loading points:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const validatePoint = (x: number, y: number, derive: number): string | null => {
    if (isNaN(x) || isNaN(y) || isNaN(derive)) {
      return 'Все поля должны быть числами';
    }

    if (points.some(p => !p.isEditing && p.x === x)) {
      return 'Точка с таким X уже существует';
    }

    return null;
  };

  const handleAddPoint = async () => {
    if (!newPoint.x.trim() || !newPoint.y.trim() || !newPoint.derive.trim()) {
      toast.error('Заполните все поля');
      return;
    }

    const x = parseFloat(newPoint.x);
    const y = parseFloat(newPoint.y);
    const derive = parseFloat(newPoint.derive);

    const error = validatePoint(x, y, derive);
    if (error) {
      toast.error(error);
      return;
    }

    try {
      setAddingPoint(true);
      const pointData: TabPointsCreateDTO = { x, y, derive, functionId };
      const newPointData = await createTabPoint(pointData);

      setPoints(prev => [...prev, { ...newPointData, isEditing: false }]);
      addTabPoint(functionId, newPointData);
      setNewPoint({ x: '', y: '', derive: '' });
      toast.success('Точка добавлена успешно');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Ошибка при добавлении точки');
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

    const error = validatePoint(point.x, point.y, point.derive);
    if (error) {
      toast.error(error);
      return;
    }

    try {
      const pointData: TabPointsCreateDTO = {
        x: point.x,
        y: point.y,
        derive: point.derive,
        functionId
      };

      await updateTabPoint(point.id, pointData);
      setPoints(prev => prev.map((p, i) =>
        i === index ? { ...p, isEditing: false } : p
      ));
      toast.success('Точка обновлена успешно');
      loadPoints(); // Перезагружаем для синхронизации
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
      await deleteTabPoint(pointId);
      setPoints(prev => prev.filter(p => p.id !== pointId));
      removeTabPoint(functionId, pointId);
      toast.success('Точка удалена успешно');
    } catch (error) {
      toast.error('Ошибка при удалении точки');
      console.error('Error deleting point:', error);
    }
  };

  const handleBulkImport = async () => {
    if (!bulkData.trim()) {
      toast.error('Введите данные для импорта');
      return;
    }

    const lines = bulkData.trim().split('\n');
    const pointsToAdd: TabPointsCreateDTO[] = [];
    const errors: string[] = [];

    lines.forEach((line, index) => {
      const parts = line.trim().split(/\s+/);
      if (parts.length < 2 || parts.length > 3) {
        errors.push(`Строка ${index + 1}: неверный формат (нужно 2 или 3 числа)`);
        return;
      }

      const x = parseFloat(parts[0]);
      const y = parseFloat(parts[1]);
      const derive = parts[2] ? parseFloat(parts[2]) : 0;

      if (isNaN(x) || isNaN(y) || isNaN(derive)) {
        errors.push(`Строка ${index + 1}: неверный числовой формат`);
        return;
      }

      const error = validatePoint(x, y, derive);
      if (error) {
        errors.push(`Строка ${index + 1}: ${error}`);
        return;
      }

      pointsToAdd.push({ x, y, derive, functionId });
    });

    if (errors.length > 0) {
      toast.error(
        <div>
          <p>Ошибки при импорте:</p>
          <ul className="text-sm mt-1">
            {errors.slice(0, 5).map((err, i) => <li key={i}>{err}</li>)}
            {errors.length > 5 && <li>... и еще {errors.length - 5} ошибок</li>}
          </ul>
        </div>,
        { autoClose: 10000 }
      );
      return;
    }

    try {
      setImporting(true);
      const createdPoints: TabPointsDTO[] = [];

      for (const pointData of pointsToAdd) {
        const created = await createTabPoint(pointData);
        createdPoints.push(created);
      }

      setPoints(prev => [...prev, ...createdPoints.map(p => ({ ...p, isEditing: false }))]);
      createdPoints.forEach(p => addTabPoint(functionId, p));
      setBulkData('');
      setShowBulkImport(false);
      toast.success(`Успешно добавлено ${createdPoints.length} точек`);
    } catch (error) {
      toast.error('Ошибка при импорте точек');
      console.error('Error importing points:', error);
    } finally {
      setImporting(false);
    }
  };

  const handleFileImport = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    try {
      setImporting(true);
      const data = await file.arrayBuffer();
      const workbook = XLSX.read(data);
      const worksheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 }) as any[][];

      const pointsToAdd: TabPointsCreateDTO[] = [];
      const errors: string[] = [];

      jsonData.slice(1).forEach((row, index) => {
        if (row.length < 2 || row[0] === undefined || row[1] === undefined) {
          return;
        }

        const x = parseFloat(row[0]);
        const y = parseFloat(row[1]);
        const derive = row[2] ? parseFloat(row[2]) : 0;

        if (isNaN(x) || isNaN(y) || isNaN(derive)) {
          errors.push(`Строка ${index + 2}: неверный числовой формат`);
          return;
        }

        const error = validatePoint(x, y, derive);
        if (error) {
          errors.push(`Строка ${index + 2}: ${error}`);
          return;
        }

        pointsToAdd.push({ x, y, derive, functionId });
      });

      if (errors.length > 0) {
        toast.error(
          <div>
            <p>Ошибки при импорте из Excel:</p>
            <ul className="text-sm mt-1">
              {errors.slice(0, 5).map((err, i) => <li key={i}>{err}</li>)}
              {errors.length > 5 && <li>... и еще {errors.length - 5} ошибок</li>}
            </ul>
          </div>,
          { autoClose: 10000 }
        );
        return;
      }

      const createdPoints: TabPointsDTO[] = [];
      for (const pointData of pointsToAdd) {
        const created = await createTabPoint(pointData);
        createdPoints.push(created);
      }

      setPoints(prev => [...prev, ...createdPoints.map(p => ({ ...p, isEditing: false }))]);
      createdPoints.forEach(p => addTabPoint(functionId, p));
      toast.success(`Успешно добавлено ${createdPoints.length} точек из файла`);
    } catch (error) {
      toast.error('Ошибка при импорте из файла');
      console.error('Error importing from file:', error);
    } finally {
      setImporting(false);
      event.target.value = '';
    }
  };

  const handleExportPoints = () => {
    if (points.length === 0) {
      toast.error('Нет точек для экспорта');
      return;
    }

    const data = points.map(p => [p.x, p.y, p.derive]);
    const ws = XLSX.utils.aoa_to_sheet([
      ['X', 'Y', 'Производная'],
      ...data
    ]);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Точки');
    XLSX.writeFile(wb, `${currentFunction?.name || 'function'}_points.xlsx`);
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
                Табулированная функция • {points.length} точек
              </p>
            </div>
            <div className="flex space-x-2">
              <Button
                variant="outline"
                onClick={() => setShowBulkImport(!showBulkImport)}
              >
                <TableCellsIcon className="h-4 w-4 mr-2" />
                Массовое добавление
              </Button>
              <Button
                variant="outline"
                onClick={handleExportPoints}
                disabled={points.length === 0}
              >
                <CloudArrowUpIcon className="h-4 w-4 mr-2" />
                Экспорт в Excel
              </Button>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Left Column: Chart */}
          <div>
            <FunctionChart
              functionType="tab"
              functionId={functionId}
              functionName={currentFunction.name}
              points={chartPoints}
              className="mb-6"
              height={350}
            />

            {/* Add Point Form */}
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
              <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                Добавить новую точку
              </h3>
              <div className="grid grid-cols-3 gap-4 mb-4">
                <div>
                  <label htmlFor="newPointX" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    X <span className="text-red-500">*</span>
                  </label>
                  <Input
                    id="newPointX"
                    type="number"
                    step="any"
                    value={newPoint.x}
                    onChange={(e) => setNewPoint(prev => ({ ...prev, x: e.target.value }))}
                    placeholder="0.0"
                    className="w-full"
                  />
                </div>
                <div>
                  <label htmlFor="newPointY" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Y <span className="text-red-500">*</span>
                  </label>
                  <Input
                    id="newPointY"
                    type="number"
                    step="any"
                    value={newPoint.y}
                    onChange={(e) => setNewPoint(prev => ({ ...prev, y: e.target.value }))}
                    placeholder="0.0"
                    className="w-full"
                  />
                </div>
                <div>
                  <label htmlFor="newPointDerive" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    Производная
                  </label>
                  <Input
                    id="newPointDerive"
                    type="number"
                    step="any"
                    value={newPoint.derive}
                    onChange={(e) => setNewPoint(prev => ({ ...prev, derive: e.target.value }))}
                    placeholder="0.0"
                    className="w-full"
                  />
                </div>
              </div>
              <div className="flex justify-between items-center">
                <div className="text-sm text-gray-500 dark:text-gray-400">
                  Файл импорта:
                  <label className="ml-2 text-primary-600 dark:text-primary-400 hover:text-primary-700 dark:hover:text-primary-300 cursor-pointer">
                    <input
                      type="file"
                      accept=".xlsx,.xls,.csv"
                      onChange={handleFileImport}
                      className="hidden"
                    />
                    Выбрать файл (.xlsx, .csv)
                  </label>
                </div>
                <Button
                  onClick={handleAddPoint}
                  loading={addingPoint}
                  disabled={!newPoint.x.trim() || !newPoint.y.trim()}
                  className="w-32"
                >
                  <PlusIcon className="h-5 w-5 mr-1" />
                  Добавить
                </Button>
              </div>
            </div>
          </div>

          {/* Right Column: Points Table */}
          <div>
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
              <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
                <div className="flex justify-between items-center">
                  <h2 className="text-lg font-medium text-gray-900 dark:text-white">
                    Точки функции ({points.length})
                  </h2>
                  <div className="flex items-center space-x-2">
                    <span className="text-sm text-gray-500 dark:text-gray-400">
                      Сортировка по X
                    </span>
                  </div>
                </div>
              </div>

              <div className="overflow-x-auto max-h-[500px]">
                <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                  <thead className="bg-gray-50 dark:bg-gray-700 sticky top-0">
                    <tr>
                      <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                        X
                      </th>
                      <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                        Y
                      </th>
                      <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                        Производная
                      </th>
                      <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                        Действия
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                    {isLoading ? (
                      <tr>
                        <td colSpan={4} className="px-4 py-8 text-center">
                          <div className="flex flex-col items-center">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mb-2"></div>
                            <p className="text-gray-500 dark:text-gray-400">Загрузка точек...</p>
                          </div>
                        </td>
                      </tr>
                    ) : points.length === 0 ? (
                      <tr>
                        <td colSpan={4} className="px-4 py-8 text-center">
                          <div className="flex flex-col items-center">
                            <TableCellsIcon className="h-12 w-12 text-gray-300 dark:text-gray-600 mb-2" />
                            <p className="text-gray-500 dark:text-gray-400">Нет точек</p>
                            <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">
                              Добавьте первую точку с помощью формы слева
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
                            {point.isEditing ? (
                              <Input
                                type="number"
                                step="any"
                                value={point.y}
                                onChange={(e) => setPoints(prev => prev.map((p, i) =>
                                  i === index ? { ...p, y: parseFloat(e.target.value) || 0 } : p
                                ))}
                                className="w-24"
                              />
                            ) : (
                              <span className="text-sm text-gray-500 dark:text-gray-300">
                                {point.y.toFixed(4)}
                              </span>
                            )}
                          </td>
                          <td className="px-4 py-3 whitespace-nowrap">
                            {point.isEditing ? (
                              <Input
                                type="number"
                                step="any"
                                value={point.derive}
                                onChange={(e) => setPoints(prev => prev.map((p, i) =>
                                  i === index ? { ...p, derive: parseFloat(e.target.value) || 0 } : p
                                ))}
                                className="w-24"
                              />
                            ) : (
                              <span className="text-sm text-gray-500 dark:text-gray-300">
                                {point.derive.toFixed(4)}
                              </span>
                            )}
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

            {/* Stats */}
            {points.length > 0 && (
              <div className="mt-4 grid grid-cols-3 gap-4">
                <div className="bg-gray-50 dark:bg-gray-800 rounded-lg p-4">
                  <p className="text-sm text-gray-500 dark:text-gray-400">Минимальный X</p>
                  <p className="text-lg font-medium text-gray-900 dark:text-white">
                    {Math.min(...points.map(p => p.x)).toFixed(4)}
                  </p>
                </div>
                <div className="bg-gray-50 dark:bg-gray-800 rounded-lg p-4">
                  <p className="text-sm text-gray-500 dark:text-gray-400">Максимальный X</p>
                  <p className="text-lg font-medium text-gray-900 dark:text-white">
                    {Math.max(...points.map(p => p.x)).toFixed(4)}
                  </p>
                </div>
                <div className="bg-gray-50 dark:bg-gray-800 rounded-lg p-4">
                  <p className="text-sm text-gray-500 dark:text-gray-400">Общее количество</p>
                  <p className="text-lg font-medium text-gray-900 dark:text-white">{points.length}</p>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Bulk Import Modal */}
        {showBulkImport && (
          <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center z-50 p-4">
            <div className="bg-white dark:bg-gray-800 rounded-lg max-w-2xl w-full p-6">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-medium text-gray-900 dark:text-white">
                  Массовое добавление точек
                </h3>
                <button
                  onClick={() => setShowBulkImport(false)}
                  className="text-gray-400 hover:text-gray-500 dark:hover:text-gray-300"
                >
                  <XMarkIcon className="h-6 w-6" />
                </button>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Введите точки (каждая точка на новой строке):
                  </label>
                  <p className="text-sm text-gray-500 dark:text-gray-400 mb-2">
                    Формат: X Y [Производная] (производная необязательна, по умолчанию 0)
                  </p>
                  <textarea
                    value={bulkData}
                    onChange={(e) => setBulkData(e.target.value)}
                    className="w-full h-64 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:text-white font-mono text-sm"
                    placeholder="0.0 1.0 0.0
1.0 2.0 1.0
2.0 4.0 2.0
..."
                  />
                  <div className="mt-2 text-sm text-gray-500 dark:text-gray-400">
                    Пример:
                    <code className="ml-2 text-xs bg-gray-100 dark:bg-gray-700 px-2 py-1 rounded">
                      0 1 0{"\n"}1 2 1{"\n"}2 4 2
                    </code>
                  </div>
                </div>

                <div className="flex justify-end space-x-3">
                  <Button
                    variant="secondary"
                    onClick={() => setShowBulkImport(false)}
                    disabled={importing}
                  >
                    Отмена
                  </Button>
                  <Button
                    onClick={handleBulkImport}
                    loading={importing}
                    disabled={!bulkData.trim()}
                  >
                    Импортировать
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

export default TabulatedFunctionPage;