package ru.ssau.tk.swc.labs.concurrent;

import ru.ssau.tk.swc.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.swc.labs.functions.UnitFunction;
import java.util.*;

public class MultiplyingTaskExecutor {
    public static void main(String[] args) {
        UnitFunction tempFun = new UnitFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(tempFun, 1, 1000, 10000);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);

            threads.add(thread);
            thread.start();
        }

        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("Результат после 10 потоков:");
        for (int i = 0; i < function.getCount(); i++) {
            System.out.printf("x = %f, y = %f%n", function.getX(i), function.getY(i));
        }
    }
}
