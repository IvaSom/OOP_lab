package ru.ssau.tk.swc.labs.concurrent;

import ru.ssau.tk.swc.labs.functions.*;

public class ReadWriteTaskExecutor {
    public static void main(String[] args) {
        TabulatedFunction function = new LinkedListTabulatedFunction(new ConstantFunction(-1), 1, 1000, 1000);

        Thread readThread = new Thread(new ReadTask(function));
        Thread writeThread = new Thread(new WriteTask(function, 0.5));

        readThread.start();
        writeThread.start();

        try {
            readThread.join();
            writeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
