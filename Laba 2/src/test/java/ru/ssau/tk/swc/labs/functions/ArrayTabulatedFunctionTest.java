package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(0.0, sinTabulated.getY(4), 1e-10);

        // Тест с функцией косинуса
        MathFunction cosFunction = new CosFunction();
        ArrayTabulatedFunction cosTabulated = new ArrayTabulatedFunction(cosFunction, 0, Math.PI/2, 3);

        assertEquals(3, cosTabulated.getCount());
        assertEquals(1.0, cosTabulated.getY(0));
        assertEquals(0.0, cosTabulated.getY(2), 1e-10);

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
        assertEquals(2, function.floorIndexOfX(3.3));
        assertEquals(3, function.floorIndexOfX(4.5));
        assertEquals(4, function.floorIndexOfX(5.0));

        assertEquals(0, function.floorIndexOfX(0.5));
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
    }

    @Test
    void testExtrapolateRight() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Линейная экстраполяция справа
        assertEquals(35.0, function.extrapolateRight(3.5));
        assertEquals(40.0, function.extrapolateRight(4.0));
    }

    @Test
    void testInterpolate() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Линейная интерполяция
        assertEquals(15.0, function.interpolate(1.5, 0));
        assertEquals(25.0, function.interpolate(2.5, 1));
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
        double[] xValues = {1.0, 2.0, 2.5, 3.0};
        double[] yValues = {10.0, 20.0, 25.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Тест indexOfX
        assertEquals(0, function.indexOfX(1.0));
        assertEquals(1, function.indexOfX(2.0));
        assertEquals(3, function.indexOfX(3.0));
        assertEquals(2, function.indexOfX(2.5));
        assertEquals(-1, function.indexOfX(5.0));

        // Тест indexOfY
        assertEquals(0, function.indexOfY(10.0));
        assertEquals(1, function.indexOfY(20.0));
        assertEquals(3, function.indexOfY(30.0));
        assertEquals(2, function.indexOfY(25.0));
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

    @Test
    void testInsert(){
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction fun = new ArrayTabulatedFunction(xValues, yValues);
        //в начало
        fun.insert(0.5, 5.0);
        assertEquals(4, fun.getCount());
        assertEquals(0.5, fun.getX(0));
        assertEquals(5.0, fun.getY(0));
        //в середину
        fun.insert(1.5, 15.0);
        assertEquals(5, fun.getCount());
        assertEquals(1.5, fun.getX(2));
        assertEquals(15.0, fun.getY(2));
        //в конец
        fun.insert(3.5, 35.0);
        assertEquals(6, fun.getCount());
        assertEquals(3.5, fun.getX(5));
        assertEquals(35.0, fun.getY(5));
        //замена
        fun.insert(2.0, 25.0);
        assertEquals(6, fun.getCount());
        assertEquals(2.0, fun.getX(3));
        assertEquals(25.0, fun.getY(3));

    }

    @Test
    void testRemove(){
        double[] xValues = {1.31, 12.12, 51.412, 51.5, 222222};
        double[] yValues = {1.0, 2.0, 3.0, 42.42, 52};

        ArrayTabulatedFunction fun = new ArrayTabulatedFunction(xValues, yValues);

        fun.remove(4);
        assertEquals(4, fun.getCount());

        fun.remove(0);
        assertEquals(3, fun.getCount());
        assertEquals(12.12, fun.getX(0));
        assertEquals(2, fun.getY(0));

        fun.remove(1);
        assertEquals(2, fun.getCount());
        assertEquals(51.5, fun.getX(1));
        assertEquals(42.42, fun.getY(1));
    }

    @Test
    void testConstructorWithDifferentArrayLengths() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0};

        try {
            new ArrayTabulatedFunction(xValues, yValues);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Количество точек должно совпадать!", e.getMessage());
        }
    }

    @Test
    void testFirstConstructorWithLessThan2Points() {
        double[] smallX = {1.0};
        double[] smallY = {10.0};

        try {
            new ArrayTabulatedFunction(smallX, smallY);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Нужно как минимум 2 точки!", e.getMessage());
        }
    }

    @Test
    void testSecondConstructorWithLessThan2Points(){
        SqrFunction f = new SqrFunction();
        double b = 20, a =10;
        int temp = -1;

        try {
            new ArrayTabulatedFunction(f, a, b, temp);
            fail("Expected IllegalArgumentException");
        }catch (IllegalArgumentException e) {
            assertEquals("Нужно как минимум 2 точки!", e.getMessage());
        }
    }

    @Test
    void testFirstConstructorWithDuplicateXValues() {
        double[] duplicateX = {1.0, 2.0, 2.0, 4.0};
        double[] validY = {10.0, 20.0, 30.0, 40.0};

        try {
            new ArrayTabulatedFunction(duplicateX, validY);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("xValues значения должны находиться в порядке возрастания!", e.getMessage());
        }
    }

    @Test
    void testFirstConstructorWithDescendingXValues() {
        double[] descendingX = {4.0, 3.0, 2.0, 1.0};
        double[] validY = {10.0, 20.0, 30.0, 40.0};

        try {
            new ArrayTabulatedFunction(descendingX, validY);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("xValues значения должны находиться в порядке возрастания!", e.getMessage());
        }
    }

    @Test
    void testGetXWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        try {
            function.getX(-1);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Невозможный индекс!", e.getMessage());
        }
    }

    @Test
    void testGetXWithIndexOutOfBounds() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        try {
            function.getX(5);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Невозможный индекс!", e.getMessage());
        }
    }

    @Test
    void testGetYWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        try {
            function.getY(-1);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Невозможный индекс!", e.getMessage());
        }
    }

    @Test
    void testGetYWithIndexOutOfBounds() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        try {
            function.getY(3);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Невозможный индекс!", e.getMessage());
        }
    }

    @Test
    void testSetYWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        try {
            function.setY(-1, 50.0);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Невозможный индекс!", e.getMessage());
        }
    }

    @Test
    void testSetYWithIndexOutOfBounds() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        try {
            function.setY(5, 50.0);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Невозможный индекс!", e.getMessage());
        }
    }

    @Test
    void testRemoveWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        try {
            function.remove(-1);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Невозможный индекс!", e.getMessage());
        }
    }

    @Test
    void testRemoveWithIndexOutOfBounds() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        try {
            function.remove(5);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Невозможный индекс!", e.getMessage());
        }
    }
}