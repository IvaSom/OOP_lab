package ru.ssau.tk.swc.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LnFunction implements MathFunction{
    private static final Logger logger = LoggerFactory.getLogger(LnFunction.class);
    @Override
    public double apply(double x) {
        if (x <= 0) {
            logger.error("Логарифм посчитать не получилось. x должен быть > 0");
            throw new IllegalArgumentException("x должен быть > 0");
        }
        logger.info("Выполнена функция косинуса");
        return Math.log(x);}
}