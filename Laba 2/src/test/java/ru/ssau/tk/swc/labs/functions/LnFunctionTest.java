package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LnFunctionTest {
    @Test
    void testLnFunctionBasic() {
        MathFunction ln = new LnFunction();

        assertEquals(0.0, ln.apply(1), 1e-10);
        assertEquals(1.0, ln.apply(Math.E), 1e-10);

        assertTrue(ln.apply(2) > ln.apply(1));
        assertTrue(ln.apply(10) > ln.apply(5));
    }

    @Test
    void testLnFunctionWithXNegative(){
        MathFunction ln = new LnFunction();

        try {
            ln.apply(-1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("x должен быть > 0", e.getMessage());
        }
    }

    @Test
    void testLnFunctionWithXEqualZero(){
        MathFunction ln = new LnFunction();

        try {
            ln.apply(0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("x должен быть > 0", e.getMessage());
        }
    }
}
