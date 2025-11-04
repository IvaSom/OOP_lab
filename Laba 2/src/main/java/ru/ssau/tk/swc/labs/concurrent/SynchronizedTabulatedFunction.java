package ru.ssau.tk.swc.labs.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.swc.labs.functions.Point;
import ru.ssau.tk.swc.labs.functions.TabulatedFunction;
import ru.ssau.tk.swc.labs.operations.TabulatedFunctionOperationService;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction function;
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedTabulatedFunction.class);

    public SynchronizedTabulatedFunction(TabulatedFunction function){
        this.function = function;
    }

    @Override
    public synchronized int getCount(){
        return function.getCount();
    }

    @Override
    public synchronized double getX(int index) {
        return function.getX(index);
    }

    @Override
    public synchronized double getY(int index) {
        return function.getY(index);
    }

    @Override
    public synchronized void setY(int index, double value) {
        function.setY(index, value);
    }

    @Override
    public synchronized int indexOfX(double x) {
        return function.indexOfX(x);
    }

    @Override
    public synchronized int indexOfY(double y) {
        return function.indexOfY(y);
    }

    @Override
    public synchronized double leftBound() {
        return function.leftBound();
    }

    @Override
    public synchronized double rightBound() {
        return function.rightBound();
    }

    @Override
    public synchronized double apply(double x) {
        return function.apply(x);
    }

    public interface Operation<T>{
        T apply(SynchronizedTabulatedFunction function);
    }

    public <T> T doSynchronously(Operation<T> operation){
        synchronized (this){
            return operation.apply(this);
        }
    }

    @Override
    public synchronized Iterator<Point> iterator() {
        synchronized (function) {
            Point[] points = TabulatedFunctionOperationService.asPoints(function);
            return new Iterator<Point>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < points.length;
                }

                @Override
                public Point next() {
                    if (!hasNext()) {
                        logger.error("Не возможный элемент");
                        throw new NoSuchElementException("Следующего элемента нет");
                    }
                    return points[index++];
                }
            };
        }
    }
}

