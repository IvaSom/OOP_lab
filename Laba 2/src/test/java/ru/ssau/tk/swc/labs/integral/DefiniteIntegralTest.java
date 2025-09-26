package ru.ssau.tk.swc.labs.integral;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.ssau.tk.swc.labs.functions.*;

public class DefiniteIntegralTest {
    private static final double epsilon = 1e-3;

    @Test
    void testSqrFunctionAllMethod() {

        SqrFunction f = new SqrFunction();
        DefiniteIntegral integral = new DefiniteIntegral(f);
        final float resultFirstIntegral = 125/3f;
        final double resultSecondIntegral = Math.pow(1000, 3) / 3;
        final float resultThirdIntegral = -26999/3f;

        final float a = 1.42f, b = 3.191f, c = 5.312f;

        final double total = integral.methodTrapezoid(a, c, 10000);
        final double part1 = integral.methodTrapezoid(a, b, 10000);
        final double part2 = integral.methodTrapezoid(b, c, 10000);

        assertEquals(0, integral.methodRectangle(1, 1, 1));
        assertEquals(0, integral.methodTrapezoid(1, 1, 1));
        assertEquals(0, integral.methodSimpson(1, 1, 1));
        assertEquals(resultFirstIntegral, integral.methodRectangle(0, 5, 1000), epsilon);
        assertEquals(resultFirstIntegral, integral.methodTrapezoid(0, 5, 1000), 1e-4);
        assertEquals(resultFirstIntegral, integral.methodSimpson(0, 5, 1000), 1e-5);
        assertEquals(integral.methodSimpson(-5, 5, 1000) , 2 * integral.methodSimpson(0, 5, 1000), 1e-8);
        assertEquals(total, part1 + part2, 1e-7);
        assertEquals(resultSecondIntegral, integral.methodSimpson(0, 1000, 10001), epsilon);
        assertEquals(resultThirdIntegral, integral.methodRectangle(-1, -30, 10000), epsilon);
        assertEquals(resultThirdIntegral, integral.methodTrapezoid(-1, -30, 10000), epsilon);
        assertEquals(resultThirdIntegral, integral.methodSimpson(-1, -30, 1000), epsilon);
    }

    @Test
    void testCosSinSqrComposition() {

        MathFunction cos = new CosFunction();
        MathFunction sin = new SinFunction();
        MathFunction sqr = new SqrFunction();

        MathFunction composition = cos.andThen(sin).andThen(sqr);
        DefiniteIntegral integral = new DefiniteIntegral(composition);

        assertTrue(integral.methodSimpson(0, Math.PI/2, 2000) > 0);
        assertTrue(integral.methodSimpson(0, Math.PI/2, 2000) < Math.PI/2);
        assertEquals(integral.methodSimpson(0, Math.PI/2, 2000), integral.methodTrapezoid(0, Math.PI/2, 2000), 1e-7);

        assertTrue(integral.methodSimpson(2*Math.PI, Math.PI/4, 2000) > -2);
        assertTrue(integral.methodSimpson(2*Math.PI, Math.PI/4, 2000) < (2*Math.PI - Math.PI/4));
        assertEquals(integral.methodSimpson(2*Math.PI, Math.PI/4, 2000), integral.methodTrapezoid(2*Math.PI, Math.PI/4, 2000), 1e-6);

        assertTrue(integral.methodSimpson(-1, 2, 2000) > -3*3);
        assertTrue(integral.methodSimpson(-1, 2, 2000) < 3*3);
        assertEquals(integral.methodSimpson(-1, 2, 2000), integral.methodTrapezoid(-1, 2, 2000), 1e-7);
    }

    @Test
    void testLnCosComposition() {

        MathFunction cos = new CosFunction();
        MathFunction ln = new LnFunction();
        MathFunction composition = cos.andThen(ln);
        DefiniteIntegral integral = new DefiniteIntegral(composition);

        assertTrue(integral.methodSimpson(0, Math.PI/4, 2000) < 0);
        assertTrue(integral.methodSimpson(0, Math.PI/4, 2000) > -0.2);
        assertTrue(integral.methodSimpson(-Math.PI/2, Math.PI/2, 2000) < 0);
        assertTrue(integral.methodSimpson(-Math.PI/2, Math.PI/2, 2000) > -2.3);
    }

    @Test
    void testMethodTrapezoidWithNNegative(){
        SqrFunction f = new SqrFunction();
        DefiniteIntegral integral = new DefiniteIntegral(f);

        try {
            integral.methodTrapezoid(11, 12, -1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("n должно быть > 0", e.getMessage());  // Исправлено
        }
    }

    @Test
    void testMethodRectangleWithNNegative(){
        SqrFunction f = new SqrFunction();
        DefiniteIntegral integral = new DefiniteIntegral(f);

        try {
            integral.methodRectangle(11, 12, -1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("n должно быть > 0", e.getMessage());
        }
    }

    @Test
    void testMethodSimpsonWithNNegative(){
        SqrFunction f = new SqrFunction();
        DefiniteIntegral integral = new DefiniteIntegral(f);

        try {
            integral.methodSimpson(11, 12, -1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("n должно быть > 0", e.getMessage());  // Исправлено
        }
    }

    @Test
    void testMethodTrapezoidWithNEqualZero(){
        SqrFunction f = new SqrFunction();
        DefiniteIntegral integral = new DefiniteIntegral(f);

        try {
            integral.methodTrapezoid(11, 12, 0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("n должно быть > 0", e.getMessage());  // Исправлено
        }
    }

    @Test
    void testMethodRectangleWithNEqualZero(){  // Исправлено название
        SqrFunction f = new SqrFunction();
        DefiniteIntegral integral = new DefiniteIntegral(f);

        try {
            integral.methodRectangle(11, 12, 0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("n должно быть > 0", e.getMessage());  // Исправлено
        }
    }

    @Test
    void testMethodSimpsonWithNEqualZero(){  // Исправлено название
        SqrFunction f = new SqrFunction();
        DefiniteIntegral integral = new DefiniteIntegral(f);

        try {
            integral.methodSimpson(11, 12, 0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("n должно быть > 0", e.getMessage());  // Исправлено
        }
    }
}