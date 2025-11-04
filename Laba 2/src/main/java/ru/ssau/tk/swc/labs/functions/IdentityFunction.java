package ru.ssau.tk.swc.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentityFunction implements MathFunction {
    private static final Logger logger = LoggerFactory.getLogger(IdentityFunction.class);
    @Override //показывает, что переопределяем метод,а не создаеи новый
    public double apply(double x) {
        logger.info("Установлен y=x");
        return x;
    }
}