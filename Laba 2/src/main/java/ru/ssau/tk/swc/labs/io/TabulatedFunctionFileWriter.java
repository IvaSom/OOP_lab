package ru.ssau.tk.swc.labs.io;

import ru.ssau.tk.swc.labs.functions.*;
import java.io.*;

public class TabulatedFunctionFileWriter {
    public static void main(String[] args) {
        try(BufferedWriter arrayWriter = new BufferedWriter(new FileWriter("output/array function.txt"));
            BufferedWriter listWriter = new BufferedWriter(new FileWriter("output/linked list function.txt"));) {
            TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{37, 42, 73}, new double[]{3, 22, 14});
            TabulatedFunction listFunction = new LinkedListTabulatedFunction(new CosFunction(), 0, 1, 45);

            FunctionsIO.writeTabulatedFunction(arrayWriter, arrayFunction);
            FunctionsIO.writeTabulatedFunction(listWriter, listFunction);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}

