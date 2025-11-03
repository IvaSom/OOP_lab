package ru.ssau.tk.swc.labs.operations;

import ru.ssau.tk.swc.labs.functions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class SteppingDifferentialOperator implements DifferentialOperator <MathFunction>{
    private static final Logger logger = LoggerFactory.getLogger(SteppingDifferentialOperator.class);
    private double step;

    public SteppingDifferentialOperator (double st){
        logger.info("Установлен шаг");
        if (st <= 0 || Double.isNaN(st) || Double.isInfinite(st)) {
            logger.error("Ошибка: некоректный шаг");
            throw new IllegalArgumentException();
        }
        this.step = st;
    }

    public double getStep(){
        return this.step;
    }

    public void setStep(double st){
        this.step = st;
    }
}
