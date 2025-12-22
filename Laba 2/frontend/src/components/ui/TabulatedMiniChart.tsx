import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { TabPointsDTO } from '../types';

interface TabulatedMiniChartProps {
  points: TabPointsDTO[];
  functionName: string;
  height?: number;
  className?: string;
  showGrid?: boolean;
  showAxes?: boolean;
}

// Цвета для разных типов функций
const TABULATED_COLOR = '#10b981'; // Зеленый для табулированных
const TABULATED_COLOR_DARK = '#059669'; // Темно-зеленый для контраста

const TabulatedMiniChart: React.FC<TabulatedMiniChartProps> = ({
  points,
  functionName,
  height = 150,
  className = '',
  showGrid = true,
  showAxes = true
}) => {
  if (points.length === 0) {
    return (
      <div className={`flex items-center justify-center h-${height} text-gray-400 dark:text-gray-500 ${className}`}>
        <div className="text-center">
          <svg className="w-8 h-8 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p className="text-xs">Нет данных</p>
        </div>
      </div>
    );
  }

  // Сортируем точки по X для правильного отображения графика
  const sortedPoints = [...points].sort((a, b) => a.x - b.x);

  // Берем максимум 50 точек для мини-графика, чтобы не перегружать
  const displayPoints = sortedPoints.length > 50
    ? sortedPoints.filter((_, index) => index % Math.ceil(sortedPoints.length / 50) === 0)
    : sortedPoints;

  const chartData = displayPoints.map(point => ({
    x: point.x,
    y: point.y,
    derive: point.derive
  }));

  // Вычисляем диапазоны для оптимального масштабирования
  const xValues = chartData.map(d => d.x);
  const yValues = chartData.map(d => d.y);

  const minX = Math.min(...xValues);
  const maxX = Math.max(...xValues);
  const minY = Math.min(...yValues);
  const maxY = Math.max(...yValues);

  // Добавляем небольшие отступы для лучшего отображения
  const xPadding = (maxX - minX) * 0.1 || 0.1;
  const yPadding = (maxY - minY) * 0.1 || 0.1;

  const domainX = [minX - xPadding, maxX + xPadding];
  const domainY = [minY - yPadding, maxY + yPadding];

  // Если все Y одинаковые, добавляем небольшую высоту
  if (domainY[0] === domainY[1]) {
    domainY[0] -= 1;
    domainY[1] += 1;
  }

  return (
    <div className={`${className}`} style={{ height: `${height}px` }}>
      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          data={chartData}
          margin={{
            top: 10,
            right: 10,
            left: 10,
            bottom: 10,
          }}
        >
          {/* Клеточная сетка */}
          {showGrid && (
            <CartesianGrid
              strokeDasharray="2 2"
              stroke="#e5e7eb"
              strokeOpacity={0.3}
              horizontal={true}
              vertical={true}
            />
          )}

          {/* Оси координат */}
          {showAxes && (
            <>
              <XAxis
                dataKey="x"
                stroke="#6b7280"
                fontSize={10}
                tick={{ fill: '#6b7280' }}
                tickLine={{ stroke: '#6b7280' }}
                axisLine={{ stroke: '#6b7280' }}
                tickFormatter={(value) => value.toFixed(1)}
                domain={domainX}
                tickCount={5}
              />
              <YAxis
                stroke="#6b7280"
                fontSize={10}
                tick={{ fill: '#6b7280' }}
                tickLine={{ stroke: '#6b7280' }}
                axisLine={{ stroke: '#6b7280' }}
                tickFormatter={(value) => value.toFixed(1)}
                domain={domainY}
                tickCount={5}
              />
            </>
          )}

          <Tooltip
            contentStyle={{
              backgroundColor: '#374151',
              borderColor: '#4b5563',
              color: '#f9fafb',
              fontSize: '12px',
              padding: '8px',
              borderRadius: '6px'
            }}
            formatter={(value: number) => [value.toFixed(4), 'Значение']}
            labelFormatter={(label) => `X: ${Number(label).toFixed(4)}`}
          />

          {/* Линия графика с темно-зеленым цветом */}
          <Line
            type="monotone"
            dataKey="y"
            name={functionName}
            stroke={TABULATED_COLOR_DARK}
            strokeWidth={2}
            dot={false}
            activeDot={{
              r: 4,
              stroke: TABULATED_COLOR_DARK,
              strokeWidth: 1,
              fill: '#ffffff'
            }}
          />
        </LineChart>
      </ResponsiveContainer>

      {/* Легенда с именем функции в зеленом цвете */}
      <div className="flex items-center justify-center mt-1 px-2">
        <div
          className="w-3 h-3 rounded-full mr-2 flex-shrink-0"
          style={{ backgroundColor: TABULATED_COLOR_DARK }}
        />
        <span
          className="text-xs font-medium truncate"
          style={{ color: TABULATED_COLOR }}
        >
          {functionName}
        </span>
      </div>
    </div>
  );
};

export default TabulatedMiniChart;