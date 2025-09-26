package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CosFunctionTest {
    @Test
    void testCosFunctionBasic() {
        MathFunction cos = new CosFunction();

        assertEquals(1.0, cos.apply(0));
        assertEquals(0.0, cos.apply(Math.PI/2), 10e-7);
        assertEquals(-1.0, cos.apply(Math.PI));
        assertEquals(cos.apply(1.5), cos.apply(-1.5));
    }

    @Test
    void testCosFunctionRange() {
        MathFunction cos = new CosFunction();

        for (double x = -10; x <= 10; x += 0.5) { assertTrue(cos.apply(x) >= -1.0 && cos.apply(x) <= 1.0);}
    }
}
