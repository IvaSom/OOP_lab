package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.TabPoint;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TabPointDAOTest {
    private TabPointDAO dao;
    private String dbName;

    // Для тестов нам понадобится создать tabFun записи, чтобы ссылаться на них
    private Long testFunId1;
    private Long testFunId2;

    @BeforeEach
    public void setup() throws Exception {
        // Генерируем уникальное имя БД для каждого теста
        dbName = "testdb_" + System.currentTimeMillis() + "_" + Math.random();

        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "");
             Statement stmt = setupConn.createStatement()) {

            // Сначала создаем таблицу tabFun
            stmt.execute("DROP TABLE IF EXISTS tab_points");
            stmt.execute("DROP TABLE IF EXISTS tabFun");

            stmt.execute("""
                        CREATE TABLE tabFun (
                            id BIGSERIAL PRIMARY KEY,
                            type VARCHAR(50) NOT NULL
                        );
                    """);

            // Затем создаем таблицу tab_points с внешним ключом
            stmt.execute("""
                        CREATE TABLE tab_points (
                            id BIGSERIAL PRIMARY KEY,
                            x DOUBLE PRECISION NOT NULL,
                            y DOUBLE PRECISION NOT NULL,
                            derive DOUBLE PRECISION NOT NULL,
                            funID BIGINT NOT NULL,
                            CONSTRAINT funID_fk FOREIGN KEY (funID) REFERENCES tabFun (id)
                        );
                    """);

            // Создаем тестовые записи в tabFun для ссылок
            stmt.executeUpdate("INSERT INTO tabFun (type) VALUES ('Функция 1')");
            stmt.executeUpdate("INSERT INTO tabFun (type) VALUES ('Функция 2')");

            try (ResultSet rs = stmt.executeQuery("SELECT id FROM tabFun ORDER BY id")) {
                if (rs.next()) testFunId1 = rs.getLong("id");
                if (rs.next()) testFunId2 = rs.getLong("id");
            }
        }

        dao = new TabPointDAO(new TestDataSourceProvider());
    }

    private class TestDataSourceProvider implements DataSourceProvider {
        @Override
        public Connection getConnection() throws SQLException {
            Connection conn = DriverManager.getConnection(
                    "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                    "sa",
                    ""
            );
            conn.setAutoCommit(true);
            return conn;
        }
    }

    @Test
    public void testBasicCRUD() {
        System.out.println("=== Тест Basic CRUD для TabPoint ===");

        // CREATE
        TabPoint point = new TabPoint();
        point.setX(1.5);
        point.setY(2.3);
        point.setDerive(0.8);
        point.setFunID(testFunId1);

        Long id = dao.create(point);
        System.out.println("Создана точка с ID: " + id);
        assertNotNull(id, "ID не должен быть null");

        // READ by ID
        Optional<TabPoint> found = dao.findByID(id);
        System.out.println("Найдена по ID: " + found.isPresent());
        assertTrue(found.isPresent(), "Точка должна быть найдена по ID");
        assertEquals(1.5, found.get().getX(), 0.001);
        assertEquals(2.3, found.get().getY(), 0.001);
        assertEquals(0.8, found.get().getDerive(), 0.001);
        assertEquals(testFunId1, found.get().getFunID());

        // READ by X and FunID
        Optional<TabPoint> foundByXAndFunID = dao.findByXAndFunID(1.5, testFunId1);
        System.out.println("Найдена по X и FunID: " + foundByXAndFunID.isPresent());
        assertTrue(foundByXAndFunID.isPresent(), "Точка должна быть найдена по X и FunID");

        // DELETE
        boolean deleted = dao.delete(id);
        System.out.println("Удалена: " + deleted);
        assertTrue(deleted, "Точка должна быть удалена");

        Optional<TabPoint> notFound = dao.findByID(id);
        System.out.println("После удаления: " + notFound.isPresent());
        assertFalse(notFound.isPresent(), "Точка не должна быть найдена после удаления");
    }

    @Test
    public void testFindAll() {
        System.out.println("\n=== Тест FindAll для TabPoint ===");

        for (int i = 1; i <= 3; i++) {
            TabPoint point = new TabPoint();
            point.setX(i * 1.0);
            point.setY(i * 2.0);
            point.setDerive(i * 0.5);
            point.setFunID(testFunId1);

            Long id = dao.create(point);
            assertNotNull(id, "ID не должен быть null для точки " + i);
            System.out.println("Создана точка " + i + " с ID: " + id);
        }

        List<TabPoint> all = dao.findAll();
        System.out.println("Всего точек: " + all.size());
        assertEquals(3, all.size(), "Должно быть 3 точки");
    }

    @Test
    public void testCreateWithNegativeValues() {
        System.out.println("\n=== Тест создания с отрицательными значениями для TabPoint ===");

        TabPoint point = new TabPoint();
        point.setX(-1.5);
        point.setY(-2.3);
        point.setDerive(-0.8);
        point.setFunID(testFunId1);

        Long id = dao.create(point);
        System.out.println("Создана точка с отрицательными значениями, ID: " + id);

        if (id != null) {
            Optional<TabPoint> found = dao.findByID(id);
            assertTrue(found.isPresent(), "Точка должна быть найдена");
            assertEquals(-1.5, found.get().getX(), 0.001);
            assertEquals(-2.3, found.get().getY(), 0.001);
            assertEquals(-0.8, found.get().getDerive(), 0.001);
        }
    }

    @Test
    public void testCreateWithZeroValues() {
        System.out.println("\n=== Тест создания с нулевыми значениями для TabPoint ===");

        TabPoint point = new TabPoint();
        point.setX(0.0);
        point.setY(0.0);
        point.setDerive(0.0);
        point.setFunID(testFunId1);

        Long id = dao.create(point);
        System.out.println("Создана точка с нулевыми значениями, ID: " + id);

        if (id != null) {
            Optional<TabPoint> found = dao.findByID(id);
            assertTrue(found.isPresent(), "Точка должна быть найдена");
            assertEquals(0.0, found.get().getX(), 0.001);
            assertEquals(0.0, found.get().getY(), 0.001);
            assertEquals(0.0, found.get().getDerive(), 0.001);
        }
    }

    @Test
    public void testMultiplePointsSameFunID() {
        System.out.println("\n=== Тест нескольких точек с одинаковым funID для TabPoint ===");

        for (int i = 0; i < 4; i++) {
            TabPoint point = new TabPoint();
            point.setX(i * 2.0);
            point.setY(i * 3.0);
            point.setDerive(i * 0.5);
            point.setFunID(testFunId1);

            Long id = dao.create(point);
            assertNotNull(id, "Точка " + i + " должна быть создана");
            System.out.println("Создана точка " + i + " с ID: " + id);
        }

        List<TabPoint> all = dao.findAll();
        assertEquals(4, all.size(), "Должно быть 4 точки");

        // Все точки должны иметь одинаковый funID
        for (TabPoint point : all) {
            assertEquals(testFunId1, point.getFunID(), "Все точки должны ссылаться на testFunId1");
        }
    }

    @Test
    public void testPointsWithDifferentFunIDs() {
        System.out.println("\n=== Тест точек с разными funID для TabPoint ===");

        // Создаем точки с разными funID
        TabPoint point1 = new TabPoint();
        point1.setX(1.0);
        point1.setY(2.0);
        point1.setDerive(0.5);
        point1.setFunID(testFunId1);

        TabPoint point2 = new TabPoint();
        point2.setX(3.0);
        point2.setY(4.0);
        point2.setDerive(1.0);
        point2.setFunID(testFunId2);

        Long id1 = dao.create(point1);
        Long id2 = dao.create(point2);

        assertNotNull(id1, "Первая точка должна быть создана");
        assertNotNull(id2, "Вторая точка должна быть создана");

        System.out.println("Создана точка 1 с ID: " + id1 + " и funID: " + testFunId1);
        System.out.println("Создана точка 2 с ID: " + id2 + " и funID: " + testFunId2);

        // Проверяем, что точки имеют разные funID
        Optional<TabPoint> found1 = dao.findByID(id1);
        Optional<TabPoint> found2 = dao.findByID(id2);

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertNotEquals(found1.get().getFunID(), found2.get().getFunID(), "Точки должны иметь разные funID");
    }

    @Test
    public void testFindByXAndFunID() {
        System.out.println("\n=== Тест поиска по X и FunID для TabPoint ===");

        // Создаем несколько точек с одинаковым X, но разными funID
        TabPoint point1 = new TabPoint();
        point1.setX(5.0);
        point1.setY(10.0);
        point1.setDerive(2.0);
        point1.setFunID(testFunId1);

        TabPoint point2 = new TabPoint();
        point2.setX(5.0); // Тот же X
        point2.setY(20.0);
        point2.setDerive(3.0);
        point2.setFunID(testFunId2); // Другой funID

        Long id1 = dao.create(point1);
        Long id2 = dao.create(point2);

        // Ищем точку с X=5.0 и funID=testFunId1
        Optional<TabPoint> found1 = dao.findByXAndFunID(5.0, testFunId1);
        assertTrue(found1.isPresent(), "Должна быть найдена точка с X=5.0 и funID=" + testFunId1);
        assertEquals(id1, found1.get().getId());

        // Ищем точку с X=5.0 и funID=testFunId2
        Optional<TabPoint> found2 = dao.findByXAndFunID(5.0, testFunId2);
        assertTrue(found2.isPresent(), "Должна быть найдена точка с X=5.0 и funID=" + testFunId2);
        assertEquals(id2, found2.get().getId());

        // Ищем несуществующую комбинацию
        Optional<TabPoint> notFound = dao.findByXAndFunID(5.0, 999999L);
        assertFalse(notFound.isPresent(), "Не должна быть найдена точка с несуществующим funID");
    }

    @Test
    public void testSortedByX() {
        System.out.println("\n=== Тест сортировки по X для TabPoint ===");

        // Создаем точки в случайном порядке по X
        double[] xValues = {3.0, 1.0, 4.0, 2.0};

        for (double x : xValues) {
            TabPoint point = new TabPoint();
            point.setX(x);
            point.setY(x * 2);
            point.setDerive(x * 0.5);
            point.setFunID(testFunId1);
            dao.create(point);
        }

        // Сортировка по возрастанию X
        List<TabPoint> ascSorted = dao.sortedByX("ASC");
        System.out.println("Точки отсортированы по X ASC:");
        for (int i = 0; i < ascSorted.size(); i++) {
            System.out.println("  " + ascSorted.get(i));
            if (i > 0) {
                assertTrue(ascSorted.get(i).getX() >= ascSorted.get(i-1).getX(),
                        "X должны быть отсортированы по возрастанию");
            }
        }

        // Сортировка по убыванию X
        List<TabPoint> descSorted = dao.sortedByX("DESC");
        System.out.println("Точки отсортированы по X DESC:");
        for (int i = 0; i < descSorted.size(); i++) {
            System.out.println("  " + descSorted.get(i));
            if (i > 0) {
                assertTrue(descSorted.get(i).getX() <= descSorted.get(i-1).getX(),
                        "X должны быть отсортированы по убыванию");
            }
        }

        // Сортировка по умолчанию (ASC)
        List<TabPoint> defaultSorted = dao.sortedByX("INVALID");
        assertEquals(ascSorted.size(), defaultSorted.size(),
                "При неверном порядке сортировки должен использоваться ASC");
    }

    @Test
    public void testSortedByFunID() {
        System.out.println("\n=== Тест сортировки по FunID для TabPoint ===");

        // Создаем точки с разными funID
        Long[] funIDs = {testFunId2, testFunId1, testFunId2, testFunId1};

        for (int i = 0; i < funIDs.length; i++) {
            TabPoint point = new TabPoint();
            point.setX(i * 1.0);
            point.setY(i * 2.0);
            point.setDerive(i * 0.5);
            point.setFunID(funIDs[i]);
            dao.create(point);
        }

        // Сортировка по возрастанию funID
        List<TabPoint> ascSorted = dao.sortedByFunID("ASC");
        System.out.println("Точки отсортированы по FunID ASC:");
        for (int i = 0; i < ascSorted.size(); i++) {
            System.out.println("  " + ascSorted.get(i));
            if (i > 0) {
                assertTrue(ascSorted.get(i).getFunID() >= ascSorted.get(i-1).getFunID(),
                        "FunID должны быть отсортированы по возрастанию");
            }
        }

        // Сортировка по убыванию funID
        List<TabPoint> descSorted = dao.sortedByFunID("DESC");
        System.out.println("Точки отсортированы по FunID DESC:");
        for (int i = 0; i < descSorted.size(); i++) {
            System.out.println("  " + descSorted.get(i));
            if (i > 0) {
                assertTrue(descSorted.get(i).getFunID() <= descSorted.get(i-1).getFunID(),
                        "FunID должны быть отсортированы по убыванию");
            }
        }
    }

    @Test
    public void testSortedByID() {
        System.out.println("\n=== Тест сортировки по ID для TabPoint ===");

        // Создаем несколько точек
        for (int i = 0; i < 5; i++) {
            TabPoint point = new TabPoint();
            point.setX(i * 2.0);
            point.setY(i * 3.0);
            point.setDerive(i * 0.5);
            point.setFunID(testFunId1);
            dao.create(point);
        }

        // Сортировка по возрастанию ID
        List<TabPoint> ascSorted = dao.sortedByID("ASC");
        System.out.println("Точки отсортированы по ID ASC:");
        for (int i = 0; i < ascSorted.size(); i++) {
            System.out.println("  " + ascSorted.get(i));
            if (i > 0) {
                assertTrue(ascSorted.get(i).getId() > ascSorted.get(i-1).getId(),
                        "ID должны быть отсортированы по возрастанию");
            }
        }

        // Сортировка по убыванию ID
        List<TabPoint> descSorted = dao.sortedByID("DESC");
        System.out.println("Точки отсортированы по ID DESC:");
        for (int i = 0; i < descSorted.size(); i++) {
            System.out.println("  " + descSorted.get(i));
            if (i > 0) {
                assertTrue(descSorted.get(i).getId() < descSorted.get(i-1).getId(),
                        "ID должны быть отсортированы по убыванию");
            }
        }
    }

    @Test
    public void testDeleteNonExistent() {
        System.out.println("\n=== Тест удаления несуществующей точки для TabPoint ===");

        boolean deleted = dao.delete(999999L);
        System.out.println("Удалена несуществующая точка: " + deleted);
        assertFalse(deleted, "Удаление несуществующей точки должно вернуть false");
    }

    @Test
    public void testFindNonExistent() {
        System.out.println("\n=== Тест поиска несуществующей точки для TabPoint ===");

        Optional<TabPoint> foundById = dao.findByID(999999L);
        System.out.println("Найдена несуществующая точка по ID: " + foundById.isPresent());
        assertFalse(foundById.isPresent(), "Несуществующая точка не должна быть найдена по ID");

        Optional<TabPoint> foundByXAndFunID = dao.findByXAndFunID(999.0, 999999L);
        System.out.println("Найдена несуществующая точка по X и FunID: " + foundByXAndFunID.isPresent());
        assertFalse(foundByXAndFunID.isPresent(), "Несуществующая точка не должна быть найдена по X и FunID");
    }

    @Test
    public void testEmptyFindAll() {
        System.out.println("\n=== Тест пустого FindAll для TabPoint ===");

        List<TabPoint> all = dao.findAll();
        System.out.println("Точек в пустой базе: " + all.size());
        assertTrue(all.isEmpty(), "Список должен быть пустым при пустой базе");
        assertEquals(0, all.size(), "Размер должен быть 0");
    }

    @Test
    public void testLargeValues() {
        System.out.println("\n=== Тест больших значений для TabPoint ===");

        TabPoint point = new TabPoint();
        point.setX(Double.MAX_VALUE);
        point.setY(Double.MIN_NORMAL);
        point.setDerive(1.7976931348623157E308); // Очень большое значение
        point.setFunID(testFunId1);

        Long id = dao.create(point);
        System.out.println("Создана точка с большими значениями, ID: " + id);

        if (id != null) {
            Optional<TabPoint> found = dao.findByID(id);
            assertTrue(found.isPresent(), "Точка должна быть найдена");
            assertEquals(Double.MAX_VALUE, found.get().getX(), 0.001);
            assertEquals(Double.MIN_NORMAL, found.get().getY(), 0.001);
        }
    }

    @Test
    public void testFindByIdAfterMultipleOperations() {
        System.out.println("\n=== Тест поиска после нескольких операций для TabPoint ===");

        TabPoint[] points = new TabPoint[5];
        Long[] ids = new Long[5];

        // Создаем точки
        for (int i = 0; i < 5; i++) {
            points[i] = new TabPoint();
            points[i].setX(i * 1.5);
            points[i].setY(i * 2.5);
            points[i].setDerive(i * 0.3);
            points[i].setFunID(i % 2 == 0 ? testFunId1 : testFunId2);

            ids[i] = dao.create(points[i]);
            assertNotNull(ids[i], "Точка " + i + " должна быть создана");
            System.out.println("Создана точка " + i + " с ID: " + ids[i]);
        }

        // Удаляем каждую вторую точку
        for (int i = 0; i < 5; i += 2) {
            boolean deleted = dao.delete(ids[i]);
            assertTrue(deleted, "Точка с ID " + ids[i] + " должна быть удалена");
            System.out.println("Удалена точка с ID: " + ids[i]);
        }

        // Проверяем, какие точки остались
        for (int i = 0; i < 5; i++) {
            Optional<TabPoint> found = dao.findByID(ids[i]);
            if (i % 2 == 0) {
                assertFalse(found.isPresent(),
                        "Точка с ID " + ids[i] + " должна быть удалена и не найдена");
            } else {
                assertTrue(found.isPresent(),
                        "Точка с ID " + ids[i] + " должна быть найдена");
                assertEquals(points[i].getX(), found.get().getX(), 0.001);
                assertEquals(points[i].getFunID(), found.get().getFunID());
            }
        }
    }
}