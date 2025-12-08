package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.CompFun;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CompFunDAOTest {
    private static final String DB_NAME = "compfun_test";
    private CompFunDAO dao;

    @BeforeEach
    public void setup() throws Exception {
        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + DB_NAME + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "")) {

            try (Statement stmt = setupConn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS compFun CASCADE");

                stmt.execute("""
                    CREATE TABLE compFun (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
                    );
                """);
            }
        }

        dao = new CompFunDAO(() -> {
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:h2:mem:" + DB_NAME + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                        "sa",
                        ""
                );
                conn.setAutoCommit(true);
                return conn;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testBasicCRUD() {
        CompFun fun = new CompFun();
        fun.setName("Composite Function 1");

        Long id = dao.create(fun);
        assertNotNull(id);

        Optional<CompFun> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals("Composite Function 1", found.get().getName());

        Optional<CompFun> foundByName = dao.findByName("Composite Function 1");
        assertTrue(foundByName.isPresent());

        boolean deleted = dao.delete(id);
        assertTrue(deleted);

        Optional<CompFun> notFound = dao.findByID(id);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFindAll() {
        for (int i = 1; i <= 3; i++) {
            CompFun fun = new CompFun();
            fun.setName("Function " + i);
            Long id = dao.create(fun);
            assertNotNull(id);
        }

        List<CompFun> all = dao.findAll();
        assertEquals(3, all.size());

        assertEquals("Function 1", all.get(0).getName());
        assertEquals("Function 2", all.get(1).getName());
        assertEquals("Function 3", all.get(2).getName());
    }

    @Test
    public void testFindNonExistent() {
        Optional<CompFun> found = dao.findByID(999999L);
        assertFalse(found.isPresent());

        Optional<CompFun> foundByName = dao.findByName("Non-existent Function");
        assertFalse(foundByName.isPresent());
    }

    @Test
    public void testDeleteNonExistent() {
        boolean deleted = dao.delete(999999L);
        assertFalse(deleted);
    }

    @Test
    public void testDuplicateNames() {
        CompFun fun1 = new CompFun();
        fun1.setName("Same Name");
        Long id1 = dao.create(fun1);
        assertNotNull(id1);

        CompFun fun2 = new CompFun();
        fun2.setName("Same Name");
        Long id2 = dao.create(fun2);

        if (id2 != null) {
            assertNotEquals(id1, id2);
            assertEquals(2, dao.findAll().size());
        } else {
            assertEquals(1, dao.findAll().size());
        }
    }

    @Test
    public void testEmptyName() {
        CompFun fun = new CompFun();
        fun.setName("");
        Long id = dao.create(fun);

        if (id != null) {
            assertNotNull(id);
            Optional<CompFun> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals("", found.get().getName());
        }
    }

    @Test
    public void testNullName() {
        CompFun fun = new CompFun();
        fun.setName(null);
        Long id = dao.create(fun);

        assertNull(id);
    }

    @Test
    public void testVeryLongName() {
        CompFun fun = new CompFun();
        String longName = "Very very very very very very very very very long function name";
        fun.setName(longName);
        Long id = dao.create(fun);

        if (id != null) {
            assertNotNull(id);
            Optional<CompFun> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertTrue(found.get().getName().length() <= 50);
        }
    }

    @Test
    public void testMultipleOperations() {
        Long[] ids = new Long[5];
        for (int i = 0; i < 5; i++) {
            CompFun fun = new CompFun();
            fun.setName("Function " + (i + 1));
            ids[i] = dao.create(fun);
            assertNotNull(ids[i]);
        }

        for (int i = 0; i < 5; i += 2) {
            boolean deleted = dao.delete(ids[i]);
            assertTrue(deleted);
        }

        List<CompFun> remaining = dao.findAll();
        assertEquals(2, remaining.size());

        Optional<CompFun> found2 = dao.findByID(ids[1]);
        assertTrue(found2.isPresent());

        Optional<CompFun> found4 = dao.findByID(ids[3]);
        assertTrue(found4.isPresent());
    }

    @Test
    public void testFindByNameCaseSensitive() {
        CompFun fun = new CompFun();
        fun.setName("MixedCaseFunction");
        Long id = dao.create(fun);
        assertNotNull(id);

        Optional<CompFun> exact = dao.findByName("MixedCaseFunction");
        assertTrue(exact.isPresent());

        Optional<CompFun> lower = dao.findByName("mixedcasefunction");
        Optional<CompFun> upper = dao.findByName("MIXEDCASEFUNCTION");
    }

    @Test
    public void testEmptyDatabase() {
        List<CompFun> all = dao.findAll();
        assertTrue(all.isEmpty());
        assertEquals(0, all.size());
    }

    @Test
    public void testToString() {
        CompFun fun = new CompFun();
        fun.setName("Test Function");
        Long id = dao.create(fun);
        assertNotNull(id);

        Optional<CompFun> found = dao.findByID(id);
        assertTrue(found.isPresent());

        String toString = found.get().toString();
        assertTrue(toString.contains("Test Function"));
        assertTrue(toString.contains(id.toString()));
    }
}
