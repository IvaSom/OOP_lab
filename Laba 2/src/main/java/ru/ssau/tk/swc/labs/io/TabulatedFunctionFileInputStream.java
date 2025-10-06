package ru.ssau.tk.swc.labs.io;

import ru.ssau.tk.swc.labs.functions.*;
import ru.ssau.tk.swc.labs.functions.factory.*;
import java.io.*;
import ru.ssau.tk.swc.labs.operations.*;


public class TabulatedFunctionFileInputStream {
    public static void main(String[] args) {

        try (FileInputStream fileStream = new FileInputStream("input/binary function.bin");
             BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)) {

            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedStream, new ArrayTabulatedFunctionFactory());

            System.out.println(function.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Введите размер и значения функции");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            TabulatedFunction consoleFunction = FunctionsIO.readTabulatedFunction(reader, new LinkedListTabulatedFunctionFactory());

            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
            TabulatedFunction fun = operator.derive(consoleFunction);

            System.out.println("Производная функции:");
            System.out.println(fun.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
