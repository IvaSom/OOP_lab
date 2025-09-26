package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionTest {
    @Test
    void testConstructor1() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.leftBound());
        assertEquals(4.0, function.rightBound());
        assertEquals(2.0, function.getX(1));
        assertEquals(30.0, function.getY(2));


        assertEquals(20.0, function.getY(1));
        function.setY(1, 25.0);
        assertEquals(25.0, function.getY(1));

    }

    @Test
    void testConstructor2() {
        SqrFunction source = new SqrFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(source, 0.0, 4.0, 5);

        assertEquals(5, function.getCount());
        assertEquals(0.0, function.leftBound());
        assertEquals(4.0, function.rightBound());
        assertEquals(0.0, function.getY(0));
        assertEquals(16.0, function.getY(4));
    }

    @Test
    void testIndexOf() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(1.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(-1, function.indexOfX(5.0));

        assertEquals(1, function.indexOfY(20.0));
        assertEquals(3, function.indexOfY(40.0));
        assertEquals(-1, function.indexOfY(50.0));
    }

    @Test
    void testFloorIndexOfX() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.floorIndexOfX(1.5));
        assertEquals(1, function.floorIndexOfX(2.0));
        assertEquals(2, function.floorIndexOfX(3.5));
        assertEquals(4, function.floorIndexOfX(5.0));
    }

    @Test
    void testExtrapolateLeft() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(5.0, function.extrapolateLeft(0.5));
    }

    @Test
    void testExtrapolateRight() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(35.0, function.extrapolateRight(3.5));
    }

    @Test
    void testInterpolate() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(15.0, function.interpolate(1.5, 0));
        assertEquals(25.0, function.interpolate(2.5, 1));
    }

    @Test
    void testRemove(){
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        //из середины
        function.remove(2); // удаляем элемент с индексом 2 (x=3.0)
        assertEquals(4, function.getCount());
        assertEquals(4.0, function.getX(2));
        assertEquals(40.0, function.getY(2));
        //из начала
        function.remove(0); // удаляем первый элемент (x=1.0)
        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(0));
        assertEquals(20.0, function.getY(0));
        //из конца
        function.remove(2); // удаляем последний элемент (x=5.0)
        assertEquals(2, function.getCount());
        assertEquals(4.0, function.getX(1));
        assertEquals(40.0, function.getY(1));
        //в итоге
        assertEquals(2.0, function.getX(0));
        assertEquals(20.0, function.getY(0));
        assertEquals(4.0, function.getX(1));
        assertEquals(40.0, function.getY(1));
    }

    @Test
    void testInsert(){
        double[] xValues = {0.11, 33};
        double[] yValues = {1.0, 2.0};

        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xValues,yValues);

        list.insert(15, 1.3);
        assertEquals(3, list.getCount());
        assertEquals(15, list.getX(1));
        assertEquals(1.3, list.getY(1));

        list.insert(10e-6, 0.1);
        assertEquals(4, list.getCount());
        assertEquals(10e-6, list.getX(0));
        assertEquals(0.1, list.getY(0));

        list.insert(42.42, 33.0);
        assertEquals(5, list.getCount());
        assertEquals(42.42, list.getX(4));
        assertEquals(33, list.getY(4));

        list.insert(42.42, 42.0);
        assertEquals(5, list.getCount());
        assertEquals(42.42, list.getX(4));
        assertEquals(42.0, list.getY(4));
    }
}