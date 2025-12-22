import React, { useEffect, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { CompPointsDTO } from '../../types';
import { getCompPointsByFunction } from '../../api/functionApi';

interface CompFunMiniChartProps {
  functionId: number;
  functionName: string;
  className?: string;
  height?: number;
  showGrid?: boolean;
  showAxes?: boolean;
}

// Темно-фиолетовый цвет для композитных функций
const COMPOSITE_COLOR = '#7c3aed'; // Более темный фиолетовый
const COMPOSITE_COLOR_DARK = '#5b21b6'; // Еще темнее для контраста

const CompFunMiniChart: React.FC<CompFunMiniChartProps> = ({
  functionId,
  functionName,
  className = '',
  height = 100,
  showGrid = true,
  showAxes = true
}) => {
  const [points, setPoints] = useState<CompPointsDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadPoints = async () => {
      try {
        setLoading(true);
        const data = await getCompPointsByFunction(functionId);

        // Если точек мало, добавляем интерполяцию
        if (data.length > 0 && data.length < 10) {
          const sorted = data.sort((a, b) => a.x - b.x);
          const minX = sorted[0].x;
          const maxX = sorted[sorted.length - 1].x;
          const step = (maxX - minX) / 20;

          const interpolated = [];
          for (let x = minX; x <= maxX; x += step) {
            // Находим ближайшие точки для линейной интерполяции
            const prevPoints = sorted.filter(p => p.x <= x);
            const nextPoints = sorted.filter(p => p.x >= x);

            if (prevPoints.length > 0 && nextPoints.length > 0) {
              const prev = prevPoints[prevPoints.length - 1];
              const next = nextPoints[0];

              if (prev.x === next.x) {
                interpolated.push({ x, y: prev.y });
              } else {
                const t = (x - prev.x) / (next.x - prev.x);
                const y = prev.y + t * (next.y - prev.y);
                interpolated.push({ x, y });
              }
            }
          }

          // Объединяем оригинальные и интерполированные точки
          const combined = [...sorted, ...interpolated.map((p, i) => ({
            id: -(i + 1), // Отрицательные ID для интерполированных точек
            x: p.x,
            y: p.y,
            functionId
          }))];

          setPoints(combined);
        } else {
          setPoints(data);
        }

      } catch (err) {
        console.error('Error loading points for mini chart:', err);
        setError('Не удалось загрузить точки функции');
      } finally {
        setLoading(false);
      }
    };

    if (functionId) {
      loadPoints();
    }
  }, [functionId]);

  if (loading) {
    return (
      <div className={`flex items-center justify-center ${className}`} style={{ height }}>
        <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-gray-400"></div>
      </div>
    );
  }

  if (error || points.length === 0) {
    return (
      <div className={`flex flex-col items-center justify-center text-gray-400 ${className}`} style={{ height }}>
        <svg className="w-8 h-8 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
        <span className="text-xs">Нет данных для графика</span>
      </div>
    );
  }

  // Сортируем точки по X
  const sortedPoints = [...points]
    .sort((a, b) => a.x - b.x);

  const chartData = sortedPoints.map(point => ({
    x: Number(point.x.toFixed(2)),
    y: Number(point.y.toFixed(2))
  }));

  // Находим диапазоны для осей
  const xValues = chartData.map(d => d.x);
  const yValues = chartData.map(d => d.y);
  const xMin = Math.min(...xValues);
  const xMax = Math.max(...xValues);
  const yMin = Math.min(...yValues);
  const yMax = Math.max(...yValues);
  const xPadding = (xMax - xMin) * 0.1 || 0.1;
  const yPadding = (yMax - yMin) * 0.1 || 0.1;

  return (
    <div className={`${className}`} style={{ height }}>
      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          data={chartData}
          margin={{
            top: 10,
            right: 10,
            left: 10,
            bottom: 10
          }}
        >
          {/* Клеточная сетка как у аналитических */}
          {showGrid && (
            <CartesianGrid
              strokeDasharray="2 2"
              stroke="#e5e7eb"
              strokeOpacity={0.3}
              horizontal={true}
              vertical={true}
            />
          )}

          {/* Оси координат с подписями */}
          {showAxes && (
            <>
              <XAxis
                dataKey="x"
                type="number"
                domain={[xMin - xPadding, xMax + xPadding]}
                stroke="#6b7280"
                fontSize={10}
                tick={{ fill: '#6b7280' }}
                axisLine={{ stroke: '#6b7280' }}
                tickLine={{ stroke: '#6b7280' }}
                tickFormatter={(value) => value.toFixed(1)}
                tickCount={5}
              />
              <YAxis
                type="number"
                domain={[yMin - yPadding, yMax + yPadding]}
                stroke="#6b7280"
                fontSize={10}
                tick={{ fill: '#6b7280' }}
                axisLine={{ stroke: '#6b7280' }}
                tickLine={{ stroke: '#6b7280' }}
                tickFormatter={(value) => value.toFixed(1)}
                tickCount={5}
              />
            </>
          )}

          <Tooltip
            contentStyle={{
              backgroundColor: '#1F2937',
              borderColor: '#374151',
              borderRadius: '0.375rem',
              color: '#F9FAFB',
              fontSize: '12px',
              padding: '8px'
            }}
            formatter={(value: number) => [value.toFixed(4), 'Y']}
            labelFormatter={(label: number) => `X: ${label.toFixed(4)}`}
          />

          {/* Линия графика с темно-фиолетовым цветом */}
          <Line
            type="monotone"
            dataKey="y"
            name={functionName}
            stroke={COMPOSITE_COLOR_DARK}
            strokeWidth={2}
            dot={false}
            activeDot={{
              r: 4,
              fill: COMPOSITE_COLOR_DARK,
              stroke: '#ffffff',
              strokeWidth: 1
            }}
            isAnimationActive={true}
            animationDuration={1000}
          />
        </LineChart>
      </ResponsiveContainer>

      {/* Легенда с именем функции в фиолетовом цвете */}
      <div className="flex items-center justify-center mt-1 px-2">
        <div
          className="w-3 h-3 rounded-full mr-2 flex-shrink-0"
          style={{ backgroundColor: COMPOSITE_COLOR_DARK }}
        />
        <span
          className="text-xs font-medium truncate"
          style={{ color: COMPOSITE_COLOR }}
        >
          {functionName}
        </span>
      </div>
    </div>
  );
};

export default CompFunMiniChart;