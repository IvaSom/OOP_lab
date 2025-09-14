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
}
