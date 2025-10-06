package ru.ssau.tk.swc.labs.operations;

import ru.ssau.tk.swc.labs.functions.*;
import ru.ssau.tk.swc.labs.functions.factory.*;
import ru.ssau.tk.swc.labs.exceptions.*;

public class TabulatedFunctionOperationService {
    private TabulatedFunctionFactory factory;

    public TabulatedFunctionOperationService (TabulatedFunctionFactory f){
        this.factory = f;
    }

    public TabulatedFunctionOperationService (){
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    private interface BiOperation {
        double apply(double u, double v);
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        if (a.getCount() != b.getCount()) {
            throw new InconsistentFunctionsException();
        }
        Point[] pointsA = asPoints(a);
        Point[] pointsB = asPoints(b);
        int n = pointsA.length;
        double[] xValues = new double[n];
        double[] yValues = new double[n];
        for (int i = 0; i < n; i++) {
            if (pointsA[i].x != pointsB[i].x) {
                throw new InconsistentFunctionsException();
            }
            xValues[i] = pointsA[i].x;
            yValues[i] = operation.apply(pointsA[i].y, pointsB[i].y);
        }
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction sum (TabulatedFunction a, TabulatedFunction b){
        return doOperation(a, b, (u, v) -> u + v);
    }

    public TabulatedFunction subtraction (TabulatedFunction a, TabulatedFunction b){
        return doOperation(a, b, (u, v) -> u - v);
    }

    public TabulatedFunction multiplication (TabulatedFunction a, TabulatedFunction b){
        return doOperation(a, b, (u, v) -> u * v);
    }

    public TabulatedFunction division (TabulatedFunction a, TabulatedFunction b){
        return doOperation(a, b, (u, v) -> u / v);
    }

    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        Point[] functionValues = new Point[tabulatedFunction.getCount()];
        int i = 0;

        for (Point p : tabulatedFunction) {
            functionValues[i] = p;
            i++;
        }
        return functionValues;
    }
}
