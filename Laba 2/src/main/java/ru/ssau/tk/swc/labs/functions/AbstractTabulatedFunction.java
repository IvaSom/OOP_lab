package ru.ssau.tk.swc.labs.functions;

abstract public class AbstractTabulatedFunction implements TabulatedFunction{
    protected int count;
    abstract protected int floorIndexOfX(double x);
    abstract protected double extrapolateLeft(double x);
    abstract protected double extrapolateRight(double x);
    abstract protected double interpolate (double x, int floorIndex);

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY){return leftY + (x - leftX) / (rightX - leftX) * (rightY - leftY);}

    @Override
    public double apply(double x){
        if (x < getX(0))
            return extrapolateLeft(x);
        if (x > getX(getCount() - 1))
            return extrapolateRight(x);

        if (indexOfX(x) != -1)
            return getY(indexOfX(x));

        return interpolate(x, floorIndexOfX(x));
    }
}
