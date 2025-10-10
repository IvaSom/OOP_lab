package ru.ssau.tk.swc.labs.operations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.ssau.tk.swc.labs.functions.*;
import ru.ssau.tk.swc.labs.functions.factory.*;

class MiddleSteppingDifferentialOperatorTest {
    MiddleSteppingDifferentialOperator op = new MiddleSteppingDifferentialOperator(2);
    LinkedListTabulatedFunction fun = new LinkedListTabulatedFunction(new double[]{4,9,16}, new double[]{2,3,4});
    @Test
    void derive() {
        MathFunction newFunc = op.derive(fun);
        assertEquals(0.2, newFunc.apply(0), 0.1);
        assertEquals(0.1999, newFunc.apply(4), 0.0001);
        assertEquals(0.15714285714285714, newFunc.apply(10));
        assertEquals(0.1428571428571428, newFunc.apply(25));
        assertEquals(0.1428571428571428, newFunc.apply(90));
        assertEquals(0.14285714285714235, newFunc.apply(130), 0.0001);
        assertEquals(0.1428571428571388, newFunc.apply(999), 0.0001);
    }
}
