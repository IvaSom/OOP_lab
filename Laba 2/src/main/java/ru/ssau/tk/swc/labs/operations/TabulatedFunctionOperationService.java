package ru.ssau.tk.swc.labs.operations;
import ru.ssau.tk.swc.labs.functions.*;

public class TabulatedFunctionOperationService {
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
