package ru.ssau.tk.swc.labs.operations;
import ru.ssau.tk.swc.labs.functions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator {

    private static final Logger logger = LoggerFactory.getLogger(LeftSteppingDifferentialOperator.class);

    public LeftSteppingDifferentialOperator (double step){
            super(step);
    }

    @Override
    public MathFunction derive(MathFunction function){
        return new MathFunction() {
            @Override
            public double apply(double x) {
                logger.info("Выплнена левая производная");
                return (function.apply(x) - function.apply(x - getStep())) / getStep();
            }
        };
    }
}
