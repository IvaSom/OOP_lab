package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdentityFunctionTest {
    @Test
    void testApply() {
        IdentityFunction identityFunction = new IdentityFunction();
        Assertions.assertEquals(137, identityFunction.apply(137));
        Assertions.assertEquals(52.42, identityFunction.apply(52.42));
        Assertions.assertEquals(0, identityFunction.apply(0));
    }
}