package ru.ssau.tk.swc.labs.operations;

import ru.ssau.tk.swc.labs.functions.*;

public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator {

    public RightSteppingDifferentialOperator (double step){
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function){
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (function.apply(x) - function.apply(x + getStep())) / getStep();
            }
        };
    }
}



