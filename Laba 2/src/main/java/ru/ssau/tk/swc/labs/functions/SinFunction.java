package ru.ssau.tk.swc.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinFunction implements MathFunction{
    private static final Logger logger = LoggerFactory.getLogger(SinFunction.class);
    @Override
    public double apply(double x) {
        logger.info("Выполнена функция синуса");
        return Math.sin(x);
    }
}
