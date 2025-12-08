package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.AnalFun;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class AnalFunDAOTest {
    private AnalFunDAO dao;
    private static AtomicInteger dbCounter = new AtomicInteger(0);
    private static String dbName;



    @BeforeEach
    public void setup() throws Exception {
        dbName = "testdb_" + dbCounter.getAndIncrement();
        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "");
             Statement stmt = setupConn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS analFun");
            stmt.execute("""
                        CREATE TABLE analFun (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(50) NOT NULL,
                            type INTEGER
                        );
                    """);
        }

        dao = new AnalFunDAO(new TestDataSourceProvider());
    }

    private static class TestDataSourceProvider implements DataSourceProvider {
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
        System.out.println("=== Тест Basic CRUD ===");

        // CREATE
        AnalFun fun = new AnalFun();
        fun.setName("Квадратичная");
        fun.setType(1);

        Long id = dao.create(fun);
        System.out.println("Создана функция с ID: " + id);
        assertNotNull(id, "ID не должен быть null");

        // READ by ID
        Optional<AnalFun> found = dao.findByID(id);
        System.out.println("Найдена по ID: " + found.isPresent());
        assertTrue(found.isPresent(), "Функция должна быть найдена по ID");
        assertEquals("Квадратичная", found.get().getName());
        assertEquals(1, found.get().getType());

        // READ by Name
        Optional<AnalFun> foundByName = dao.findByName("Квадратичная");
        System.out.println("Найдена по имени: " + foundByName.isPresent());
        assertTrue(foundByName.isPresent(), "Функция должна быть найдена по имени");

        // DELETE
        boolean deleted = dao.delete(id);
        System.out.println("Удалена: " + deleted);
        assertTrue(deleted, "Функция должна быть удалена");

        Optional<AnalFun> notFound = dao.findByID(id);
        System.out.println("После удаления: " + notFound.isPresent());
        assertFalse(notFound.isPresent(), "Функция не должна быть найдена после удаления");
    }

    @Test
    public void testNullType() {
        System.out.println("\n=== Тест Null Type ===");

        AnalFun fun = new AnalFun();
        fun.setName("Без типа");

        Long id = dao.create(fun);
        System.out.println("Создана функция с ID: " + id);
        assertNotNull(id, "ID не должен быть null");

        Optional<AnalFun> found = dao.findByID(id);
        System.out.println("Найдена функцией поиска: " + found.isPresent());

        assertTrue(found.isPresent(), "Функция должна быть найдена");
        assertNull(found.get().getType(), "Тип должен быть null");

        try (Connection checkConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "");
             Statement checkStmt = checkConn.createStatement();
             ResultSet rs = checkStmt.executeQuery("SELECT * FROM analFun WHERE id = " + id)) {

            if (rs.next()) {
                System.out.println("В БД: id=" + rs.getLong("id") +
                        ", name=" + rs.getString("name") +
                        ", type=" + rs.getObject("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Ошибка при проверке в БД: " + e.getMessage());
        }
    }

    @Test
    public void testFindAll() {
        System.out.println("\n=== Тест FindAll ===");

        for (int i = 1; i <= 3; i++) {
            AnalFun fun = new AnalFun();
            fun.setName("Функция " + i);
            fun.setType(i);
            Long id = dao.create(fun);
            assertNotNull(id, "ID не должен быть null для функции " + i);
            System.out.println("Создана функция " + i + " с ID: " + id);
        }

        var all = dao.findAll();
        System.out.println("Всего функций: " + all.size());
        assertEquals(3, all.size(), "Должно быть 3 функции");
    }

    @Test
    public void testMultipleCreates() {
        System.out.println("\n=== Тест нескольких созданий ===");

        AnalFun fun1 = new AnalFun();
        fun1.setName("Функция 1");
        fun1.setType(1);
        Long id1 = dao.create(fun1);
        System.out.println("ID 1: " + id1);
        assertNotNull(id1, "Первый ID не должен быть null");

        AnalFun fun2 = new AnalFun();
        fun2.setName("Функция 2");
        fun2.setType(2);
        Long id2 = dao.create(fun2);
        System.out.println("ID 2: " + id2);
        assertNotNull(id2, "Второй ID не должен быть null");
        assertNotEquals(id1, id2, "ID должны быть разными");

        assertEquals(2, dao.findAll().size(), "Должно быть 2 функции");
    }

    @Test
    public void testFindNonExistent() {
        System.out.println("\n=== Тест поиска несуществующего ===");

        Optional<AnalFun> found = dao.findByID(999999L);
        System.out.println("Несуществующий ID: " + found.isPresent());
        assertFalse(found.isPresent(), "Несуществующий ID не должен быть найден");

        Optional<AnalFun> foundByName = dao.findByName("Несуществующая");
        System.out.println("Несуществующее имя: " + foundByName.isPresent());
        assertFalse(foundByName.isPresent(), "Несуществующее имя не должно быть найдено");
    }

    @Test
    public void testDeleteNonExistent() {
        System.out.println("\n=== Тест удаления несуществующего ===");

        boolean deleted = dao.delete(999999L);
        System.out.println("Удален несуществующий ID: " + deleted);
        assertFalse(deleted, "Удаление несуществующей функции должно вернуть false");
    }

    @Test
    public void testEmptyFindAll() {
        System.out.println("\n=== Тест пустого FindAll ===");

        var all = dao.findAll();
        System.out.println("Функций в пустой базе: " + all.size());
        assertTrue(all.isEmpty(), "Список должен быть пустым при пустой базе");
        assertEquals(0, all.size(), "Размер должен быть 0");
    }

    @Test
    public void testNullName() {
        System.out.println("\n=== Тест null имени ===");

        AnalFun fun = new AnalFun();
        fun.setName(null);
        fun.setType(1);

        Long id = dao.create(fun);
        System.out.println("Функция с null именем создана с ID: " + id);

        assertNull(id, "Функция с null именем не должна быть создана");
    }

    @Test
    public void testVeryLongName() {
        System.out.println("\n=== Тест очень длинного имени ===");

        AnalFun fun = new AnalFun();
        String longName = "Очень очень очень очень очень очень очень длинное имя функции";
        fun.setName(longName);
        fun.setType(1);

        Long id = dao.create(fun);
        System.out.println("Функция с длинным именем создана с ID: " + id);

        if (id != null) {
            Optional<AnalFun> found = dao.findByID(id);
            assertTrue(found.isPresent(), "Функция должна быть найдена");
            assertTrue(found.get().getName().length() <= 50,
                    "Имя должно быть обрезано до 50 символов");
        }
    }

    @Test
    public void testUpdateThroughCreate() {
        System.out.println("\n=== Тест 'обновления' через создание ===");

        AnalFun fun1 = new AnalFun();
        fun1.setName("Исходная функция");
        fun1.setType(1);

        Long id1 = dao.create(fun1);
        assertNotNull(id1, "Функция должна быть создана");
        System.out.println("Создана функция с ID: " + id1);

        AnalFun fun2 = new AnalFun();
        fun2.setName("Другая функция");
        fun2.setType(2);

        Long id2 = dao.create(fun2);
        assertNotNull(id2, "Вторая функция должна быть создана");
        System.out.println("Создана вторая функция с ID: " + id2);

        assertNotEquals(id1, id2, "ID должны быть разными");

        Optional<AnalFun> found1 = dao.findByID(id1);
        Optional<AnalFun> found2 = dao.findByID(id2);

        assertTrue(found1.isPresent(), "Первая функция должна существовать");
        assertTrue(found2.isPresent(), "Вторая функция должна существовать");
        assertNotEquals(found1.get().getName(), found2.get().getName(), "Имена должны быть разными");
    }

    @Test
    public void testCaseSensitiveNames() {
        System.out.println("\n=== Тест чувствительности к регистру ===");

        AnalFun fun1 = new AnalFun();
        fun1.setName("Функция");
        fun1.setType(1);

        Long id1 = dao.create(fun1);
        assertNotNull(id1, "Функция должна быть создана");
        System.out.println("Создана функция 'Функция' с ID: " + id1);

        AnalFun fun2 = new AnalFun();
        fun2.setName("функция");
        fun2.setType(2);

        Long id2 = dao.create(fun2);
        System.out.println("Создана функция 'функция' с ID: " + id2);

        if (id2 != null) {
            assertNotEquals(id1, id2, "Функции с разным регистром должны иметь разные ID");
        }
    }

    @Test
    public void testInvalidTypeValues() {
        System.out.println("\n=== Тест недопустимых значений типа ===");

        AnalFun fun1 = new AnalFun();
        fun1.setName("Функция с типом 0");
        fun1.setType(0);

        Long id1 = dao.create(fun1);
        System.out.println("Функция с типом 0 создана с ID: " + id1);

        AnalFun fun2 = new AnalFun();
        fun2.setName("Функция с типом 11");
        fun2.setType(11);

        Long id2 = dao.create(fun2);
        System.out.println("Функция с типом 11 создана с ID: " + id2);

        AnalFun fun3 = new AnalFun();
        fun3.setName("Функция с отрицательным типом");
        fun3.setType(-1);

        Long id3 = dao.create(fun3);
        System.out.println("Функция с типом -1 создана с ID: " + id3);
    }

    @Test
    public void testFindByIdAfterMultipleOperations() {
        System.out.println("\n=== Тест поиска после нескольких операций ===");

        AnalFun[] functions = new AnalFun[5];
        Long[] ids = new Long[5];

        for (int i = 0; i < 5; i++) {
            functions[i] = new AnalFun();
            functions[i].setName("Функция " + (i + 1));
            functions[i].setType(i + 1);

            ids[i] = dao.create(functions[i]);
            assertNotNull(ids[i], "Функция " + (i + 1) + " должна быть создана");
            System.out.println("Создана функция " + (i + 1) + " с ID: " + ids[i]);
        }

        for (int i = 0; i < 5; i += 2) {
            boolean deleted = dao.delete(ids[i]);
            assertTrue(deleted, "Функция с ID " + ids[i] + " должна быть удалена");
            System.out.println("Удалена функция с ID: " + ids[i]);
        }

        for (int i = 0; i < 5; i++) {
            Optional<AnalFun> found = dao.findByID(ids[i]);
            if (i % 2 == 0) {
                assertFalse(found.isPresent(),
                        "Функция с ID " + ids[i] + " должна быть удалена и не найдена");
            } else {
                assertTrue(found.isPresent(),
                        "Функция с ID " + ids[i] + " должна быть найдена");
                assertEquals(functions[i].getName(), found.get().getName());
            }
        }
    }
}