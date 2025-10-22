package ru.ssau.tk.swc.labs.operations;

import ru.ssau.tk.swc.labs.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk.swc.labs.functions.*;
import ru.ssau.tk.swc.labs.functions.factory.*;

public class TabulatedDifferentialOperator implements DifferentialOperator <TabulatedFunction>{
    private TabulatedFunctionFactory factory;
    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedDifferentialOperator() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }
    @Override
    public TabulatedFunction derive(TabulatedFunction function){//производная
        Point[] points=TabulatedFunctionOperationService.asPoints(function);
        double[] xValues=new double[points.length];
        double[] yValues=new double[points.length];
        for (int i=0; i<points.length-1; i++){
            xValues[i]=points[i].x;
            yValues[i] = (points[i + 1].y - points[i].y) / (points[i + 1].x - points[i].x);
        }
        xValues[xValues.length-1]=points[points.length-1].x;
        yValues[yValues.length-1]=yValues[yValues.length-2];//сказано находить ее слева, поэтому такая же
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        SynchronizedTabulatedFunction syncFunc = (function instanceof SynchronizedTabulatedFunction) ? (SynchronizedTabulatedFunction) function : new SynchronizedTabulatedFunction(function);

        return syncFunc.doSynchronously(func -> derive(func));
    }
}
