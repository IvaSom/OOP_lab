package ru.ssau.tk.swc.labs.functions;

public interface MathFunction {
    double apply(double x);

    default NestedFunction compositeFunction (MathFunction afterFunction) {return new NestedFunction (this, afterFunction);}
}