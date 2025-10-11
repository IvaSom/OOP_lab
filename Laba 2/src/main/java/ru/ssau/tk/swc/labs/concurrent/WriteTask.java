package ru.ssau.tk.swc.labs.concurrent;


import ru.ssau.tk.swc.labs.functions.*;
import  java.lang.Runnable;

public class WriteTask implements Runnable{
    private TabulatedFunction function;
    double value;
    public WriteTask(TabulatedFunction function, double value){
        this.function=function;
        this.value=value;
    }
    @Override
    public void run(){
        for (int i=0; i<function.getCount(); i++){
            function.setY(i, value);
            System.out.printf("Writing for index %d complete\n", i);
        }
    }
}
