package ru.ssau.tk.swc.labs.functions;

public class NestedFunction implements MathFunction {
    private MathFunction first;
    private MathFunction second;

    public NestedFunction (MathFunction f, MathFunction s){this.first = f; this.second = s;}

    public double apply (double x){return first.apply(second.apply(x));}
}