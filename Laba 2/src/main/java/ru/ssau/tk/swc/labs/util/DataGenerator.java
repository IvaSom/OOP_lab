package ru.ssau.tk.swc.labs.util;

import ru.ssau.tk.swc.labs.dao.*;
import ru.ssau.tk.swc.labs.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

public class DataGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DataGenerator.class);
    private static final Random random = new Random();

    // Генерация аналитической функции
    public static AnalFun generateAnalFun() {
        AnalFun fun = new AnalFun();
        fun.setName("AnalFun_" + System.currentTimeMillis() + "_" + random.nextInt(1000));
        fun.setType(random.nextInt(10) + 1); // Тип от 1 до 10
        return fun;
    }

    // Генерация табулированной функции
    public static TabFun generateTabFun() {
        TabFun fun = new TabFun();
        String[] types = {"linear", "quadratic", "cubic", "exponential", "logarithmic"};
        fun.setType(types[random.nextInt(types.length)]);
        return fun;
    }

    // Генерация композитной функции
    public static CompFun generateCompFun() {
        CompFun fun = new CompFun();
        fun.setName("CompFun_" + System.currentTimeMillis() + "_" + random.nextInt(1000));
        return fun;
    }

    // Генерация точки аналитической функции
    public static AnalPoint generateAnalPoint(Long funID) {
        AnalPoint point = new AnalPoint();
        point.setX(random.nextDouble() * 100 - 50); // x от -50 до 50
        point.setY(random.nextDouble() * 100 - 50); // y от -50 до 50
        point.setFunID(funID);
        return point;
    }

    // Генерация точки табулированной функции
    public static TabPoint generateTabPoint(Long funID) {
        TabPoint point = new TabPoint();
        point.setX(random.nextDouble() * 100 - 50);
        point.setY(random.nextDouble() * 100 - 50);
        point.setDerive(random.nextDouble() * 10 - 5); // производная от -5 до 5
        point.setFunID(funID);
        return point;
    }

    // Генерация точки композитной функции
    public static CompPoint generateCompPoint(Long funID) {
        CompPoint point = new CompPoint();
        point.setX(random.nextDouble() * 100 - 50);
        point.setY(random.nextDouble() * 100 - 50);
        point.setFunID(funID);
        return point;
    }

    // Генерация пользователя
    public static User generateUser() {
        User user = new User();
        String base = "user_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
        user.setName("User " + base);
        user.setLogin(base);
        user.setEmail(base + "@example.com");
        user.setPassword("password_" + random.nextInt(10000));
        return user;
    }

    // Генерация структуры композитной функции
    public static CompositeStructure generateCompositeStructure(Long compositeId, Long analyticId) {
        CompositeStructure structure = new CompositeStructure();
        structure.setComposite_id(compositeId);
        structure.setAnalytic_id(analyticId);
        structure.setOrder(random.nextInt(5) + 1); // порядок от 1 до 5
        return structure;
    }
}