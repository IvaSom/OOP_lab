package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnitFunctionTest {

    @Test
    void testApply(){
        UnitFunction function = new UnitFunction();
        //
        assertEquals(1, function.apply(12.21));
        assertEquals(1, function.apply(73));
        assertEquals(1, function.apply(0));
        assertEquals(1, function.apply(-73));
        assertEquals(1, function.apply(-12.21));
        assertEquals(1, function.apply(1111111112));
        assertEquals(1, function.apply(1.23456789e8));
        assertEquals(1, function.apply(1.23456789e-8));
    }
}

