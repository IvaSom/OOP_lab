package ru.ssau.tk.swc.labs.operations;

import ru.ssau.tk.swc.labs.functions.*;

abstract public class SteppingDifferentialOperator implements DifferentialOperator <MathFunction>{
    private double step;

    public SteppingDifferentialOperator (double st){
        if (st <= 0 || Double.isNaN(st) || Double.isInfinite(st))
            throw new IllegalArgumentException();
        this.step = st;
    }

    public double getStep(){
        return this.step;
    }

    public void setStep(double st){
        this.step = st;
    }
}
