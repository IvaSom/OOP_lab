package ru.ssau.tk.swc.labs.functions;

public class IdentityFunction implements MathFunction {
    @Override //показывает, что переопределяем метод,а не создаеи новый
    public double apply(double x) {
        return x;
    }
}