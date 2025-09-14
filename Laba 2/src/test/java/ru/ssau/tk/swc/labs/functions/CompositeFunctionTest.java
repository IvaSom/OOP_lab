package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {
    @Test
    void testCompositeSqr() {
        //квадрат тождественной функции
        MathFunction identity = new IdentityFunction();
        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(identity, sqr);

        assertEquals(0.0, composite.apply(0.0));
        assertEquals(1.0, composite.apply(1.0));
        assertEquals(4.0, composite.apply(2.0));
        assertEquals(9.0, composite.apply(3.0));
        assertEquals(16.0, composite.apply(4.0));
    }
    @Test
    void testCompositeSqrSqr() {
        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(sqr, sqr);

        assertEquals(0.0, composite.apply(0.0));
        assertEquals(1.0, composite.apply(1.0));
        assertEquals(16.0, composite.apply(2.0));
        assertEquals(81.0, composite.apply(3.0));
        assertEquals(256.0, composite.apply(4.0));
    }
    @Test
    void testCompositeOfComposite() {
        MathFunction sqr = new SqrFunction();
        MathFunction identity = new IdentityFunction();

        CompositeFunction composite1 = new CompositeFunction(sqr, identity);
        CompositeFunction composite2 = new CompositeFunction(composite1, sqr);

        assertEquals(0.0, composite2.apply(0.0));
        assertEquals(1.0, composite2.apply(1.0));
        assertEquals(16.0, composite2.apply(2.0));
        assertEquals(81.0, composite2.apply(3.0));
    }
    @Test
    void testAndThen() {
        MathFunction identity = new IdentityFunction();
        MathFunction sqr = new SqrFunction();

        //метод andThen из интерфейса
        MathFunction composite = identity.andThen(sqr).andThen(sqr);

        assertEquals(0.0, composite.apply(0.0));
        assertEquals(1.0, composite.apply(1.0));
        assertEquals(16.0, composite.apply(2.0));
        assertEquals(81.0, composite.apply(3.0));
        assertEquals(256.0, composite.apply(4.0));
    }

}