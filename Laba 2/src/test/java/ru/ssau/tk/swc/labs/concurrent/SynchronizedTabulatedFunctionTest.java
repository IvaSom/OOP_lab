package ru.ssau.tk.swc.labs.concurrent;

import ru.ssau.tk.swc.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.swc.labs.functions.Point;
import ru.ssau.tk.swc.labs.functions.TabulatedFunction;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.swc.labs.functions.factory.ArrayTabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

class SynchronizedTabulatedFunctionTest {
    @Test
    void testIterator() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction fun = new LinkedListTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction synchronizedFunction = new SynchronizedTabulatedFunction(fun);

        Iterator<Point> iterator = synchronizedFunction.iterator();
        int i = 0;

        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(point.x, xValues[i]);
            assertEquals(point.y, yValues[i]);
            i++;
        }

        assertEquals(i, xValues.length);
    }

    @Test
    void testIteratorFor() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction fun = new LinkedListTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction synchronizedFunction = new SynchronizedTabulatedFunction(fun);

        int i = 0;
        for (Point point : synchronizedFunction) {
            assertEquals(point.x, xValues[i]);
            assertEquals(point.y, yValues[i]);
            i++;
        }

        assertEquals(i, xValues.length);
    }

    @Test
    void testIteratorNoSuchElementException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction fun = new LinkedListTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction synchronizedFunction = new SynchronizedTabulatedFunction(fun);

        Iterator<Point> iterator = synchronizedFunction.iterator();
        int i = 0;

        while (iterator.hasNext()) {
            Point point = iterator.next();
        }

        try {
            Point point = iterator.next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            assertEquals("Следующего элемента нет", e.getMessage());
        }
    }

    @Test
    void testIteratorWithModification() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction fun = new LinkedListTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction synchronizedFunction = new SynchronizedTabulatedFunction(fun);

        Iterator<Point> iterator = synchronizedFunction.iterator();

        //меняем исходную функцию
        synchronizedFunction.setY(0, 100.0);
        synchronizedFunction.setY(1, 200.0);
        synchronizedFunction.setY(2, 300.0);

        Point firstPoint = iterator.next();
        assertEquals(1.0, firstPoint.x);
        assertEquals(10.0, firstPoint.y);

        Point secondPoint = iterator.next();
        assertEquals(2.0, secondPoint.x);
        assertEquals(20.0, secondPoint.y);

        Point thirdPoint = iterator.next();
        assertEquals(3.0, thirdPoint.x);
        assertEquals(30.0, thirdPoint.y);
        //итератор с копией работает
    }

    @Test
    void testIteratorEmptyFunction() {
        double[] xValues = {};
        double[] yValues = {};

        try {
            TabulatedFunction fun = new LinkedListTabulatedFunction(xValues, yValues);
            SynchronizedTabulatedFunction synchronizedFunction = new SynchronizedTabulatedFunction(fun);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Нужно как минимум 2 точки!", e.getMessage());
        }
    }

    private TabulatedFunction createTestFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        return new ArrayTabulatedFunctionFactory().create(xValues, yValues);
    }

    @Test
    public void testGetCount() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(syncFunc.getCount(), 4);
    }

    @Test
    public void testGetX() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(syncFunc.getX(0), 1.0);
        assertEquals(syncFunc.getX(2), 3.0);
    }

    @Test
    public void testGetY() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(syncFunc.getY(0), 10.0);
        assertEquals(syncFunc.getY(3), 40.0);
    }

    @Test
    public void testSetY() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        syncFunc.setY(1, 25.0);
        assertEquals(syncFunc.getY(1), 25.0);
        assertEquals(original.getY(1), 25.0); // Проверяем, что изменилась и исходная функция
    }

    @Test
    public void testIndexOfX() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(syncFunc.indexOfX(3.0), 2);
        assertEquals(syncFunc.indexOfX(5.0), -1); // Несуществующее значение
    }

    @Test
    public void testIndexOfY() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(syncFunc.indexOfY(30.0), 2);
        assertEquals(syncFunc.indexOfY(100.0), -1); // Несуществующее значение
    }

    @Test
    public void testLeftBound() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(syncFunc.leftBound(), 1.0);
    }

    @Test
    public void testRightBound() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(syncFunc.rightBound(), 4.0);
    }

    @Test
    public void testApply() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        assertEquals(syncFunc.apply(2.0), 20.0);
        assertEquals(syncFunc.apply(1.5), 15.0); // Интерполяция
    }

    @Test
    public void testDoSynchronouslyWithReturnValue() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        // Операция с возвращаемым значением
        Double result = syncFunc.doSynchronously(func -> {
            double sum = 0;
            for (int i = 0; i < func.getCount(); i++) {
                sum += func.getY(i);
            }
            return sum;
        });

        assertEquals(result, 100.0); // 10 + 20 + 30 + 40 = 100
    }

    @Test
    public void testDoSynchronouslyWithoutReturnValue() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        // Операция без возвращаемого значения (Void)
        syncFunc.doSynchronously(func -> {
            for (int i = 0; i < func.getCount(); i++) {
                func.setY(i, func.getY(i) * 2);
            }
            return null;
        });

        // Проверяем, что значения удвоились
        assertEquals(syncFunc.getY(0), 20.0);
        assertEquals(syncFunc.getY(1), 40.0);
    }

    @Test
    public void testDoSynchronouslyWithComplexOperation() {
        TabulatedFunction original = createTestFunction();
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(original);

        // Сложная операция: читаем и пишем в одном блоке
        String result = syncFunc.doSynchronously(func -> {
            double firstY = func.getY(0);
            func.setY(0, firstY + 5);
            return "First Y was: " + firstY;
        });

        assertEquals(result, "First Y was: 10.0");
        assertEquals(syncFunc.getY(0), 15.0); // 10 + 5 = 15
    }
}