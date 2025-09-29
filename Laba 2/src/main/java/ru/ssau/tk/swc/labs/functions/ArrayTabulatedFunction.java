package ru.ssau.tk.swc.labs.functions;

import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.Iterator;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable{
    private double[] xValues;
    private double[] yValues;
    private int count;

    public ArrayTabulatedFunction(double[] xArr, double[] yArr) {
        if (xArr.length != yArr.length)
            throw new IllegalArgumentException("Количество точек должно совпадать!");

        if (xArr.length < 2)
            throw new IllegalArgumentException("Нужно как минимум 2 точки!");

        this.count = xArr.length;
        this.xValues = Arrays.copyOf(xArr, count);
        this.yValues = Arrays.copyOf(yArr, count);

        for (int i = 1; i < count; i++)
            if (this.xValues[i] <= this.xValues[i - 1])
                throw new IllegalArgumentException("xValues значения должны находиться в порядке возрастания!");
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int countTemp) {
        if (countTemp < 2)
            throw new IllegalArgumentException("Нужно как минимум 2 точки!");

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
    }

    @Override
    public int floorIndexOfX(double x) {
        if (x < xValues[0]) return 0;

        for (int i = 0; i < count - 1; i++) {
            if (x < xValues[i + 1]) {
                return i;
            }
        }

        return count - 1;
    }

    @Override
    public double extrapolateLeft(double x) {
        return interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
    }

    @Override
    public double extrapolateRight(double x) {
      return interpolate(x, xValues[count - 2], xValues[count - 1], yValues[count - 2], yValues[count - 1]);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
       return interpolate(x, xValues[floorIndex], xValues[floorIndex + 1], yValues[floorIndex], yValues[floorIndex + 1]);
    }

    @Override
    public int getCount() {return count;}

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count)
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count)
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count)
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        yValues[index] = value;
    }

    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; i++)
            if (xValues[i] == x)
                return i;
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; i++)
            if (yValues[i]== y)
                return i;
        return -1;
    }

    @Override
    public double leftBound() {return xValues[0];}

    @Override
    public double rightBound() {return xValues[count - 1];}

    @Override
    public void remove(int index){
        if (index < 0 || index >= count)
            throw new IndexOutOfBoundsException("Невозможный индекс!");

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
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex != count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Следующего элемента нет");
                }
                Point point = new Point(xValues[currentIndex], yValues[currentIndex]);
                currentIndex++;
                return point;
            }
        };
    }
}
