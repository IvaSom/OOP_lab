package ru.ssau.tk.swc.labs.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.swc.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.swc.labs.functions.UnitFunction;
import java.util.*;

public class MultiplyingTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(MultiplyingTaskExecutor.class);

    public static void main(String[] args) {
        logger.info("Запуск main()");

        UnitFunction tempFun = new UnitFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(tempFun, 1, 1000, 10000);
        List<Thread> threads = new ArrayList<>();

        logger.info("Создание и запуск 10 потоков MultiplyingTask");
        for (int i = 0; i < 10; i++) {
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);

            threads.add(thread);
            logger.debug("Запускается поток №{}", i + 1);
            thread.start();
        }

        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            logger.error("Поток main был прерван", e);
            e.printStackTrace();
        }

        logger.info("Вывод результата после работы 10 потоков, общее количество точек: {}", function.getCount());
        System.out.println("Результат после 10 потоков:");
        for (int i = 0; i < function.getCount(); i++) {
            System.out.printf("x = %f, y = %f%n", function.getX(i), function.getY(i));
        }
        logger.info("main() завершён");
    }
}
