package ru.ssau.tk.swc.labs.operations;

import ru.ssau.tk.swc.labs.functions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiddleSteppingDifferentialOperator extends SteppingDifferentialOperator {

    private static final Logger logger = LoggerFactory.getLogger(MiddleSteppingDifferentialOperator.class);

    public MiddleSteppingDifferentialOperator (double step){
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function){
        return new MathFunction() {
            @Override
            public double apply(double x) {
                logger.info("Выполнена производная");
                return (function.apply(x + getStep()) - function.apply(x - getStep())) / (2 * getStep());
            }
        };
    }
}


