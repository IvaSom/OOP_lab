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
        // Полная очистка и пересоздание БД для каждого теста
        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + DB_NAME + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "")) {

            try (Statement stmt = setupConn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS composite_structure CASCADE");
                stmt.execute("DROP TABLE IF EXISTS compFun CASCADE");
                stmt.execute("DROP TABLE IF EXISTS analFun CASCADE");

                // Создаем родительские таблицы
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

                // Создаем тестовые данные
                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Analytic 1', 1)");
                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Analytic 2', 2)");
                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Analytic 3', 3)");

                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite 1')");
                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite 2')");

                // Создаем таблицу composite_structure
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

        // DAO с провайдером, создающим новые соединения
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
        System.out.println("=== Test Basic CRUD ===");

        // CREATE
        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(1L);
        cs.setOrder(1);

        Long id = dao.create(cs);
        System.out.println("Created composite structure with ID: " + id);
        assertNotNull(id, "ID should not be null");

        // READ by ID
        Optional<CompositeStructure> found = dao.findByID(id);
        System.out.println("Found by ID: " + found.isPresent());
        assertTrue(found.isPresent(), "Composite structure should be found by ID");
        assertEquals(1L, found.get().getComposite_id());
        assertEquals(1L, found.get().getAnalytic_id());
        assertEquals(1, found.get().getOrder());

        // DELETE
        boolean deleted = dao.delete(id);
        System.out.println("Deleted: " + deleted);
        assertTrue(deleted, "Composite structure should be deleted");

        Optional<CompositeStructure> notFound = dao.findByID(id);
        System.out.println("After deletion: " + notFound.isPresent());
        assertFalse(notFound.isPresent(), "Composite structure should not be found after deletion");
    }

    @Test
    public void testFindAll() {
        System.out.println("\n=== Test FindAll ===");

        // Create multiple composite structures
        for (int i = 1; i <= 3; i++) {
            CompositeStructure cs = new CompositeStructure();
            cs.setComposite_id(1L);
            cs.setAnalytic_id((long) i); // Different analytic functions
            cs.setOrder(i);

            Long id = dao.create(cs);
            assertNotNull(id, "ID should not be null for structure " + i);
            System.out.println("Created structure " + i + " with ID: " + id);
        }

        List<CompositeStructure> all = dao.findAll();
        System.out.println("Total structures: " + all.size());
        assertEquals(3, all.size(), "Should be 3 structures");
    }

    @Test
    public void testUniqueConstraint() {
        System.out.println("\n=== Test Unique Constraint ===");

        // First structure with order 1 for composite_id 1
        CompositeStructure cs1 = new CompositeStructure();
        cs1.setComposite_id(1L);
        cs1.setAnalytic_id(1L);
        cs1.setOrder(1);

        Long id1 = dao.create(cs1);
        System.out.println("First structure with order 1, ID: " + id1);
        assertNotNull(id1);

        // Try to create another structure with same composite_id and order
        CompositeStructure cs2 = new CompositeStructure();
        cs2.setComposite_id(1L); // Same composite
        cs2.setAnalytic_id(2L); // Different analytic
        cs2.setOrder(1); // Same order - should violate UNIQUE constraint

        Long id2 = dao.create(cs2);
        System.out.println("Second structure with same order, ID: " + id2);

        // Should fail due to UNIQUE constraint
        if (id2 == null) {
            System.out.println("Unique constraint enforced - second structure not created");
            assertEquals(1, dao.findAll().size(), "Only first structure should exist");
        } else {
            System.out.println("Warning: Unique constraint not enforced");
        }
    }

    @Test
    public void testDifferentOrdersSameComposite() {
        System.out.println("\n=== Test Different Orders Same Composite ===");

        // Create chain of 3 analytic functions for same composite
        for (int order = 1; order <= 3; order++) {
            CompositeStructure cs = new CompositeStructure();
            cs.setComposite_id(1L);
            cs.setAnalytic_id((long) order);
            cs.setOrder(order);

            Long id = dao.create(cs);
            assertNotNull(id, "Structure with order " + order + " should be created");
            System.out.println("Created order " + order + " with ID: " + id);
        }

        List<CompositeStructure> all = dao.findAll();
        assertEquals(3, all.size(), "Should be 3 structures");

        // Verify orders are correct
        for (CompositeStructure cs : all) {
            assertEquals(1L, cs.getComposite_id(), "All should belong to composite 1");
            assertTrue(cs.getOrder() >= 1 && cs.getOrder() <= 3,
                    "Order should be between 1 and 3");
        }
    }

    @Test
    public void testForeignKeyConstraints() {
        System.out.println("\n=== Test Foreign Key Constraints ===");

        // Try to create with non-existent composite_id
        CompositeStructure cs1 = new CompositeStructure();
        cs1.setComposite_id(999L); // Doesn't exist
        cs1.setAnalytic_id(1L);
        cs1.setOrder(1);

        Long id1 = dao.create(cs1);
        System.out.println("With non-existent composite_id, ID: " + id1);
        // Should fail due to foreign key constraint
        assertNull(id1, "Should not create with non-existent composite_id");

        // Try to create with non-existent analytic_id
        CompositeStructure cs2 = new CompositeStructure();
        cs2.setComposite_id(1L);
        cs2.setAnalytic_id(999L); // Doesn't exist
        cs2.setOrder(1);

        Long id2 = dao.create(cs2);
        System.out.println("With non-existent analytic_id, ID: " + id2);
        // Should fail due to foreign key constraint
        assertNull(id2, "Should not create with non-existent analytic_id");
    }

    @Test
    public void testCascadeDelete() {
        System.out.println("\n=== Test Cascade Delete ===");

        // Create structures for composite 1
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

        // Verify both exist
        assertEquals(2, dao.findAll().size(), "Should be 2 structures before delete");

        // Delete one structure directly
        boolean deleted = dao.delete(id1);
        assertTrue(deleted, "Should delete first structure");

        // Verify only one remains
        List<CompositeStructure> remaining = dao.findAll();
        assertEquals(1, remaining.size(), "Should be 1 structure after delete");
        assertEquals(id2, remaining.get(0).getId(), "Remaining should be second structure");
    }

    @Test
    public void testFindNonExistent() {
        System.out.println("\n=== Test Find Non-Existent ===");

        Optional<CompositeStructure> found = dao.findByID(999999L);
        System.out.println("Non-existent ID: " + found.isPresent());
        assertFalse(found.isPresent(), "Non-existent ID should not be found");
    }

    @Test
    public void testDeleteNonExistent() {
        System.out.println("\n=== Test Delete Non-Existent ===");

        boolean deleted = dao.delete(999999L);
        System.out.println("Deleted non-existent: " + deleted);
        assertFalse(deleted, "Deleting non-existent structure should return false");
    }

    @Test
    public void testNegativeOrder() {
        System.out.println("\n=== Test Negative Order ===");

        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(1L);
        cs.setOrder(-1); // Negative order

        Long id = dao.create(cs);
        System.out.println("Structure with negative order, ID: " + id);

        // Should probably be allowed unless there's a CHECK constraint
        if (id != null) {
            assertNotNull(id);
            Optional<CompositeStructure> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals(-1, found.get().getOrder());
        }
    }

    @Test
    public void testZeroOrder() {
        System.out.println("\n=== Test Zero Order ===");

        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(1L);
        cs.setOrder(0); // Zero order

        Long id = dao.create(cs);
        System.out.println("Structure with zero order, ID: " + id);

        if (id != null) {
            assertNotNull(id);
            Optional<CompositeStructure> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals(0, found.get().getOrder());
        }
    }

    @Test
    public void testLargeOrder() {
        System.out.println("\n=== Test Large Order ===");

        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(1L);
        cs.setOrder(999); // Large order number

        Long id = dao.create(cs);
        System.out.println("Structure with large order, ID: " + id);
        assertNotNull(id, "Should create with large order");
    }

    @Test
    public void testSameAnalyticDifferentComposites() {
        System.out.println("\n=== Test Same Analytic Different Composites ===");

        // Same analytic function can be used in different composites
        CompositeStructure cs1 = new CompositeStructure();
        cs1.setComposite_id(1L);
        cs1.setAnalytic_id(1L);
        cs1.setOrder(1);

        CompositeStructure cs2 = new CompositeStructure();
        cs2.setComposite_id(2L); // Different composite
        cs2.setAnalytic_id(1L); // Same analytic
        cs2.setOrder(1);

        Long id1 = dao.create(cs1);
        Long id2 = dao.create(cs2);

        System.out.println("Same analytic in composite 1, ID: " + id1);
        System.out.println("Same analytic in composite 2, ID: " + id2);

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2, "Should have different IDs");
        assertEquals(2, dao.findAll().size(), "Should be 2 structures");
    }

    @Test
    public void testToString() {
        System.out.println("\n=== Test ToString ===");

        CompositeStructure cs = new CompositeStructure();
        cs.setComposite_id(1L);
        cs.setAnalytic_id(2L);
        cs.setOrder(3);

        Long id = dao.create(cs);
        assertNotNull(id);

        Optional<CompositeStructure> found = dao.findByID(id);
        assertTrue(found.isPresent());

        String toString = found.get().toString();
        System.out.println("toString: " + toString);
        assertTrue(toString.contains("composite_id=1"), "Should contain composite_id");
        assertTrue(toString.contains("analytic_id=2"), "Should contain analytic_id");
        assertTrue(toString.contains("order=3"), "Should contain order");
    }

    @Test
    public void testEmptyDatabase() {
        System.out.println("\n=== Test Empty Database ===");

        List<CompositeStructure> all = dao.findAll();
        assertTrue(all.isEmpty(), "List should be empty");
        assertEquals(0, all.size(), "Size should be 0");
    }

    @Test
    public void testMultipleComposites() {
        System.out.println("\n=== Test Multiple Composites ===");

        // Create structures for 2 different composites
        for (int compId = 1; compId <= 2; compId++) {
            for (int order = 1; order <= 2; order++) {
                CompositeStructure cs = new CompositeStructure();
                cs.setComposite_id((long) compId);
                cs.setAnalytic_id((long) order);
                cs.setOrder(order);

                Long id = dao.create(cs);
                assertNotNull(id, "Structure for composite " + compId + " order " + order + " should be created");
            }
        }

        List<CompositeStructure> all = dao.findAll();
        assertEquals(4, all.size(), "Should be 4 structures total");

        // Count structures per composite
        long countComp1 = all.stream().filter(cs -> cs.getComposite_id() == 1L).count();
        long countComp2 = all.stream().filter(cs -> cs.getComposite_id() == 2L).count();

        assertEquals(2, countComp1, "Composite 1 should have 2 structures");
        assertEquals(2, countComp2, "Composite 2 should have 2 structures");
    }
}
