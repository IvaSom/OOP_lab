package ru.ssau.tk.swc.labs.integral;

public class DefiniteIntegral {
    private MathFunction function;

    public DefiniteIntegral (MathFunction f) {this.function = f;}

    public MathFunction getFunction() {return function;}

    public void setFunction(MathFunction f) {this.function = f;}

    public double methodTrapezoid (double a, double b, int n) {
        if (a == b) return 0;
        if (n <= 0) throw new IllegalArgumentException("n должно быть <= 0, но было: " + n);

        double h = (b - a) / n;
        double sum = (function.apply(a) + function.apply(b)) / 2;

        for (int i = 1; i < n; i++)
            sum += function.apply(a + i * h);

        return sum * h;
    }

    public double methodRectangle (double a, double b, int n) {
        if (a == b) return 0;
        if (n <= 0) throw new IllegalArgumentException("n должно быть <= 0, но было: " + n);

        double h = (b - a) / n;
        double sum = 0;
        for (int i = 0; i < n; i++)
            sum += function.apply(a + i * h + h/2);

        return sum * h;
    }

    public double methodSimpson (double a, double b, int n) {
        if (a == b) return 0;
        if (n <= 0) throw new IllegalArgumentException("n должно быть <= 0, но было: " + n);
        if (n % 2 == 1) n++;

        double h = (b - a) / n;
        double sum = function.apply(a) + function.apply(b);
        for (int i = 1; i < n; i++) {
            double coeff = (i % 2 == 0) ? 2 : 4;
            sum += coeff * function.apply(a + i * h);
        }
        return sum * h / 3;
    }

}