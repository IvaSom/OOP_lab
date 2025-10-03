package ru.ssau.tk.swc.labs.operations;

import ru.ssau.tk.swc.labs.functions.MathFunction;

public interface DifferentialOperator <T> extends MathFunction {
    T derive(T function);
}
