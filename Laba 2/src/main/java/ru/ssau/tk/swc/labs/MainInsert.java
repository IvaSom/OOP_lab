package ru.ssau.tk.swc.labs;

import ru.ssau.tk.swc.labs.dao.*;
import ru.ssau.tk.swc.labs.entity.*;
import ru.ssau.tk.swc.labs.util.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainInsert {
    private static final Logger logger = LoggerFactory.getLogger(MainInsert.class);

    public static void main(String[] args) {
        logger.info("Начало генерации тестовых данных...");

        // 1. Создаем провайдер подключения
        DataSourceProvider dataSourceProvider = new PostgreSQLDataSourceProvider();

        // 2. Создаем DAO объекты
        AnalFunDAO analFunDAO = new AnalFunDAO(dataSourceProvider);
        TabFunDAO tabFunDAO = new TabFunDAO(dataSourceProvider);
        CompFunDAO compFunDAO = new CompFunDAO(dataSourceProvider);
        AnalPointDAO analPointDAO = new AnalPointDAO(dataSourceProvider);
        TabPointDAO tabPointDAO = new TabPointDAO(dataSourceProvider);
        CompPointDAO compPointDAO = new CompPointDAO(dataSourceProvider);
        UserDAO userDAO = new UserDAO(dataSourceProvider);
        CompositeStructureDAO compositeStructureDAO = new CompositeStructureDAO(dataSourceProvider);

        try {
            // 3. Создаем несколько функций для связей
            logger.info("Создание базовых функций...");

            List<Long> analFunIds = new ArrayList<>();
            List<Long> tabFunIds = new ArrayList<>();
            List<Long> compFunIds = new ArrayList<>();

            // Создаем 10 аналитических функций
            for (int i = 0; i < 10; i++) {
                AnalFun fun = DataGenerator.generateAnalFun();
                Long id = analFunDAO.create(fun);
                if (id != null) analFunIds.add(id);
            }

            // Создаем 5 табулированных функций
            for (int i = 0; i < 5; i++) {
                TabFun fun = DataGenerator.generateTabFun();
                Long id = tabFunDAO.create(fun);
                if (id != null) tabFunIds.add(id);
            }

            // Создаем 3 композитные функции
            for (int i = 0; i < 3; i++) {
                CompFun fun = DataGenerator.generateCompFun();
                Long id = compFunDAO.create(fun);
                if (id != null) compFunIds.add(id);
            }

            logger.info("Создано функций: AnalFun={}, TabFun={}, CompFun={}",
                    analFunIds.size(), tabFunIds.size(), compFunIds.size());

            // 4. Генерируем 1000 записей для разных таблиц
            int totalRecords = 0;

            // Пользователи - 200 записей
            logger.info("Генерация пользователей...");
            for (int i = 0; i < 200; i++) {
                User user = DataGenerator.generateUser();
                userDAO.create(user);
                totalRecords++;
                if (i % 50 == 0) logger.info("Создано {} пользователей", i);
            }

            // Точки аналитических функций - 300 записей
            logger.info("Генерация точек аналитических функций...");
            for (int i = 0; i < 300; i++) {
                if (analFunIds.isEmpty()) break;
                Long funId = analFunIds.get(randomIndex(analFunIds.size()));
                AnalPoint point = DataGenerator.generateAnalPoint(funId);
                analPointDAO.create(point);
                totalRecords++;
                if (i % 100 == 0) logger.info("Создано {} аналитических точек", i);
            }

            // Точки табулированных функций - 300 записей
            logger.info("Генерация точек табулированных функций...");
            for (int i = 0; i < 300; i++) {
                if (tabFunIds.isEmpty()) break;
                Long funId = tabFunIds.get(randomIndex(tabFunIds.size()));
                TabPoint point = DataGenerator.generateTabPoint(funId);
                tabPointDAO.create(point);
                totalRecords++;
                if (i % 100 == 0) logger.info("Создано {} табулированных точек", i);
            }

            // Точки композитных функций - 150 записей
            logger.info("Генерация точек композитных функций...");
            for (int i = 0; i < 150; i++) {
                if (compFunIds.isEmpty()) break;
                Long funId = compFunIds.get(randomIndex(compFunIds.size()));
                CompPoint point = DataGenerator.generateCompPoint(funId);
                compPointDAO.create(point);
                totalRecords++;
                if (i % 50 == 0) logger.info("Создано {} композитных точек", i);
            }

            // Структуры композитных функций - 50 записей
            logger.info("Генерация структур композитных функций...");
            for (int i = 0; i < 50; i++) {
                if (compFunIds.isEmpty() || analFunIds.isEmpty()) break;
                Long compId = compFunIds.get(randomIndex(compFunIds.size()));
                Long analId = analFunIds.get(randomIndex(analFunIds.size()));
                CompositeStructure structure = DataGenerator.generateCompositeStructure(compId, analId);
                compositeStructureDAO.create(structure);
                totalRecords++;
            }

            logger.info("Генерация завершена! Всего создано {} записей.", totalRecords);
            logger.info("Распределение по таблицам:");
            logger.info("- Пользователи: 200");
            logger.info("- Аналитические точки: 300");
            logger.info("- Табулированные точки: 300");
            logger.info("- Композитные точки: 150");
            logger.info("- Структуры композитных функций: 50");

        } catch (Exception e) {
            logger.error("Ошибка при генерации данных", e);
        }
    }

    private static int randomIndex(int size) {
        return new java.util.Random().nextInt(size);
    }
}