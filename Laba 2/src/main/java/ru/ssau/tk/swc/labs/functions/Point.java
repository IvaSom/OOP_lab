package ru.ssau.tk.swc.labs.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Point {
    final public double x;
    final public double y;

    private static final Logger logger = LoggerFactory.getLogger(Point.class);

    public Point (double x, double y){
        logger.info("Точка создалась");
        this.x = x;
        this.y = y;
    }
}
