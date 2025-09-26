package ru.ssau.tk.swc.labs.functions;

public class LnFunction implements MathFunction{
    @Override
    public double apply(double x) {
        if (x <= 0)
            throw new IllegalArgumentException("x должен быть > 0");
        return Math.log(x);}
}