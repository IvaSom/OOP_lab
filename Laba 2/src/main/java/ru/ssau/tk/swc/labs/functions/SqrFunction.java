package ru.ssau.tk.swc.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqrFunction implements MathFunction {
    private static final Logger logger = LoggerFactory.getLogger(SqrFunction.class);
    @Override
    public double apply (double x) {
        logger.info("Выполнена функция квадрата");
        return x * x;
    }
}