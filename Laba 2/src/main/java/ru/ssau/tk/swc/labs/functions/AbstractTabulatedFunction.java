package ru.ssau.tk.swc.labs.functions;

import ru.ssau.tk.swc.labs.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk.swc.labs.exceptions.DifferentLengthOfArraysException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractTabulatedFunction implements TabulatedFunction{
    protected int count;
    abstract protected int floorIndexOfX(double x);
    abstract protected double extrapolateLeft(double x);
    abstract protected double extrapolateRight(double x);
    abstract protected double interpolate (double x, int floorIndex);

    private static final Logger logger = LoggerFactory.getLogger(AbstractTabulatedFunction.class);

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY){
        logger.info("Интерполяция завершена");
        return leftY + (x - leftX) / (rightX - leftX) * (rightY - leftY);
    }

    @Override
    public double apply(double x){
        if (x < getX(0)) {
            logger.info("x добавлен, начата экстраполяция слева");
            return extrapolateLeft(x);
        }
        if (x > getX(getCount() - 1)) {
            logger.info("x добавлен, начата экстраполяция справа");
            return extrapolateRight(x);
        }
        if (indexOfX(x) != -1) {
            logger.info("x найден среди существующих");
            return getY(indexOfX(x));
        }
        logger.info("x добавлен, начата интерполяция");
        return interpolate(x, floorIndexOfX(x));
    }

    void checkLengthIsTheSame(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            logger.error("Колличество точек x и y не совпало");
            throw new DifferentLengthOfArraysException("Количество точек должно совпадать!");
        }
    }

    void checkSorted(double[] xValues) {
        for (int i = 0; i < xValues.length - 1; i++) {
            if (xValues[i] > xValues[i + 1]) {
                logger.error("Значения x записаны не в порядке возрастания");
                throw new ArrayIsNotSortedException("xValues значения должны находиться в порядке возрастания!");
            }
        }
    }
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getSimpleName()).append(" size = ").append(count).append("\n");

        for (Point point : this) {
            stringBuilder.append("[").append(point.x).append("; ").append(point.y).append("]\n");
        }
        logger.info("Приведение к строке завершено");
        return stringBuilder.toString();
    }
}
