package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.TabFun;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TabFunDAOTest {
    private TabFunDAO dao;
    private static String dbName;

    @BeforeEach
    public void setup() throws Exception {
        dbName = "tab_fun_test";
        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "");
             Statement stmt = setupConn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS tabFun");
            stmt.execute("""
                        CREATE TABLE tabFun (
                            id BIGSERIAL PRIMARY KEY,
                            type VARCHAR(50) NOT NULL
                        );
                    """);
        }

        dao = new TabFunDAO(new TestDataSourceProvider());
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
        System.out.println("=== Тест Basic CRUD для TabFun ===");

        // CREATE
        TabFun fun = new TabFun();
        fun.setType("Линейная");

        Long id = dao.create(fun);
        System.out.println("Создана табулированная функция с ID: " + id);
        assertNotNull(id, "ID не должен быть null");

        // READ by ID
        Optional<TabFun> found = dao.findByID(id);
        System.out.println("Найдена по ID: " + found.isPresent());
        assertTrue(found.isPresent(), "Табулированная функция должна быть найдена по ID");
        assertEquals("Линейная", found.get().getType());

        // READ by Type
        Optional<TabFun> foundByType = dao.findByType("Линейная");
        System.out.println("Найдена по типу: " + foundByType.isPresent());
        assertTrue(foundByType.isPresent(), "Табулированная функция должна быть найдена по типу");

        // DELETE
        boolean deleted = dao.delete(id);
        System.out.println("Удалена: " + deleted);
        assertTrue(deleted, "Табулированная функция должна быть удалена");

        Optional<TabFun> notFound = dao.findByID(id);
        System.out.println("После удаления: " + notFound.isPresent());
        assertFalse(notFound.isPresent(), "Табулированная функция не должна быть найдена после удаления");
    }

    @Test
    public void testNullType() {
        System.out.println("\n=== Тест Null Type для TabFun ===");

        TabFun fun = new TabFun();
        fun.setType(null);

        Long id = dao.create(fun);
        System.out.println("Создана функция с null типом и ID: " + id);

        // В данном случае, так как поле type NOT NULL, функция не должна быть создана
        assertNull(id, "Функция с null типом не должна быть создана");
    }

    @Test
    public void testFindAll() {
        System.out.println("\n=== Тест FindAll для TabFun ===");

        for (int i = 1; i <= 3; i++) {
            TabFun fun = new TabFun();
            fun.setType("Тип " + i);
            Long id = dao.create(fun);
            assertNotNull(id, "ID не должен быть null для функции " + i);
            System.out.println("Создана табулированная функция " + i + " с ID: " + id);
        }

        var all = dao.findAll();
        System.out.println("Всего табулированных функций: " + all.size());
        assertEquals(3, all.size(), "Должно быть 3 табулированных функции");
    }

    @Test
    public void testMultipleCreates() {
        System.out.println("\n=== Тест нескольких созданий для TabFun ===");

        TabFun fun1 = new TabFun();
        fun1.setType("Тип 1");
        Long id1 = dao.create(fun1);
        System.out.println("ID 1: " + id1);
        assertNotNull(id1, "Первый ID не должен быть null");

        TabFun fun2 = new TabFun();
        fun2.setType("Тип 2");
        Long id2 = dao.create(fun2);
        System.out.println("ID 2: " + id2);
        assertNotNull(id2, "Второй ID не должен быть null");
        assertNotEquals(id1, id2, "ID должны быть разными");

        assertEquals(2, dao.findAll().size(), "Должно быть 2 табулированных функции");
    }

    @Test
    public void testFindNonExistent() {
        System.out.println("\n=== Тест поиска несуществующего для TabFun ===");

        Optional<TabFun> found = dao.findByID(999999L);
        System.out.println("Несуществующий ID: " + found.isPresent());
        assertFalse(found.isPresent(), "Несуществующий ID не должен быть найден");

        Optional<TabFun> foundByType = dao.findByType("Несуществующий тип");
        System.out.println("Несуществующий тип: " + foundByType.isPresent());
        assertFalse(foundByType.isPresent(), "Несуществующий тип не должен быть найден");
    }

    @Test
    public void testDeleteNonExistent() {
        System.out.println("\n=== Тест удаления несуществующего для TabFun ===");

        boolean deleted = dao.delete(999999L);
        System.out.println("Удален несуществующий ID: " + deleted);
        assertFalse(deleted, "Удаление несуществующей функции должно вернуть false");
    }

    @Test
    public void testEmptyFindAll() {
        System.out.println("\n=== Тест пустого FindAll для TabFun ===");

        var all = dao.findAll();
        System.out.println("Табулированных функций в пустой базе: " + all.size());
        assertTrue(all.isEmpty(), "Список должен быть пустым при пустой базе");
        assertEquals(0, all.size(), "Размер должен быть 0");
    }

    @Test
    public void testVeryLongType() {
        System.out.println("\n=== Тест очень длинного типа для TabFun ===");

        TabFun fun = new TabFun();
        String longType = "Очень очень очень очень очень очень очень длинный тип табулированной функции";
        fun.setType(longType);

        Long id = dao.create(fun);
        System.out.println("Табулированная функция с длинным типом создана с ID: " + id);

        if (id != null) {
            Optional<TabFun> found = dao.findByID(id);
            assertTrue(found.isPresent(), "Табулированная функция должна быть найдена");
            assertTrue(found.get().getType().length() <= 50,
                    "Тип должен быть обрезан до 50 символов");
        }
    }

    @Test
    public void testCaseSensitiveTypes() {
        System.out.println("\n=== Тест чувствительности к регистру для типов TabFun ===");

        TabFun fun1 = new TabFun();
        fun1.setType("Линейный");

        Long id1 = dao.create(fun1);
        assertNotNull(id1, "Табулированная функция должна быть создана");
        System.out.println("Создана функция 'Линейный' с ID: " + id1);

        TabFun fun2 = new TabFun();
        fun2.setType("линейный");

        Long id2 = dao.create(fun2);
        System.out.println("Создана функция 'линейный' с ID: " + id2);

        if (id2 != null) {
            assertNotEquals(id1, id2, "Функции с разным регистром должны иметь разные ID");
        }
    }

    @Test
    public void testFindByIdAfterMultipleOperations() {
        System.out.println("\n=== Тест поиска после нескольких операций для TabFun ===");

        TabFun[] functions = new TabFun[5];
        Long[] ids = new Long[5];

        for (int i = 0; i < 5; i++) {
            functions[i] = new TabFun();
            functions[i].setType("Тип " + (i + 1));

            ids[i] = dao.create(functions[i]);
            assertNotNull(ids[i], "Табулированная функция " + (i + 1) + " должна быть создана");
            System.out.println("Создана табулированная функция " + (i + 1) + " с ID: " + ids[i]);
        }

        for (int i = 0; i < 5; i += 2) {
            boolean deleted = dao.delete(ids[i]);
            assertTrue(deleted, "Табулированная функция с ID " + ids[i] + " должна быть удалена");
            System.out.println("Удалена табулированная функция с ID: " + ids[i]);
        }

        for (int i = 0; i < 5; i++) {
            Optional<TabFun> found = dao.findByID(ids[i]);
            if (i % 2 == 0) {
                assertFalse(found.isPresent(),
                        "Табулированная функция с ID " + ids[i] + " должна быть удалена и не найдена");
            } else {
                assertTrue(found.isPresent(),
                        "Табулированная функция с ID " + ids[i] + " должна быть найдена");
                assertEquals(functions[i].getType(), found.get().getType());
            }
        }
    }

    @Test
    public void testFindByTypeAfterDeletion() {
        System.out.println("\n=== Тест поиска по типу после удаления для TabFun ===");

        TabFun fun1 = new TabFun();
        fun1.setType("Общий тип");
        Long id1 = dao.create(fun1);
        assertNotNull(id1, "Первая функция должна быть создана");

        TabFun fun2 = new TabFun();
        fun2.setType("Общий тип");
        Long id2 = dao.create(fun2);
        assertNotNull(id2, "Вторая функция должна быть создана");

        // Удаляем первую функцию
        boolean deleted = dao.delete(id1);
        assertTrue(deleted, "Первая функция должна быть удалена");

        // Поиск по типу все равно должен найти вторую функцию
        Optional<TabFun> found = dao.findByType("Общий тип");
        assertTrue(found.isPresent(), "Должна быть найдена функция с типом 'Общий тип'");
        assertEquals(id2, found.get().getId(), "Должна быть найдена вторая функция");
    }

    @Test
    public void testCreateWithEmptyType() {
        System.out.println("\n=== Тест создания с пустым типом для TabFun ===");

        TabFun fun = new TabFun();
        fun.setType("");

        Long id = dao.create(fun);
        System.out.println("Создана функция с пустым типом и ID: " + id);

        // Пустая строка должна быть допустима, так как тип NOT NULL
        if (id != null) {
            Optional<TabFun> found = dao.findByID(id);
            assertTrue(found.isPresent(), "Функция должна быть найдена");
            assertEquals("", found.get().getType(), "Тип должен быть пустой строкой");
        }
    }

    @Test
    public void testMultipleFunctionsSameType() {
        System.out.println("\n=== Тест нескольких функций с одинаковым типом для TabFun ===");

        // Создаем несколько функций с одинаковым типом
        for (int i = 0; i < 3; i++) {
            TabFun fun = new TabFun();
            fun.setType("Одинаковый тип");
            Long id = dao.create(fun);
            assertNotNull(id, "Функция " + i + " должна быть создана");
            System.out.println("Создана функция " + i + " с типом 'Одинаковый тип' и ID: " + id);
        }

        // Поиск по типу вернет первую найденную
        Optional<TabFun> found = dao.findByType("Одинаковый тип");
        assertTrue(found.isPresent(), "Должна быть найдена хотя бы одна функция с типом 'Одинаковый тип'");

        // Всего должно быть 3 функции
        assertEquals(3, dao.findAll().size(), "Должно быть 3 функции");
    }
}
