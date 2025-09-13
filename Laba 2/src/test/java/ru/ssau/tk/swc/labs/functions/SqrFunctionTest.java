package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SqrFunctionTest {

    @Test
    void testApply() {
        SqrFunction function = new SqrFunction();
        // Тест с положительными числами
        assertEquals(4.0, function.apply(2.0));
        assertEquals(9.0, function.apply(3.0));
        assertEquals(25.0, function.apply(5.0));

        // Тест с отрицательными числами
        assertEquals(4.0, function.apply(-2.0));
        assertEquals(9.0, function.apply(-3.0));

        // Тест с нулем
        assertEquals(0.0, function.apply(0.0));

        // Тест с дробными числами
        assertEquals(0.25, function.apply(0.5));
        assertEquals(2.25, function.apply(1.5));
    }

    @Test
    void testApplyWithPrecision() {
        SqrFunction function = new SqrFunction();

        // Тест с плавающей точкой и допустимой погрешностью
        assertEquals(2.25, function.apply(1.5), 1e-10);
        assertEquals(0.01, function.apply(0.1), 1e-10);
    }

    @Test
    void testApplyPrecisionAndRounding() {
        SqrFunction function = new SqrFunction();

        // Числа, близкие к 1.0 (проверка точности)
        assertEquals(1.0000000000000002, function.apply(1.0000000000000001), 1e-15);
        assertEquals(0.9999999999999998, function.apply(0.9999999999999999), 1e-15);

        // Большие числа с плавающей точкой
        double largeNumber = 1.23456789e8;
        assertEquals(largeNumber * largeNumber, function.apply(largeNumber), largeNumber * 1e-7);

        // Очень маленькие числа
        double smallNumber = 1.23456789e-8;
        assertEquals(smallNumber * smallNumber, function.apply(smallNumber), 1e-20);
    }

    @Test
    void testApplySpecialMathematicalCases() {
        SqrFunction function = new SqrFunction();

        // Золотое сечение
        double phi = (1 + Math.sqrt(5)) / 2;
        assertEquals(phi + 1, function.apply(phi), 1e-10); // φ² = φ + 1

        // Тригонометрические тождества
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 12) {
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            assertEquals(1.0, function.apply(sin) + function.apply(cos), 1e-10,
                    "sin²(x) + cos²(x) должно быть равно 1 для x = " + angle);
        }
    }
}