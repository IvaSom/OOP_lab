package ru.ssau.tk.swc.labs.functions;

public class CompositeFunction implements MathFunction {
    private final MathFunction fun1;
    private final MathFunction fun2;
    public CompositeFunction(MathFunction fun1, MathFunction fun2) {
        this.fun1 = fun1;
        this.fun2 = fun2;
    }
    @Override
    public double apply(double x) {
        return fun2.apply(fun1.apply(x));
    }

}
