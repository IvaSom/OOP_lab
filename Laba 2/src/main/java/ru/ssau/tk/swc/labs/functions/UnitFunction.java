package ru.ssau.tk.swc.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitFunction implements MathFunction{
    private static final Logger logger = LoggerFactory.getLogger(UnitFunction.class);
    @Override
    public double apply (double x) {
        logger.info("Выполнена функция y=1");
        return 1;
    }
}