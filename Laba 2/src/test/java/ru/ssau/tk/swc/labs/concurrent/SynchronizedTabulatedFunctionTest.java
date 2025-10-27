package ru.ssau.tk.swc.labs.concurrent;

import ru.ssau.tk.swc.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.swc.labs.functions.Point;
import ru.ssau.tk.swc.labs.functions.TabulatedFunction;

import org.junit.jupiter.api.Test;
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
}