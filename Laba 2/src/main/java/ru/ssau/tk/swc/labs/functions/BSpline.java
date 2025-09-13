package ru.ssau.tk.swc.labs.functions;

public class BSpline implements MathFunction {
    //final чтобы сплайн не развалился
    private final double[] knots; //узлы
    private final int deg; //степень
    private final int index; // индекс базисной функции

    //конструктор
    public BSpline(double[] knots, int deg, int index){
        //проверки что такой сплайн можно построить
        if (knots == null || knots.length < deg + 1) {
            throw new IllegalArgumentException("Для B-сплайна степени k нужно как минимум k + 1 узел");
        }
        //количество функций = количество узлов - степень - 1   это будет максимальный возможный индекс
        if (index < 0 || index > knots.length - deg - 2) {//выход за пределы функций
            throw new IllegalArgumentException("Недопустимый индекс базисной функции");
        }
        this.knots = knots.clone(); //клонируем, чтобы не могли изменить массив из вне(это ссылка)
        this.deg = deg;
        this.index = index;
    }

    @Override
    public double apply(double x) { //возвращаем сплайн в точке x
        return deBoor(x, deg, index);
    }
    //алгоритм де Бура
    private double deBoor(double x, int k, int i){
        // сплайн степени 0 как ступенька, между узлами 1, вне этого интервала 0
        if (k == 0) {
            if (x >= knots[i] && x < knots[i + 1]) {
                return 1.0;
            } else {
                return 0.0;
            }
        }

        //сам алгоритм
        double t1 = 0.0;
        double t2 = 0.0;

        //первое слагаемое
        double den1 = knots[i + k] - knots[i];
        if (den1 != 0) {
            t1 = ((x - knots[i]) / den1) * deBoor(x, k - 1, i);
        }

        //второе слагаемое
        double den2 = knots[i + k + 1] - knots[i + 1];
        if (den2 != 0) {
            t2 = ((knots[i + k + 1] - x) / den2) * deBoor(x, k - 1, i + 1);
        }

        return t1 + t2;
    }
}
