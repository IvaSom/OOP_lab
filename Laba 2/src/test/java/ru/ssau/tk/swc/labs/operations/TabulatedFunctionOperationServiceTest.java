package ru.ssau.tk.swc.labs.operations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.ssau.tk.swc.labs.functions.*;
import ru.ssau.tk.swc.labs.functions.factory.*;
import ru.ssau.tk.swc.labs.exceptions.*;

class TabulatedFunctionOperationServiceTest {
    private final double[] xValues = {1.0, 2.0, 3.0, 4.0};
    private final double[] yValues1 = {2.0, 4.0, 6.0, 8.0};
    private final double[] yValues2 = {1.0, 3.0, 5.0, 7.0};

    @Test
    void testMultiplicationArrayFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(xValues, yValues2);

        TabulatedFunction result = service.multiplication(func1, func2);

        assertEquals(4, result.getCount());
        assertEquals(2.0, result.getY(0));
        assertEquals(12.0, result.getY(1));
        assertEquals(30.0, result.getY(2));
        assertEquals(56.0, result.getY(3));

        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], result.getX(i));
        }
    }

    @Test
    void testMultiplicationListFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(xValues, yValues2);

        TabulatedFunction result = service.multiplication(func1, func2);

        assertEquals(4, result.getCount());
        assertEquals(2.0, result.getY(0));
        assertEquals(12.0, result.getY(1));
        assertEquals(30.0, result.getY(2));
        assertEquals(56.0, result.getY(3));

        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], result.getX(i));
        }
    }

    @Test
    void testDivisionArrayFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(xValues, yValues2);

        TabulatedFunction result = service.division(func1, func2);

        assertEquals(4, result.getCount());
        assertEquals(2.0, result.getY(0));
        assertEquals(4.0 / 3.0, result.getY(1), 1e-9);
        assertEquals(6.0 / 5.0, result.getY(2), 1e-9);
        assertEquals(8.0 / 7.0, result.getY(3), 1e-9);

        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], result.getX(i));
        }
    }

    @Test
    void testDivisionListFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(xValues, yValues2);

        TabulatedFunction result = service.division(func1, func2);

        assertEquals(4, result.getCount());
        assertEquals(2.0, result.getY(0));
        assertEquals(4.0 / 3.0, result.getY(1), 1e-9);
        assertEquals(6.0 / 5.0, result.getY(2), 1e-9);
        assertEquals(8.0 / 7.0, result.getY(3), 1e-9);

        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], result.getX(i));
        }
    }
    @Test
    void testDivisionDifferentTypes() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunction arrayFunc = new ArrayTabulatedFunctionFactory().create(xValues, yValues1);
        TabulatedFunction listFunc = new LinkedListTabulatedFunctionFactory().create(xValues, yValues2);

        TabulatedFunction result = service.division(arrayFunc, listFunc);

        assertEquals(4, result.getCount());
        assertEquals(2.0, result.getY(0));
        assertEquals(4.0 / 3.0, result.getY(1), 1e-9);
        assertEquals(6.0 / 5.0, result.getY(2), 1e-9);
        assertEquals(8.0 / 7.0, result.getY(3), 1e-9);
    }



    @Test
    void testDivisionInconsistentFunctions() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] differentX = {1.0, 2.0, 3.0};
        double[] differentY = {1.0, 3.0, 5.0};
        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(differentX, differentY);

        try {
            service.division(func1, func2);
            fail("Expected InconsistentFunctionsException");
        } catch (InconsistentFunctionsException e) {
            assertEquals(null, e.getMessage());
        }
    }

    @Test
    void testDivisionByZero() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] zeroYValues = {0.0, 0.0, 0.0, 0.0};
        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(xValues, zeroYValues);

        TabulatedFunction result = service.division(func1, func2);

        assertEquals(4, result.getCount());
        assertTrue(Double.isInfinite(result.getY(0)) || Double.isNaN(result.getY(0)));
    }
    @Test
    void testSumArrayFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(xValues, yValues2);

        TabulatedFunction result = service.sum(func1, func2);

        assertEquals(4, result.getCount());
        assertEquals(3.0, result.getY(0));
        assertEquals(7.0, result.getY(1));
        assertEquals(11.0, result.getY(2));
        assertEquals(15.0, result.getY(3));

        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], result.getX(i));
        }
    }

    @Test
    void testSumListFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(xValues, yValues2);

        TabulatedFunction result = service.sum(func1, func2);

        assertEquals(4, result.getCount());
        assertEquals(3.0, result.getY(0));
        assertEquals(7.0, result.getY(1));
        assertEquals(11.0, result.getY(2));
        assertEquals(15.0, result.getY(3));

        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], result.getX(i));
        }
    }

    @Test
    void testSubtractionArrayFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(xValues, yValues2);

        TabulatedFunction result = service.subtraction(func1, func2);

        assertEquals(4, result.getCount());
        assertEquals(1.0, result.getY(0));
        assertEquals(1.0, result.getY(1));
        assertEquals(1.0, result.getY(2));
        assertEquals(1.0, result.getY(3));

        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], result.getX(i));
        }
    }

    @Test
    void testSubtractionListFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction func1 = service.getFactory().create(xValues, yValues1);
        TabulatedFunction func2 = service.getFactory().create(xValues, yValues2);

        TabulatedFunction result = service.subtraction(func1, func2);

        assertEquals(4, result.getCount());
        assertEquals(1.0, result.getY(0));
        assertEquals(1.0, result.getY(1));
        assertEquals(1.0, result.getY(2));
        assertEquals(1.0, result.getY(3));

        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], result.getX(i));
        }
    }
}