package ru.ssau.tk.swc.labs.functions.factory;
import ru.ssau.tk.swc.labs.functions.*;

public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory{
    @Override
    public LinkedListTabulatedFunction create(double[] xValues, double[] yValues){
        return new LinkedListTabulatedFunction(xValues, yValues);
    }
}
