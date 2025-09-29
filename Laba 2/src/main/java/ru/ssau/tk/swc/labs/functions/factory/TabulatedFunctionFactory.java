package ru.ssau.tk.swc.labs.functions.factory;
import ru.ssau.tk.swc.labs.functions.*;
public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);
}
