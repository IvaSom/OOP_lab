package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ZeroFunctionTest {

    @Test
    void testApply(){
        ZeroFunction function = new ZeroFunction();

        assertEquals(0, function.apply(12.21));
        assertEquals(0, function.apply(73));
        assertEquals(0, function.apply(0));
        assertEquals(0, function.apply(-73));
        assertEquals(0, function.apply(-12.21));
        assertEquals(0, function.apply(1000000002));
        assertEquals(0, function.apply(1.23456789e8));
        assertEquals(0, function.apply(1.23456789e-8));
    }
}
