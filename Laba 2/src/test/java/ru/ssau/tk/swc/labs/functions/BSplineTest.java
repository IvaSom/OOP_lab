package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BSplineTest {
    @Test
    void testDeg0() {
        //сплайн степени 0
        double[] knots = {0, 1, 2, 3};
        BSpline spline = new BSpline(knots, 0, 1);

        assertEquals(0.0, spline.apply(0.5));
        assertEquals(1.0, spline.apply(1.0));
        assertEquals(1.0, spline.apply(1.5));
        assertEquals(0.0, spline.apply(2.0));
    }
    @Test
    void testDeg1() {
        //сплайн степени 1
        double[] knots = {0, 1, 2, 3};
        BSpline spline = new BSpline(knots, 1, 1);

        assertEquals(0.0, spline.apply(0.0));
        assertEquals(0.5, spline.apply(1.5));
        assertEquals(0.0, spline.apply(3.0));
    }
    @Test
    void testDeg2() {
        //сплайн степени 2
        double[] knots = {0, 0, 0, 1, 2, 3, 3, 3};
        BSpline spline = new BSpline(knots, 2, 2);

        assertEquals(0.0, spline.apply(-1.0));
        assertEquals(0.125, spline.apply(0.5));
        assertEquals(0.5, spline.apply(1.0));
    }
    @Test
    void testDeg3() {
        //сплайн степени 3
        double[] knots = {0, 1, 2, 3, 4, 5};
        BSpline spline = new BSpline(knots, 3, 1);

        assertEquals(0.0, spline.apply(0.0));
        assertEquals(0.0, spline.apply(1.0));
        assertEquals(0.020833333333333332, spline.apply(1.5), 1e-10);
        assertEquals(0.16666666666666666, spline.apply(2.0), 1e-10);
    }

    @Test
    void testNotEnoughKnots() {
        double[] knots = {0, 1, 2};
        try {
            BSpline spline= new BSpline(knots, 3, 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Для B-сплайна степени k нужно как минимум k + 1 узел", e.getMessage());
        }
    }

    @Test
    void testInvalidIndex1() {
        double[] knots = {0, 1, 2, 3, 4, 5};
        try {
            BSpline spline= new BSpline(knots, 3, -52);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Недопустимый индекс базисной функции", e.getMessage());
        }
    }

    @Test
    void testInvalidIndex2() {
        double[] knots = {0, 1, 2, 3, 4, 5};
        try {
            BSpline spline= new BSpline(knots, 3, 2);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Недопустимый индекс базисной функции", e.getMessage());
        }
    }
}