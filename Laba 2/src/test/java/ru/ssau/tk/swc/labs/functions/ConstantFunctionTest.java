package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConstantFunctionTest {

    @Test
    void testApply () {
        ConstantFunction function = new ConstantFunction(101);
        //
        assertEquals(101, function.apply(12.21));
        assertEquals(101, function.apply(73));
        assertEquals(101, function.apply(0));
        assertEquals(101, function.apply(-73));
        assertEquals(101, function.apply(-12.21));
        assertEquals(101, function.apply(1000000002));
        assertEquals(101, function.apply(1.23456789e8));
        assertEquals(101, function.apply(1.23456789e-8));
    }

}
