package ru.ssau.tk.swc.labs.functions;

public class SqrFunction implements MathFunction {
    @Override
    public double apply (double x) {
        return x * x;
    }
}