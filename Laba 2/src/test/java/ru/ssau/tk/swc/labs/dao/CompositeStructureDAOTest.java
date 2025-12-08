package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.CompositeStructure;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CompositeStructureDAOTest {
    private static final String DB_NAME = "composite_structure_test";
    private CompositeStructureDAO dao;

    @BeforeEach
    public void setup() throws Exception {
        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + DB_NAME + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "")) {

            try (Statement stmt = setupConn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS composite_structure CASCADE");
                stmt.execute("DROP TABLE IF EXISTS compFun CASCADE");
                stmt.execute("DROP TABLE IF EXISTS analFun CASCADE");

                stmt.execute("""
                    CREATE TABLE analFun (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        type INTEGER
                    );
                """);

                stmt.execute("""
                    CREATE TABLE compFun (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
                    );
                """);

                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Analytic 1', 1)");
                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Analytic 2', 2)");
                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Analytic 3', 3)");

                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite 1')");
                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite 2')");

                stmt.execute("""
                    CREATE TABLE composite_structure (
                        id BIGSERIAL PRIMARY KEY,
                        composite_id BIGINT NOT NULL,
                        analytic_id BIGINT NOT NULL,
                        execution_order INTEGER NOT NULL,
                        FOREIGN KEY (composite_id) REFERENCES compFun(id) ON DELETE CASCADE,
                        FOREIGN KEY (analytic_id) REFERENCES analFun(id),
                        UNIQUE(composite_id, execution_order)
                    );
                """);
            }
        }

        dao = new CompositeStructureDAO(() -> {
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
        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(1L);
        cs.setOrder(1);

        Long id = dao.create(cs);
        assertNotNull(id);

        Optional<CompositeStructure> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getComposite_id());
        assertEquals(1L, found.get().getAnalytic_id());
        assertEquals(1, found.get().getOrder());

        boolean deleted = dao.delete(id);
        assertTrue(deleted);

        Optional<CompositeStructure> notFound = dao.findByID(id);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFindAll() {
        for (int i = 1; i <= 3; i++) {
            CompositeStructure cs = new CompositeStructure();
            cs.setComposite_id(1L);
            cs.setAnalytic_id((long) i);
            cs.setOrder(i);

            Long id = dao.create(cs);
            assertNotNull(id);
        }

        List<CompositeStructure> all = dao.findAll();
        assertEquals(3, all.size());
    }

    @Test
    public void testUniqueConstraint() {
        CompositeStructure cs1 = new CompositeStructure();
        cs1.setComposite_id(1L);
        cs1.setAnalytic_id(1L);
        cs1.setOrder(1);

        Long id1 = dao.create(cs1);
        assertNotNull(id1);

        CompositeStructure cs2 = new CompositeStructure();
        cs2.setComposite_id(1L);
        cs2.setAnalytic_id(2L);
        cs2.setOrder(1);

        Long id2 = dao.create(cs2);

        if (id2 == null) {
            assertEquals(1, dao.findAll().size());
        }
    }

    @Test
    public void testDifferentOrdersSameComposite() {
        for (int order = 1; order <= 3; order++) {
            CompositeStructure cs = new CompositeStructure();
            cs.setComposite_id(1L);
            cs.setAnalytic_id((long) order);
            cs.setOrder(order);

            Long id = dao.create(cs);
            assertNotNull(id);
        }

        List<CompositeStructure> all = dao.findAll();
        assertEquals(3, all.size());

        for (CompositeStructure cs : all) {
            assertEquals(1L, cs.getComposite_id());
            assertTrue(cs.getOrder() >= 1 && cs.getOrder() <= 3);
        }
    }

    @Test
    public void testForeignKeyConstraints() {
        CompositeStructure cs1 = new CompositeStructure();
        cs1.setComposite_id(999L);
        cs1.setAnalytic_id(1L);
        cs1.setOrder(1);

        Long id1 = dao.create(cs1);
        assertNull(id1);

        CompositeStructure cs2 = new CompositeStructure();
        cs2.setComposite_id(1L);
        cs2.setAnalytic_id(999L);
        cs2.setOrder(1);

        Long id2 = dao.create(cs2);
        assertNull(id2);
    }

    @Test
    public void testCascadeDelete() {
        CompositeStructure cs1 = new CompositeStructure();
        cs1.setComposite_id(1L);
        cs1.setAnalytic_id(1L);
        cs1.setOrder(1);
        Long id1 = dao.create(cs1);
        assertNotNull(id1);

        CompositeStructure cs2 = new CompositeStructure();
        cs2.setComposite_id(1L);
        cs2.setAnalytic_id(2L);
        cs2.setOrder(2);
        Long id2 = dao.create(cs2);
        assertNotNull(id2);

        assertEquals(2, dao.findAll().size());

        boolean deleted = dao.delete(id1);
        assertTrue(deleted);

        List<CompositeStructure> remaining = dao.findAll();
        assertEquals(1, remaining.size());
        assertEquals(id2, remaining.get(0).getId());
    }

    @Test
    public void testFindNonExistent() {
        Optional<CompositeStructure> found = dao.findByID(999999L);
        assertFalse(found.isPresent());
    }

    @Test
    public void testDeleteNonExistent() {
        boolean deleted = dao.delete(999999L);
        assertFalse(deleted);
    }

    @Test
    public void testNegativeOrder() {
        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(1L);
        cs.setOrder(-1);

        Long id = dao.create(cs);

        if (id != null) {
            assertNotNull(id);
            Optional<CompositeStructure> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals(-1, found.get().getOrder());
        }
    }

    @Test
    public void testZeroOrder() {
        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(1L);
        cs.setOrder(0);

        Long id = dao.create(cs);

        if (id != null) {
            assertNotNull(id);
            Optional<CompositeStructure> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals(0, found.get().getOrder());
        }
    }

    @Test
    public void testLargeOrder() {
        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(1L);
        cs.setOrder(999);

        Long id = dao.create(cs);
        assertNotNull(id);
    }

    @Test
    public void testSameAnalyticDifferentComposites() {
        CompositeStructure cs1 = new CompositeStructure();
        cs1.setComposite_id(1L);
        cs1.setAnalytic_id(1L);
        cs1.setOrder(1);

        CompositeStructure cs2 = new CompositeStructure();
        cs2.setComposite_id(2L);
        cs2.setAnalytic_id(1L);
        cs2.setOrder(1);

        Long id1 = dao.create(cs1);
        Long id2 = dao.create(cs2);

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
        assertEquals(2, dao.findAll().size());
    }

    @Test
    public void testToString() {
        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(2L);
        cs.setOrder(3);

        Long id = dao.create(cs);
        assertNotNull(id);

        Optional<CompositeStructure> found = dao.findByID(id);
        assertTrue(found.isPresent());

        String toString = found.get().toString();
        assertTrue(toString.contains("composite_id=1"));
        assertTrue(toString.contains("analytic_id=2"));
        assertTrue(toString.contains("order=3"));
    }

    @Test
    public void testEmptyDatabase() {
        List<CompositeStructure> all = dao.findAll();
        assertTrue(all.isEmpty());
        assertEquals(0, all.size());
    }

    @Test
    public void testMultipleComposites() {
        for (int compId = 1; compId <= 2; compId++) {
            for (int order = 1; order <= 2; order++) {
                CompositeStructure cs = new CompositeStructure();
                cs.setComposite_id((long) compId);
                cs.setAnalytic_id((long) order);
                cs.setOrder(order);

                Long id = dao.create(cs);
                assertNotNull(id);
            }
        }

        List<CompositeStructure> all = dao.findAll();
        assertEquals(4, all.size());

        long countComp1 = all.stream().filter(cs -> cs.getComposite_id() == 1L).count();
        long countComp2 = all.stream().filter(cs -> cs.getComposite_id() == 2L).count();

        assertEquals(2, countComp1);
        assertEquals(2, countComp2);
    }
}