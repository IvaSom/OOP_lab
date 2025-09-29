package ru.ssau.tk.swc.labs.functions.factory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.swc.labs.functions.ArrayTabulatedFunction;
import ru.ssau.tk.swc.labs.functions.TabulatedFunction;

class ArrayTabulatedFunctionFactoryTest {
    @Test
    void testCreate() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        assertTrue(function instanceof ArrayTabulatedFunction);//проверяем что function это объект класса ArrayTabulatedFunction
    }
}