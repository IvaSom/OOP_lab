package ru.ssau.tk.swc.labs.operations;
import ru.ssau.tk.swc.labs.functions.*;

public class TabulatedFunctionOperationService { //Я переделал так как он мне возвращал null иногда
    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        Point[] functionValues = new Point[tabulatedFunction.getCount()];

        for (int i = 0; i < tabulatedFunction.getCount(); i++) {
            double x = tabulatedFunction.getX(i);
            double y = tabulatedFunction.getY(i);
            functionValues[i] = new Point(x, y);
        }
        return functionValues;
    }
}
