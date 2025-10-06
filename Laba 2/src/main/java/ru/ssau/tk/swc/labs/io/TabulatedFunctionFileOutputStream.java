package ru.ssau.tk.swc.labs.io;

import ru.ssau.tk.swc.labs.functions.*;
import java.io.*;

public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args) {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        TabulatedFunction array = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction list = new LinkedListTabulatedFunction(xValues, yValues);


        try (FileOutputStream fileStream1 = new FileOutputStream("output/array function.bin");
             BufferedOutputStream bufferedStream1 = new BufferedOutputStream(fileStream1);
             FileOutputStream fileStream2 = new FileOutputStream("output/linked list function.bin");
             BufferedOutputStream bufferedStream2 = new BufferedOutputStream(fileStream2)) {

            FunctionsIO.writeTabulatedFunction(bufferedStream1, array);
            FunctionsIO.writeTabulatedFunction(bufferedStream2, list);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
