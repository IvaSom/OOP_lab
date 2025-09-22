package ru.ssau.tk.swc.labs.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompositeTabulatedFunctionTest {

    @Test
    public void testArrayWithLinkedList() {
        // Создаем табулированную функцию через массив
        double[] xArr = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yArr = {0.0, 1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(xArr, yArr);

        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xArr, yArr);

        CompositeFunction comp = new CompositeFunction(arr, list);

        assertEquals(0.0, comp.apply(0.0));
        assertEquals(1.0, comp.apply(1.0));
        assertEquals(16.0, comp.apply(2.0));
        assertEquals(51.0, comp.apply(3.0));
        assertEquals(6.5, comp.apply(1.5));
        assertEquals(-52.0, comp.apply(-52.0));
    }


    @Test
    public void testArrayWithArray() {
        // Создаем табулированную функцию через массив
        double[] xArr = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yArr = {0.0, 1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(xArr, yArr);

        CompositeFunction comp = new CompositeFunction(arr, arr);

        assertEquals(0.0, comp.apply(0.0));
        assertEquals(1.0, comp.apply(1.0));
        assertEquals(16.0, comp.apply(2.0));
        assertEquals(51.0, comp.apply(3.0));
        assertEquals(6.5, comp.apply(1.5));
        assertEquals(-52.0, comp.apply(-52.0));
    }

    @Test
    public void testListWithList() {
        // Создаем табулированную функцию через массив
        double[] xArr = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yArr = {0.0, 1.0, 4.0, 9.0, 16.0};

        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xArr, yArr);

        CompositeFunction comp = new CompositeFunction(list, list);

        assertEquals(0.0, comp.apply(0.0));
        assertEquals(1.0, comp.apply(1.0));
        assertEquals(16.0, comp.apply(2.0));
        assertEquals(51.0, comp.apply(3.0));
        assertEquals(6.5, comp.apply(1.5));
        assertEquals(-52.0, comp.apply(-52.0));
    }

    @Test
    public void testArrayWithFun() {
        // Создаем табулированную функцию через массив
        double[] xArr = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yArr = {0.0, 1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(xArr, yArr);
        SqrFunction fun =new SqrFunction();
        CompositeFunction comp = new CompositeFunction(arr, fun);

        assertEquals(0.0, comp.apply(0.0));
        assertEquals(1.0, comp.apply(1.0));
        assertEquals(16.0, comp.apply(2.0));
        assertEquals(81.0, comp.apply(3.0));
        assertEquals(6.25, comp.apply(1.5));
        assertEquals(2704, comp.apply(-52.0));
    }

    @Test
    public void testListWithFun() {
        // Создаем табулированную функцию через массив
        double[] xArr = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yArr = {0.0, 1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xArr, yArr);

        SqrFunction fun =new SqrFunction();
        CompositeFunction comp = new CompositeFunction(list, fun);

        assertEquals(0.0, comp.apply(0.0));
        assertEquals(1.0, comp.apply(1.0));
        assertEquals(16.0, comp.apply(2.0));
        assertEquals(81.0, comp.apply(3.0));
        assertEquals(6.25, comp.apply(1.5));
        assertEquals(2704, comp.apply(-52.0));
    }
}