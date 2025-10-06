package ru.ssau.tk.swc.labs.io;

import ru.ssau.tk.swc.labs.functions.*;
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
}