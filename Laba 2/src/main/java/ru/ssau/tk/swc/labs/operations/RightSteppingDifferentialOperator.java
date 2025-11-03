package ru.ssau.tk.swc.labs.operations;

import ru.ssau.tk.swc.labs.functions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator {

    private static final Logger logger = LoggerFactory.getLogger(RightSteppingDifferentialOperator.class);

    public RightSteppingDifferentialOperator (double step){
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function){
        return new MathFunction() {

            @Override
            public double apply(double x) {
                logger.info("Выполнена правая производная");
            return (function.apply(x) - function.apply(x + getStep())) / getStep();
            }
        };
    }
}



