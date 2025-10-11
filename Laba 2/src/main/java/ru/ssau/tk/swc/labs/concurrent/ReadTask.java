package ru.ssau.tk.swc.labs.concurrent;

import ru.ssau.tk.swc.labs.functions.*;

import  java.lang.Runnable;

public class ReadTask implements Runnable{
    private TabulatedFunction function;
    public ReadTask(TabulatedFunction function){
        this.function=function;
    }
    @Override
    public void run(){
        for (int i=0; i<function.getCount(); i++){
            synchronized (function) { //если функция чем-то занята то ждет пока освободится
                System.out.printf("After read: i = %d, x = %f, y = %f\n",
                        i, function.getX(i), function.getY(i));
            }
            //%d для int, %f для float и double, %s для строк, можно %n вместо \n это одинаково
        }
    }
}
