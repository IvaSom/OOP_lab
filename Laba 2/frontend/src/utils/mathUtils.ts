export const calculateFunctionValues = (
  functionType: number,
  xMin: number,
  xMax: number,
  steps: number
): { x: number; y: number }[] => {
  const result = [];
  const step = (xMax - xMin) / steps;

  for (let i = 0; i <= steps; i++) {
    const x = xMin + i * step;
    let y = 0;

    switch (functionType) {
      case 1: // sin
        y = Math.sin(x);
        break;
      case 2: // cos
        y = Math.cos(x);
        break;
      case 3: // x²
        y = x * x;
        break;
      case 4: // constant (1)
        y = 1;
        break;
      case 5: // zero
        y = 0;
        break;
      case 6: // ln
        y = x > 0 ? Math.log(x) : -Infinity;
        break;
      case 7: // identity (x)
        y = x;
        break;
      case 8: // unit (1)
        y = 1;
        break;
      default:
        y = 0;
    }

    result.push({ x, y });
  }

  return result;
};

export const differentiateFunction = (
  points: { x: number; y: number }[],
  step: number
): { x: number; y: number }[] => {
  const result = [];

  for (let i = 1; i < points.length - 1; i++) {
    const x = points[i].x;
    const yPrev = points[i - 1].y;
    const yNext = points[i + 1].y;

    // Центральная разность для вычисления производной
    const derivative = (yNext - yPrev) / (2 * step);
    result.push({ x, y: derivative });
  }

  return result;
};

export const integrateFunction = (
  points: { x: number; y: number }[],
  threads: number = 1
): number => {
  if (points.length < 2) return 0;

  let integral = 0;
  const step = points[1].x - points[0].x;

  // Метод трапеций
  for (let i = 0; i < points.length - 1; i++) {
    integral += (points[i].y + points[i + 1].y) * step / 2;
  }

  return integral;
};