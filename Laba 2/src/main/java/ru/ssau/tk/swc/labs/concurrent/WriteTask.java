package ru.ssau.tk.swc.labs.concurrent;


import ru.ssau.tk.swc.labs.functions.*;
import  java.lang.Runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WriteTask.class);

    private TabulatedFunction function;
    private double value;

    public WriteTask(TabulatedFunction function, double value) {
        this.function = function;
        this.value = value;
        logger.info("Создан WriteTask с значением {}", value);
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, value);
                logger.debug("Запись завершена для индекса {}", i);
            }
        }
        logger.info("WriteTask с значением {} завершена", value);
    }
}
