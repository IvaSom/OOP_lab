package ru.ssau.tk.swc.labs;


import ru.ssau.tk.swc.labs.functions.MathFunction;
import ru.ssau.tk.swc.labs.operations.LeftSteppingDifferentialOperator;

public class Main {
    public static void main(String[] args) {
        LeftSteppingDifferentialOperator lsdo = new LeftSteppingDifferentialOperator(20);
        MathFunction difunc1 = lsdo.derive(new MathFunction() {
            @Override
            public double apply(double x) {
                return Math.sin(x);
            }
        });

        MathFunction difunc2 = lsdo.derive(new MathFunction() {
            @Override
            public double apply(double x) {
                return Math.pow(x, 2);
            }
        });

        System.out.println(difunc1.apply(10));
        System.out.println(difunc2.apply(10));
    }
}