package ru.ssau.tk.swc.labs.functions.factory;

import ru.ssau.tk.swc.labs.functions.*;

public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory{
    @Override
    public ArrayTabulatedFunction create(double[] xValues, double[] yValues){
        return new ArrayTabulatedFunction(xValues, yValues);
    }
}
