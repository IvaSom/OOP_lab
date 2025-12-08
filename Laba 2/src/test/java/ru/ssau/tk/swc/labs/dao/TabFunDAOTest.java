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
        TabFun fun = new TabFun();
        fun.setType("Линейная");

        Long id = dao.create(fun);
        assertNotNull(id);

        Optional<TabFun> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals("Линейная", found.get().getType());

        Optional<TabFun> foundByType = dao.findByType("Линейная");
        assertTrue(foundByType.isPresent());

        boolean deleted = dao.delete(id);
        assertTrue(deleted);

        Optional<TabFun> notFound = dao.findByID(id);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testNullType() {
        TabFun fun = new TabFun();
        fun.setType(null);

        Long id = dao.create(fun);
        assertNull(id);
    }

    @Test
    public void testFindAll() {
        for (int i = 1; i <= 3; i++) {
            TabFun fun = new TabFun();
            fun.setType("Тип " + i);
            Long id = dao.create(fun);
            assertNotNull(id);
        }

        var all = dao.findAll();
        assertEquals(3, all.size());
    }

    @Test
    public void testMultipleCreates() {
        TabFun fun1 = new TabFun();
        fun1.setType("Тип 1");
        Long id1 = dao.create(fun1);
        assertNotNull(id1);

        TabFun fun2 = new TabFun();
        fun2.setType("Тип 2");
        Long id2 = dao.create(fun2);
        assertNotNull(id2);
        assertNotEquals(id1, id2);

        assertEquals(2, dao.findAll().size());
    }

    @Test
    public void testFindNonExistent() {
        Optional<TabFun> found = dao.findByID(999999L);
        assertFalse(found.isPresent());

        Optional<TabFun> foundByType = dao.findByType("Несуществующий тип");
        assertFalse(foundByType.isPresent());
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
    public void testVeryLongType() {
        TabFun fun = new TabFun();
        String longType = "Очень очень очень очень очень очень очень длинный тип табулированной функции";
        fun.setType(longType);

        Long id = dao.create(fun);

        if (id != null) {
            Optional<TabFun> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertTrue(found.get().getType().length() <= 50);
        }
    }

    @Test
    public void testCaseSensitiveTypes() {
        TabFun fun1 = new TabFun();
        fun1.setType("Линейный");

        Long id1 = dao.create(fun1);
        assertNotNull(id1);

        TabFun fun2 = new TabFun();
        fun2.setType("линейный");

        Long id2 = dao.create(fun2);

        if (id2 != null) {
            assertNotEquals(id1, id2);
        }
    }

    @Test
    public void testFindByIdAfterMultipleOperations() {
        TabFun[] functions = new TabFun[5];
        Long[] ids = new Long[5];

        for (int i = 0; i < 5; i++) {
            functions[i] = new TabFun();
            functions[i].setType("Тип " + (i + 1));

            ids[i] = dao.create(functions[i]);
            assertNotNull(ids[i]);
        }

        for (int i = 0; i < 5; i += 2) {
            boolean deleted = dao.delete(ids[i]);
            assertTrue(deleted);
        }

        for (int i = 0; i < 5; i++) {
            Optional<TabFun> found = dao.findByID(ids[i]);
            if (i % 2 == 0) {
                assertFalse(found.isPresent());
            } else {
                assertTrue(found.isPresent());
                assertEquals(functions[i].getType(), found.get().getType());
            }
        }
    }

    @Test
    public void testFindByTypeAfterDeletion() {
        TabFun fun1 = new TabFun();
        fun1.setType("Общий тип");
        Long id1 = dao.create(fun1);
        assertNotNull(id1);

        TabFun fun2 = new TabFun();
        fun2.setType("Общий тип");
        Long id2 = dao.create(fun2);
        assertNotNull(id2);

        boolean deleted = dao.delete(id1);
        assertTrue(deleted);

        Optional<TabFun> found = dao.findByType("Общий тип");
        assertTrue(found.isPresent());
        assertEquals(id2, found.get().getId());
    }

    @Test
    public void testCreateWithEmptyType() {
        TabFun fun = new TabFun();
        fun.setType("");

        Long id = dao.create(fun);

        if (id != null) {
            Optional<TabFun> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals("", found.get().getType());
        }
    }

    @Test
    public void testMultipleFunctionsSameType() {
        for (int i = 0; i < 3; i++) {
            TabFun fun = new TabFun();
            fun.setType("Одинаковый тип");
            Long id = dao.create(fun);
            assertNotNull(id);
        }

        Optional<TabFun> found = dao.findByType("Одинаковый тип");
        assertTrue(found.isPresent());

        assertEquals(3, dao.findAll().size());
    }
}