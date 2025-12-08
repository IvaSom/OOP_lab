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
        AnalFun fun = new AnalFun();
        fun.setName("Квадратичная");
        fun.setType(1);

        Long id = dao.create(fun);
        assertNotNull(id);

        Optional<AnalFun> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals("Квадратичная", found.get().getName());
        assertEquals(1, found.get().getType());

        Optional<AnalFun> foundByName = dao.findByName("Квадратичная");
        assertTrue(foundByName.isPresent());

        boolean deleted = dao.delete(id);
        assertTrue(deleted);

        Optional<AnalFun> notFound = dao.findByID(id);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testNullType() {
        AnalFun fun = new AnalFun();
        fun.setName("Без типа");

        Long id = dao.create(fun);
        assertNotNull(id);

        Optional<AnalFun> found = dao.findByID(id);

        assertTrue(found.isPresent());
        assertNull(found.get().getType());

        try (Connection checkConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "");
             Statement checkStmt = checkConn.createStatement();
             ResultSet rs = checkStmt.executeQuery("SELECT * FROM analFun WHERE id = " + id)) {

            if (rs.next()) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Ошибка при проверке в БД: " + e.getMessage());
        }
    }

    @Test
    public void testFindAll() {
        for (int i = 1; i <= 3; i++) {
            AnalFun fun = new AnalFun();
            fun.setName("Функция " + i);
            fun.setType(i);
            Long id = dao.create(fun);
            assertNotNull(id);
        }

        var all = dao.findAll();
        assertEquals(3, all.size());
    }

    @Test
    public void testMultipleCreates() {
        AnalFun fun1 = new AnalFun();
        fun1.setName("Функция 1");
        fun1.setType(1);
        Long id1 = dao.create(fun1);
        assertNotNull(id1);

        AnalFun fun2 = new AnalFun();
        fun2.setName("Функция 2");
        fun2.setType(2);
        Long id2 = dao.create(fun2);
        assertNotNull(id2);
        assertNotEquals(id1, id2);

        assertEquals(2, dao.findAll().size());
    }

    @Test
    public void testFindNonExistent() {
        Optional<AnalFun> found = dao.findByID(999999L);
        assertFalse(found.isPresent());

        Optional<AnalFun> foundByName = dao.findByName("Несуществующая");
        assertFalse(foundByName.isPresent());
    }

    @Test
    public void testDeleteNonExistent() {
        boolean deleted = dao.delete(999999L);
        assertFalse(deleted);
    }

    @Test
    public void testEmptyFindAll() {
        var all = dao.findAll();
        assertTrue(all.isEmpty());
        assertEquals(0, all.size());
    }

    @Test
    public void testNullName() {
        AnalFun fun = new AnalFun();
        fun.setName(null);
        fun.setType(1);

        Long id = dao.create(fun);

        assertNull(id);
    }

    @Test
    public void testVeryLongName() {
        AnalFun fun = new AnalFun();
        String longName = "Очень очень очень очень очень очень очень длинное имя функции";
        fun.setName(longName);
        fun.setType(1);

        Long id = dao.create(fun);

        if (id != null) {
            Optional<AnalFun> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertTrue(found.get().getName().length() <= 50);
        }
    }

    @Test
    public void testUpdateThroughCreate() {
        AnalFun fun1 = new AnalFun();
        fun1.setName("Исходная функция");
        fun1.setType(1);

        Long id1 = dao.create(fun1);
        assertNotNull(id1);

        AnalFun fun2 = new AnalFun();
        fun2.setName("Другая функция");
        fun2.setType(2);

        Long id2 = dao.create(fun2);
        assertNotNull(id2);

        assertNotEquals(id1, id2);

        Optional<AnalFun> found1 = dao.findByID(id1);
        Optional<AnalFun> found2 = dao.findByID(id2);

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertNotEquals(found1.get().getName(), found2.get().getName());
    }

    @Test
    public void testCaseSensitiveNames() {
        AnalFun fun1 = new AnalFun();
        fun1.setName("Функция");
        fun1.setType(1);

        Long id1 = dao.create(fun1);
        assertNotNull(id1);

        AnalFun fun2 = new AnalFun();
        fun2.setName("функция");
        fun2.setType(2);

        Long id2 = dao.create(fun2);

        if (id2 != null) {
            assertNotEquals(id1, id2);
        }
    }

    @Test
    public void testInvalidTypeValues() {
        AnalFun fun1 = new AnalFun();
        fun1.setName("Функция с типом 0");
        fun1.setType(0);

        Long id1 = dao.create(fun1);

        AnalFun fun2 = new AnalFun();
        fun2.setName("Функция с типом 11");
        fun2.setType(11);

        Long id2 = dao.create(fun2);

        AnalFun fun3 = new AnalFun();
        fun3.setName("Функция с отрицательным типом");
        fun3.setType(-1);

        Long id3 = dao.create(fun3);
    }

    @Test
    public void testFindByIdAfterMultipleOperations() {
        AnalFun[] functions = new AnalFun[5];
        Long[] ids = new Long[5];

        for (int i = 0; i < 5; i++) {
            functions[i] = new AnalFun();
            functions[i].setName("Функция " + (i + 1));
            functions[i].setType(i + 1);

            ids[i] = dao.create(functions[i]);
            assertNotNull(ids[i]);
        }

        for (int i = 0; i < 5; i += 2) {
            boolean deleted = dao.delete(ids[i]);
            assertTrue(deleted);
        }

        for (int i = 0; i < 5; i++) {
            Optional<AnalFun> found = dao.findByID(ids[i]);
            if (i % 2 == 0) {
                assertFalse(found.isPresent());
            } else {
                assertTrue(found.isPresent());
                assertEquals(functions[i].getName(), found.get().getName());
            }
        }
    }
}