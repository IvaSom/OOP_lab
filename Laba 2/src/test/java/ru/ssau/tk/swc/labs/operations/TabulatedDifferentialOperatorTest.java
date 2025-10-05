package ru.ssau.tk.swc.labs.operations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.ssau.tk.swc.labs.functions.*;
import ru.ssau.tk.swc.labs.functions.factory.*;

class TabulatedDifferentialOperatorTest {
    @Test
    public void testDeriveWithArray() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        ArrayTabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        ArrayTabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(xValues[i], derivative.getX(i));
        }

        assertEquals(1.0, derivative.getY(0));
        assertEquals(3.0, derivative.getY(1));
        assertEquals(5.0, derivative.getY(2));
        assertEquals(7.0, derivative.getY(3));
        assertEquals(7.0, derivative.getY(4));
    }
    @Test
    public void testDefaultConstructor() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        assertTrue(operator.getFactory() instanceof ArrayTabulatedFunctionFactory);
    }
    @Test
    public void testDeriveWithList() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        LinkedListTabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(xValues[i], derivative.getX(i));
        }

        assertEquals(1.0, derivative.getY(0));
        assertEquals(3.0, derivative.getY(1));
        assertEquals(5.0, derivative.getY(2));
        assertEquals(7.0, derivative.getY(3));
        assertEquals(7.0, derivative.getY(4));
    }
    @Test
    public void testDeriveTwoPoints() {
        double[] xValues = {0.0, 1.0};
        double[] yValues = {0.0, 2.0};

        LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        LinkedListTabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(2, derivative.getCount());
        assertEquals(0.0, derivative.getX(0));
        assertEquals(1.0, derivative.getX(1));
        assertEquals(2.0, derivative.getY(0));
        assertEquals(2.0, derivative.getY(1));
    }
    @Test
    public void testGetSet() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        LinkedListTabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();

        operator.setFactory(newFactory);
        assertSame(newFactory, operator.getFactory());
    }

    @Test
    public void testWithNegativeValues() {
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(-3.0, derivative.getY(0));
        assertEquals(-1.0, derivative.getY(1));
        assertEquals(1.0, derivative.getY(2));
        assertEquals(3.0, derivative.getY(3));
        assertEquals(3.0, derivative.getY(4));
    }
    @Test
    public void testConst() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {52.0, 52.0, 52.0, 52.0, 52.0};

        ArrayTabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        ArrayTabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(xValues[i], derivative.getX(i));
        }

        assertEquals(0.0, derivative.getY(0));
        assertEquals(0.0, derivative.getY(1));
        assertEquals(0.0, derivative.getY(2));
        assertEquals(0.0, derivative.getY(3));
        assertEquals(0.0, derivative.getY(4));
    }
    @Test
    public void testIdentityFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 2.0, 3.0, 4.0};

        ArrayTabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        ArrayTabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(xValues[i], derivative.getX(i));
        }

        assertEquals(1.0, derivative.getY(0));
        assertEquals(1.0, derivative.getY(1));
        assertEquals(1.0, derivative.getY(2));
        assertEquals(1.0, derivative.getY(3));
        assertEquals(1.0, derivative.getY(4));
    }
}