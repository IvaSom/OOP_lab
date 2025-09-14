package ru.ssau.tk.swc.labs.functions;

public interface TabulatedFunction extends MathFunction {
    int getCount(); //Метод получения количества табулированных значений
    double getX(int index); //Метод, получающий значение аргумента x по номеру индекса
    double getY(int index);//Метод, получающий значение y по номеру индекса
    void setY(int index, double value); //Метод, задающий значение y по номеру индекса

    int indexOfX(double x); //Метод, возвращающий индекс аргумента x, если нет то -1
    int indexOfY(double y); //для y

    double leftBound(); //самый левый x
    double rightBound(); //самый правый x

}
