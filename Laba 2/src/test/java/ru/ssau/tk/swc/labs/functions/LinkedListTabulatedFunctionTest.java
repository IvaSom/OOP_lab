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
        assertEquals(0, function.floorIndexOfX(0.5));
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

}