package ru.ssau.tk.swc.labs.io;
import ru.ssau.tk.swc.labs.functions.*;
import ru.ssau.tk.swc.labs.functions.factory.*;
import java.io.*;

public class TabulatedFunctionFileReader {
    public static void main(String[] args) {
        String path = "input/function.txt";
        try(BufferedReader arrayReader = new BufferedReader(new FileReader("input/function.txt"));
            BufferedReader listReader  = new BufferedReader(new FileReader("input/function.txt"))){

            TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
            TabulatedFunctionFactory listFactory = new LinkedListTabulatedFunctionFactory();

            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(arrayReader, arrayFactory);
            TabulatedFunction listFunction = FunctionsIO.readTabulatedFunction(listReader, listFactory);

            System.out.println(arrayFunction.toString());
            System.out.println(listFunction.toString());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
