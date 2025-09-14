package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SinFunctionTest {
    @Test
    void testSinFunctionBasic() {
        MathFunction sin = new SinFunction();

        assertEquals(0.0, sin.apply(0));
        assertEquals(1.0, sin.apply(Math.PI/2));
        assertEquals(-1.0, sin.apply(-Math.PI/2));
        assertEquals(-sin.apply(1.5), sin.apply(-1.5));
    }

    @Test
    void testSinFunctionRange() {
        MathFunction sin = new SinFunction();

        for (double x = -10; x <= 10; x += 0.5) { assertTrue(sin.apply(x) >= -1.0 && sin.apply(x) <= 1.0);}
    }
}
