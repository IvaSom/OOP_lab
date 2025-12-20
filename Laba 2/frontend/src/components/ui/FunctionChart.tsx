import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { calculateFunctionValues } from '../../utils/mathUtils';

interface FunctionChartProps {
  functionType: 'anal' | 'tab' | 'comp';
  functionId: number;
  functionName: string;
  functionTypeCode?: number; // Только для аналитических функций
  points?: { x: number; y: number }[];
  className?: string;
  width?: string;
  height?: number;
}

const FunctionChart: React.FC<FunctionChartProps> = ({
  functionType,
  functionId,
  functionName,
  functionTypeCode,
  points,
  className = '',
  width = '100%',
  height = 300
}) => {
  const [chartData, setChartData] = useState<{ x: number; y: number }[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadChartData = async () => {
      try {
        setLoading(true);
        setError(null);

        if (points && points.length > 0) {
          // Если точки уже переданы в props
          setChartData(points);
        } else if (functionType === 'anal' && functionTypeCode) {
          // Для аналитической функции вычисляем значения
          const values = calculateFunctionValues(functionTypeCode, -10, 10, 100);
          setChartData(values);
        } else if (functionType === 'tab' || functionType === 'comp') {
          // Для табулированных и композитных функций нужно получать данные с сервера
          // Здесь нужно добавить API вызов
          setChartData([]);
        }
      } catch (err) {
        setError('Ошибка при загрузке данных для графика');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    loadChartData();
  }, [functionType, functionId, functionTypeCode, points]);

  if (loading) {
    return <div className="flex justify-center items-center h-64">Загрузка графика...</div>;
  }

  if (error) {
    return <div className="text-red-500 text-center p-4">{error}</div>;
  }

  if (chartData.length === 0) {
    return <div className="text-gray-500 text-center p-4">Нет данных для отображения графика</div>;
  }

  return (
    <div className={`bg-white dark:bg-gray-800 rounded-lg shadow-md p-4 ${className}`} style={{ width, height: height + 40 }}>
      <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">{functionName}</h3>
      <div style={{ width: '100%', height: height }}>
        <ResponsiveContainer width="100%" height="100%">
          <LineChart
            data={chartData}
            margin={{
              top: 5,
              right: 30,
              left: 20,
              bottom: 5,
            }}
          >
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" darkMode={true} />
            <XAxis dataKey="x" stroke="#6b7280" />
            <YAxis stroke="#6b7280" />
            <Tooltip
              contentStyle={{
                backgroundColor: '#374151',
                borderColor: '#4b5563',
                color: '#f9fafb'
              }}
            />
            <Legend />
            <Line
              type="monotone"
              dataKey="y"
              name={functionName}
              stroke="#3b82f6"
              strokeWidth={2}
              dot={false}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default FunctionChart;