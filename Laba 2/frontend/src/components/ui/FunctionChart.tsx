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
  showLegend?: boolean;
  showTooltip?: boolean;
  showGrid?: boolean;
  showAxes?: boolean;
}

// Функция для получения цвета по типу функции
const getFunctionColor = (functionType: 'anal' | 'tab' | 'comp'): string => {
  switch (functionType) {
    case 'anal':
      return '#3b82f6'; // Синий для аналитических
    case 'tab':
      return '#10b981'; // Зеленый для табулированных
    case 'comp':
      return '#8b5cf6'; // Фиолетовый для композитных
    default:
      return '#3b82f6';
  }
};

const FunctionChart: React.FC<FunctionChartProps> = ({
  functionType,
  functionId,
  functionName,
  functionTypeCode,
  points,
  className = '',
  width = '100%',
  height = 300,
  showLegend = true,
  showTooltip = true,
  showGrid = true,
  showAxes = true
}) => {
  const [chartData, setChartData] = useState<{ x: number; y: number }[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const lineColor = getFunctionColor(functionType);

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
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (error) {
    return <div className="text-red-500 text-center p-4 text-sm">{error}</div>;
  }

  if (chartData.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-gray-400 dark:text-gray-500">
        <svg className="w-12 h-12 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
        <p className="text-sm">Нет данных для отображения графика</p>
      </div>
    );
  }

  return (
    <div className={`bg-white dark:bg-gray-800 rounded-lg shadow-md p-4 ${className}`} style={{ width, height: height + 40 }}>
      <div style={{ width: '100%', height }}>
        <ResponsiveContainer width="100%" height="100%">
          <LineChart
            data={chartData}
            margin={{
              top: 10,
              right: 30,
              left: 20,
              bottom: 10,
            }}
          >
            {showGrid && (
              <CartesianGrid
                strokeDasharray="3 3"
                stroke="#e5e7eb"
                strokeOpacity={0.5}
                horizontal={true}
                vertical={true}
              />
            )}
            {showAxes && (
              <>
                <XAxis
                  dataKey="x"
                  stroke="#6b7280"
                  fontSize={12}
                  tick={{ fill: '#6b7280' }}
                  axisLine={{ stroke: '#6b7280' }}
                  tickLine={{ stroke: '#6b7280' }}
                />
                <YAxis
                  stroke="#6b7280"
                  fontSize={12}
                  tick={{ fill: '#6b7280' }}
                  axisLine={{ stroke: '#6b7280' }}
                  tickLine={{ stroke: '#6b7280' }}
                />
              </>
            )}
            {showTooltip && (
              <Tooltip
                contentStyle={{
                  backgroundColor: '#374151',
                  borderColor: '#4b5563',
                  color: '#f9fafb',
                  fontSize: '12px',
                  padding: '8px',
                  borderRadius: '6px'
                }}
                formatter={(value: number) => [value.toFixed(4), 'Y']}
                labelFormatter={(label) => `X: ${Number(label).toFixed(4)}`}
              />
            )}
            {showLegend && <Legend />}
            <Line
              type="monotone"
              dataKey="y"
              name={functionName}
              stroke={lineColor}
              strokeWidth={2}
              dot={false}
              activeDot={{
                r: 6,
                stroke: lineColor,
                strokeWidth: 2,
                fill: '#ffffff'
              }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default FunctionChart;