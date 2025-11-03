package ru.ssau.tk.swc.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CosFunction implements MathFunction{
    private static final Logger logger = LoggerFactory.getLogger(CosFunction.class);
    @Override
    public double apply(double x) {
        logger.info("Выполнена функция косинуса");
        return Math.cos(x);
    }
}
