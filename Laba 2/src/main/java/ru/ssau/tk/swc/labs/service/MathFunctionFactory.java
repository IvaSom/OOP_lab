package ru.ssau.tk.swc.labs.service;

import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.functions.*;

@Service
public class MathFunctionFactory {

    public MathFunction createMathFunction(analFun analyticFunction) {
        return switch (analyticFunction.getType()) {
            case 1 -> new SinFunction();          // sin
            case 2 -> new CosFunction();          // cos
            case 3 -> new SqrFunction();          // sqr
            case 4 -> new ConstantFunction(1.0);  // constant (используем 1.0 как значение по умолчанию)
            case 5 -> new ZeroFunction();         // zero
            case 6 -> new LnFunction();           // ln
            case 7 -> new IdentityFunction();     // identity
            case 8 -> new UnitFunction();         // unit
            default -> throw new IllegalArgumentException("Unknown analytic function type: " + analyticFunction.getType());
        };
    }

    public MathFunction createMathFunctionByType(Integer type) {
        return switch (type) {
            case 1 -> new SinFunction();
            case 2 -> new CosFunction();
            case 3 -> new SqrFunction();
            case 4 -> new ConstantFunction(1.0);
            case 5 -> new ZeroFunction();
            case 6 -> new LnFunction();
            case 7 -> new IdentityFunction();
            case 8 -> new UnitFunction();
            default -> throw new IllegalArgumentException("Unknown analytic function type: " + type);
        };
    }
}