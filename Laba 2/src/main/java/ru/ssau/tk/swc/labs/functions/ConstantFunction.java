package ru.ssau.tk.swc.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantFunction implements MathFunction{
    private static final Logger logger = LoggerFactory.getLogger(ConstantFunction.class);
    private final double constant;

    public ConstantFunction (double con) {this.constant = con;}

    public double getConstant() {
        return constant;
    }

    @Override
    public double apply(double x) {
        logger.info("Установлена константа");
        return constant;
    }
}