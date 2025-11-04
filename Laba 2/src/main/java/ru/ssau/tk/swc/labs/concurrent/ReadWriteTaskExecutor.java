package ru.ssau.tk.swc.labs.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk.swc.labs.functions.*;

public class ReadWriteTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ReadWriteTaskExecutor.class);
    public static void main(String[] args) {
        logger.info("Запуск потоков на чтение и запись");
        TabulatedFunction function = new LinkedListTabulatedFunction(new ConstantFunction(-1), 1, 1000, 1000);

        Thread readThread = new Thread(new ReadTask(function));
        Thread writeThread = new Thread(new WriteTask(function, 0.5));

        readThread.start();
        writeThread.start();

        try {
            readThread.join();
            writeThread.join();
        } catch (InterruptedException e) {
            logger.error("Поток main был прерван", e);
            e.printStackTrace();
        }

    }
}
