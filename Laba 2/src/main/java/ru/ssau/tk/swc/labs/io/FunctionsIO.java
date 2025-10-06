package ru.ssau.tk.swc.labs.io;

import ru.ssau.tk.swc.labs.functions.*;
import ru.ssau.tk.swc.labs.functions.factory.TabulatedFunctionFactory;
import java.text.*;
import java.util.Locale;
import java.io.*;

final public class FunctionsIO {
    private FunctionsIO(){
        throw new UnsupportedOperationException();
    }


    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function){
        PrintWriter printWriter = new PrintWriter(writer);
        printWriter.println(function.getCount());

        for (Point p : function)
            printWriter.printf("%f %f\n", p.x, p.y);

        printWriter.flush();
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(function.getCount());

        for (Point p : function) {
            dataOutputStream.writeDouble(p.x);
            dataOutputStream.writeDouble(p.y);
        }

        dataOutputStream.flush();
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException{
        try{
            String line = reader.readLine();
            int count = Integer.parseInt(line);
            double[] xValues = new double[count];
            double[] yValues = new double[count];
            NumberFormat numberFormatter = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < count; i++) {
                line = reader.readLine();
                String[] parts = line.split(" ");
                try {
                    xValues[i] = numberFormatter.parse(parts[0]).doubleValue();
                    yValues[i] = numberFormatter.parse(parts[1]).doubleValue();
                } catch (ParseException e){
                    throw new IOException();
                }
            }

            return factory.create(xValues, yValues);
            } catch (IOException e) {
            throw e;
        }
    }
}