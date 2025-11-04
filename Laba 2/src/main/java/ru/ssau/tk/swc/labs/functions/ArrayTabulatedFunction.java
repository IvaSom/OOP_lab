package ru.ssau.tk.swc.labs.functions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable{
    private double[] xValues;
    private double[] yValues;
    private int count;
    private static final Logger logger = LoggerFactory.getLogger(ArrayTabulatedFunction.class);

    public ArrayTabulatedFunction(double[] xArr, double[] yArr) {
        if (xArr.length != yArr.length) {
            logger.error("Колличество точек x и y не совпало");
            throw new IllegalArgumentException("Количество точек должно совпадать!");
        }
        if (xArr.length < 2) {
            logger.error("Недостаточно точек для создания функции");
            throw new IllegalArgumentException("Нужно как минимум 2 точки!");
        }
        this.count = xArr.length;
        this.xValues = Arrays.copyOf(xArr, count);
        this.yValues = Arrays.copyOf(yArr, count);

        for (int i = 1; i < count; i++)
            if (this.xValues[i] <= this.xValues[i - 1]) {
                logger.error("Значения x записаны не в порядке возрастания");
                throw new IllegalArgumentException("xValues значения должны находиться в порядке возрастания!");
            }
        logger.info("Конструктор ArrayTabulatedFunction завершил работу");
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int countTemp) {
        if (countTemp < 2){
            logger.error("Недостаточно точек для создания функции");
            throw new IllegalArgumentException("Нужно как минимум 2 точки!");
        }
        this.count = countTemp;
        this.xValues = new double[count];
        this.yValues = new double[count];

        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        if (xFrom == xTo) {
            Arrays.fill(xValues, xFrom);

            for (int i = 0; i < count; i++)
                yValues[i] = source.apply(xFrom);
        } else {
            double step = (xTo - xFrom) / (count - 1);
            for (int i = 0; i < count; i++) {
                xValues[i] = xFrom + i * step;
                yValues[i] = source.apply(xValues[i]);
            }
        }
        logger.info("Конструктор ArrayTabulatedFunction завершил работу");
    }

    @Override
    public int floorIndexOfX(double x){
        if (x < xValues[0]){
            logger.error("x меньше левой границы");
            throw new IllegalArgumentException("x меньше левой границы");
        }
        if (x > xValues[count - 1]){
            logger.info("x больше правой границы, возвращается индекс последнего x");
            return count - 1;
        }

        for (int i = 0; i < count - 1; i++){
            if (xValues[i] <= x)
                if (xValues[i + 1] > x) {
                    logger.info("Место для x найдено");
                    return i;
                }
        }
        logger.info("Место для x найдено");
        return count - 1;
    }

    @Override
    public double extrapolateLeft(double x) {
        if (count == 1) {
            logger.info("Задан только один элемент x. y возвращается такой же");
            return yValues[0];
        }

        logger.info("Для экстраполяции слева начата интерполяция");
        double temp =  interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
        logger.info("Интерполяция завершена");
        return temp;
    }

    @Override
    public double extrapolateRight(double x) {
        if (count == 1){
            logger.info("Задан только один элемент x. y возвращается такой же");
            return yValues[0];
        }
        logger.info("Для экстраполяции справа начата интерполяция");
        double temp = interpolate(x, xValues[count - 2], xValues[count - 1], yValues[count - 2], yValues[count - 1]);
        logger.info("Интерполяция завершена");
        return temp;
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (count == 1) {
            logger.info("Задан только один элемент x. y возвращается такой же");
            return yValues[0];
        }

        return interpolate(x, xValues[floorIndex], xValues[floorIndex + 1],
                yValues[floorIndex], yValues[floorIndex + 1]);
    }

    @Override
    public int getCount() {
        logger.info("Возвращается количество точек функции");
        return count;
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            logger.error("Невозможный индекс x");
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        }
        logger.info("x по индексу найден");
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count) {
            logger.error("Невозможный индекс y");
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        }
        logger.info("y по индексу найден");
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            logger.error("Невозможный индекс y");
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        }
        logger.info("Значение y по указанному индексу установлено");
        yValues[index] = value;
    }

    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; i++)
            if (xValues[i] == x) {
                logger.info("Индекс для указанного x найден");
                return i;
            }
        logger.info("Индекс для указанного x не найден");
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; i++)
            if (yValues[i]== y) {
                logger.info("Индекс для указанного y найден");
                return i;
            }
        logger.info("Индекс для указанного y не найден");
        return -1;
    }

    @Override
    public double leftBound() {
        logger.info("Возвращается левая граница");
        return xValues[0];
    }

    @Override
    public double rightBound() {
        logger.info("Возвращается правая граница");
        return xValues[count - 1];
    }

    @Override
    public void remove(int index){
        if (index < 0 || index >= count) {
            logger.error("Невозможный индекс x");
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        }

        double[] tempXValues = new double[count - 1];
        double[] tempYValues = new double[count - 1];

        int i = 0;
        while (i < index) {
            tempXValues[i] = xValues[i];
            tempYValues[i] = yValues[i];
            i++;
        }

        while (i < count - 1) {
            tempXValues[i] = xValues[i+1];
            tempYValues[i] = yValues[i+1];
            i++;
        }

        this.xValues = tempXValues;
        this.yValues = tempYValues;
        this.count--;
        logger.info("Точка с указанным индексом была удалена");
    }
    @Override
    public void insert(double x, double y){
        int iX=indexOfX(x);
        int i=0;
        if (iX!=-1){
            yValues[iX]=y;
        }
        else {
            double[] xArr = new double[xValues.length + 1];
            double[] yArr = new double[yValues.length + 1];

            for(; i<xValues.length && x>xValues[i]; i++){
                xArr[i]=xValues[i];
                yArr[i]=yValues[i];
            }
            xArr[i]=x;
            yArr[i]=y;
            System.arraycopy(xValues, i, xArr, i+1, xValues.length-i);
            System.arraycopy(yValues, i, yArr, i+1, yValues.length-i);
            xValues=xArr;
            yValues=yArr;
            count++;
        }
        logger.info("Точка была добавлена в функцию");
    }

    @Override
    public Iterator<Point> iterator() {
        logger.info("Итеретор ArrayTabulatedFunction начал работу");
        return new Iterator<Point>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex != count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    logger.error("Следующего элемента нет");
                    throw new NoSuchElementException("Следующего элемента нет");
                }
                Point point = new Point(xValues[currentIndex], yValues[currentIndex]);
                currentIndex++;
                return point;
            }
        };
    }
}
