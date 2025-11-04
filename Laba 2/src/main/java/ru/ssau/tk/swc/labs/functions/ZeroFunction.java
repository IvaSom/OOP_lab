package ru.ssau.tk.swc.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeroFunction implements MathFunction{
    private static final Logger logger = LoggerFactory.getLogger(ZeroFunction.class);
    @Override
    public double apply (double x) {
        logger.info("Выполнена функция y=0");
        return 0;
    }
}