package ru.ssau.tk.swc.labs.functions;

public class ConstantFunction implements MathFunction{
    private final double constant;

    public ConstantFunction (double con) {this.constant = con;}

    public double getConstant() {
        return constant;
    }

    @Override
    public double apply(double x) {
        return constant;
    }
}