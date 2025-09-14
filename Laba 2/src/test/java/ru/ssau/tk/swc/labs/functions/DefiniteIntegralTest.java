package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.ssau.tk.swc.labs.integral.*;

public class DefiniteIntegralTest {
    private static final double epsilon = 1e-3;

    @Test
    void testSqrFunctionAllMethod () {
        SqrFunction f = new SqrFunction();
        DefiniteIntegral integral = new DefiniteIntegral(f);
        final float resultFirstIntegral = 125/3f;
        final double resultSecondIntegral = Math.pow(1000, 3) / 3;
        final float resultThirdIntegral = -26999/3f;

        final float a = 1.42f, b = 3.191f, c = 5.312f;

        final double total = integral.methodTrapezoid(a, c, 10000);
        final double part1 = integral.methodTrapezoid(a, b, 10000);
        final double part2 = integral.methodTrapezoid(b, c, 10000);

        assertEquals(resultFirstIntegral, integral.methodRectangle(0, 5, 1000), epsilon);
        assertEquals(resultFirstIntegral, integral.methodTrapezoid(0, 5, 1000), 1e-4);
        assertEquals(resultFirstIntegral, integral.methodSimpson(0, 5, 1000), 1e-5);
        assertEquals(integral.methodSimpson(-5, 5, 1000) , 2 * integral.methodSimpson(0, 5, 1000), 1e-8);
        assertEquals(total, part1 + part2, 1e-7);
        assertEquals(resultSecondIntegral, integral.methodSimpson(0, 1000, 10000), epsilon);
        assertEquals(resultThirdIntegral, integral.methodRectangle(-1, -30, 10000), epsilon);
        assertEquals(resultThirdIntegral, integral.methodTrapezoid(-1, -30, 10000), epsilon);
        assertEquals(resultThirdIntegral, integral.methodSimpson(-1, -30, 1000), epsilon);
    }

    @Test
    void test
}
