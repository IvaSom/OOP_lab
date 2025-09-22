package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTest {

    @Test
    void testConstructorWithArrays() {
        // Тест корректного создания
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(40.0, function.getY(3));
    }

    @Test
    void testConstructorWithMathFunction() {
        // Тест с функцией синуса
        MathFunction sinFunction = new SinFunction();
        ArrayTabulatedFunction sinTabulated = new ArrayTabulatedFunction(sinFunction, 0, Math.PI, 5);

        assertEquals(5, sinTabulated.getCount());
        assertEquals(0.0, sinTabulated.getX(0));
        assertEquals(Math.PI, sinTabulated.getX(4));
        assertEquals(0.0, sinTabulated.getY(0));
        assertEquals(0.0, sinTabulated.getY(4));

        // Тест с функцией косинуса
        MathFunction cosFunction = new CosFunction();
        ArrayTabulatedFunction cosTabulated = new ArrayTabulatedFunction(cosFunction, 0, Math.PI/2, 3);

        assertEquals(3, cosTabulated.getCount());
        assertEquals(1.0, cosTabulated.getY(0));
        assertEquals(0.0, cosTabulated.getY(2));

        // Тест с одинаковыми границами
        ArrayTabulatedFunction constantFunction = new ArrayTabulatedFunction(sinFunction, 5.0, 5.0, 4);
        assertEquals(4, constantFunction.getCount());
        assertEquals(5.0, constantFunction.getX(0));
        assertEquals(5.0, constantFunction.getX(3));
        assertEquals(sinFunction.apply(5.0), constantFunction.getY(0));

        // Тест с обратными границами (xFrom > xTo)
        ArrayTabulatedFunction reversedFunction = new ArrayTabulatedFunction(cosFunction, 10.0, 0.0, 5);
        assertEquals(0.0, reversedFunction.getX(0));
        assertEquals(10.0, reversedFunction.getX(4));
    }

    @Test
    void testFloorIndexOfX() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Тест значений внутри диапазона
        assertEquals(0, function.floorIndexOfX(1.0));
        assertEquals(0, function.floorIndexOfX(1.5));
        assertEquals(1, function.floorIndexOfX(2.0));
        assertEquals(1, function.floorIndexOfX(2.7));
        assertEquals(3, function.floorIndexOfX(4.5));
        assertEquals(4, function.floorIndexOfX(5.0));

        // Тест экстраполяции слева
        assertEquals(0, function.floorIndexOfX(0.5));

        // Тест экстраполяции справа
        assertEquals(4, function.floorIndexOfX(6.0));
    }

    @Test
    void testExtrapolateLeft() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Линейная экстраполяция слева
        assertEquals(5.0, function.extrapolateLeft(0.5));
        assertEquals(0.0, function.extrapolateLeft(0.0));

        // Тест с одной точкой
        double[] singleX = {1.0};
        double[] singleY = {10.0};
        ArrayTabulatedFunction singleFunction = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(10.0, singleFunction.extrapolateLeft(0.5));
    }

    @Test
    void testExtrapolateRight() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Линейная экстраполяция справа
        assertEquals(35.0, function.extrapolateRight(3.5));
        assertEquals(40.0, function.extrapolateRight(4.0));

        // Тест с одной точкой
        double[] singleX = {1.0};
        double[] singleY = {10.0};
        ArrayTabulatedFunction singleFunction = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(10.0, singleFunction.extrapolateRight(2.0));
    }

    @Test
    void testInterpolate() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Линейная интерполяция
        assertEquals(15.0, function.interpolate(1.5, 0));
        assertEquals(25.0, function.interpolate(2.5, 1));

        // Тест с одной точкой
        double[] singleX = {1.0};
        double[] singleY = {10.0};
        ArrayTabulatedFunction singleFunction = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(10.0, singleFunction.interpolate(1.5, 0));
    }

    @Test
    void testGettersAndSetters() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Тест getX и getY
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(10.0, function.getY(0));
        assertEquals(20.0, function.getY(1));
        assertEquals(30.0, function.getY(2));

        // Тест setY
        function.setY(1, 25.0);
        assertEquals(25.0, function.getY(1));
    }

    @Test
    void testIndexOfXAndY() {
        double[] xValues = {1.0, 2.0, 3.0, 2.5};
        double[] yValues = {10.0, 20.0, 30.0, 25.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Тест indexOfX
        assertEquals(0, function.indexOfX(1.0));
        assertEquals(1, function.indexOfX(2.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(3, function.indexOfX(2.5));
        assertEquals(-1, function.indexOfX(5.0));

        // Тест indexOfY
        assertEquals(0, function.indexOfY(10.0));
        assertEquals(1, function.indexOfY(20.0));
        assertEquals(2, function.indexOfY(30.0));
        assertEquals(3, function.indexOfY(25.0));
        assertEquals(-1, function.indexOfY(50.0));
    }

    @Test
    void testBounds() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(1.0, function.leftBound());
        assertEquals(4.0, function.rightBound());
    }

    @Test
    void testWithDifferentMathFunctions() {
        // Тест с натуральным логарифмом
        MathFunction lnFunction = new LnFunction();
        ArrayTabulatedFunction lnTabulated = new ArrayTabulatedFunction(lnFunction, 1, 10, 10);
        assertEquals(10, lnTabulated.getCount());
        assertEquals(0.0, lnTabulated.getY(0)); // ln(1) = 0
        assertEquals(Math.log(10), lnTabulated.getY(9));

        // Тест с квадратом
        MathFunction sqrFunction = new SqrFunction();
        ArrayTabulatedFunction sqrTabulated = new ArrayTabulatedFunction(sqrFunction, 0, 5, 6);
        assertEquals(6, sqrTabulated.getCount());
        assertEquals(0.0, sqrTabulated.getY(0));
        assertEquals(25.0, sqrTabulated.getY(5));

        // Тест с вложенной функцией (например, sin(x^2))
        MathFunction nestedFunction = new CompositeFunction(new SinFunction(), new SqrFunction());
        ArrayTabulatedFunction nestedTabulated = new ArrayTabulatedFunction(nestedFunction, 0, Math.PI, 5);
        assertEquals(5, nestedTabulated.getCount());
        assertEquals(Math.sin(0), nestedTabulated.getY(0));
    }

    @Test
    void testEdgeCases() {
        // Тест с очень близкими значениями x
        double[] closeX = {1.0, 1.0000001, 1.0000002};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(closeX, yValues);
        assertEquals(3, function.getCount());

        // Тест с отрицательными значениями
        double[] negX = {-5.0, -3.0, -1.0};
        double[] negY = {25.0, 9.0, 1.0};
        ArrayTabulatedFunction negFunction = new ArrayTabulatedFunction(negX, negY);
        assertEquals(-5.0, negFunction.leftBound());
        assertEquals(-1.0, negFunction.rightBound());
    }
}
