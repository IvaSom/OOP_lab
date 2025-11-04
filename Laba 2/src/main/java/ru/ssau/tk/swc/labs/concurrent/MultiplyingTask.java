package ru.ssau.tk.swc.labs.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.swc.labs.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(MultiplyingTask.class);
    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++)
            synchronized (function) {
                function.setY(i, function.getY(i) * 2);
                logger.debug("Точка {}:  = {}", i, function.getY(i));
            }

        logger.info("Поток " + Thread.currentThread().getName() + " Завершил работу");
        System.out.println("Поток " + Thread.currentThread().getName() + " Завершил работу");
    }
}
