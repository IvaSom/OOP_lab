package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.CompPoint;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CompPointDAOTest {
    private static final String DB_NAME = "comp_point_test";
    private CompPointDAO dao;

    @BeforeEach
    public void setup() throws Exception {
        // Полная очистка и пересоздание БД для каждого теста
        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + DB_NAME + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "")) {

            try (Statement stmt = setupConn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS comp_points CASCADE");
                stmt.execute("DROP TABLE IF EXISTS compFun CASCADE");

                // Создаем родительскую таблицу compFun
                stmt.execute("""
                    CREATE TABLE compFun (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
                    );
                """);

                // Создаем тестовые композитные функции
                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite Function 1')");
                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite Function 2')");
                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite Function 3')");

                // Создаем таблицу comp_points
                stmt.execute("""
                    CREATE TABLE comp_points (
                        id BIGSERIAL PRIMARY KEY,
                        x DOUBLE PRECISION NOT NULL,
                        y DOUBLE PRECISION NOT NULL,
                        funID BIGINT NOT NULL,
                        CONSTRAINT funID_fk FOREIGN KEY (funID) REFERENCES compFun (id)
                    );
                """);
            }
        }

        // DAO с провайдером, создающим новые соединения
        dao = new CompPointDAO(() -> {
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
        CompPoint point = new CompPoint();
        point.setX(2.5);
        point.setY(3.7);
        point.setFunID(1L);

        Long id = dao.create(point);
        System.out.println("Created composite point with ID: " + id);
        assertNotNull(id, "ID should not be null");

        // READ by ID
        Optional<CompPoint> found = dao.findByID(id);
        System.out.println("Found by ID: " + found.isPresent());
        assertTrue(found.isPresent(), "Point should be found by ID");
        assertEquals(2.5, found.get().getX(), 0.001);
        assertEquals(3.7, found.get().getY(), 0.001);
        assertEquals(1L, found.get().getFunID());

        // READ by X and FunID
        Optional<CompPoint> foundByXAndFunID = dao.findByXAndFunID(2.5, 1L);
        System.out.println("Found by X and FunID: " + foundByXAndFunID.isPresent());
        assertTrue(foundByXAndFunID.isPresent());

        // DELETE
        boolean deleted = dao.delete(id);
        System.out.println("Deleted: " + deleted);
        assertTrue(deleted, "Point should be deleted");

        Optional<CompPoint> notFound = dao.findByID(id);
        System.out.println("After deletion: " + notFound.isPresent());
        assertFalse(notFound.isPresent(), "Point should not be found after deletion");
    }

    @Test
    public void testFindAll() {
        System.out.println("\n=== Test FindAll ===");

        // Create points for different composite functions
        for (int i = 0; i < 4; i++) {
            CompPoint point = new CompPoint();
            point.setX(i * 1.5);
            point.setY(i * 2.2);
            point.setFunID((i % 3) + 1L); // Cycle through 3 functions

            Long id = dao.create(point);
            assertNotNull(id, "ID should not be null for point " + (i+1));
            System.out.println("Created point " + (i+1) + " with ID: " + id);
        }

        List<CompPoint> all = dao.findAll();
        System.out.println("Total points: " + all.size());
        assertEquals(4, all.size(), "Should be 4 points");
    }

    @Test
    public void testFindByXAndFunID() {
        System.out.println("\n=== Test FindByXAndFunID ===");

        // Create points with same X but different funID
        CompPoint point1 = new CompPoint();
        point1.setX(5.0);
        point1.setY(10.0);
        point1.setFunID(1L);
        Long id1 = dao.create(point1);
        assertNotNull(id1);

        CompPoint point2 = new CompPoint();
        point2.setX(5.0); // Same X
        point2.setY(15.0);
        point2.setFunID(2L); // Different funID
        Long id2 = dao.create(point2);
        assertNotNull(id2);

        // Should find point for funID 1
        Optional<CompPoint> found1 = dao.findByXAndFunID(5.0, 1L);
        assertTrue(found1.isPresent());
        assertEquals(10.0, found1.get().getY(), 0.001);

        // Should find point for funID 2
        Optional<CompPoint> found2 = dao.findByXAndFunID(5.0, 2L);
        assertTrue(found2.isPresent());
        assertEquals(15.0, found2.get().getY(), 0.001);

        // Should not find non-existent combination
        Optional<CompPoint> notFound = dao.findByXAndFunID(5.0, 999L);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testSortedByX() {
        System.out.println("\n=== Test SortedByX ===");

        // Create points in random X order
        double[] xValues = {7.0, 2.0, 5.0, 1.0};
        for (double x : xValues) {
            CompPoint point = new CompPoint();
            point.setX(x);
            point.setY(x * 3);
            point.setFunID(1L);
            dao.create(point);
        }

        // ASC order
        List<CompPoint> asc = dao.sortedByX("ASC");
        assertEquals(4, asc.size());
        assertEquals(1.0, asc.get(0).getX(), 0.001);
        assertEquals(2.0, asc.get(1).getX(), 0.001);
        assertEquals(5.0, asc.get(2).getX(), 0.001);
        assertEquals(7.0, asc.get(3).getX(), 0.001);

        // DESC order
        List<CompPoint> desc = dao.sortedByX("DESC");
        assertEquals(4, desc.size());
        assertEquals(7.0, desc.get(0).getX(), 0.001);
        assertEquals(5.0, desc.get(1).getX(), 0.001);
        assertEquals(2.0, desc.get(2).getX(), 0.001);
        assertEquals(1.0, desc.get(3).getX(), 0.001);

        // Default (ASC) when invalid order
        List<CompPoint> def = dao.sortedByX("INVALID");
        assertEquals(4, def.size());
        assertEquals(1.0, def.get(0).getX(), 0.001);
    }

    @Test
    public void testSortedByFunID() {
        System.out.println("\n=== Test SortedByFunID ===");

        // Create points with different funIDs
        long[] funIDs = {3L, 1L, 2L, 3L, 1L};
        for (int i = 0; i < funIDs.length; i++) {
            CompPoint point = new CompPoint();
            point.setX(i * 1.1);
            point.setY(i * 2.2);
            point.setFunID(funIDs[i]);
            dao.create(point);
        }

        // ASC order
        List<CompPoint> asc = dao.sortedByFunID("ASC");
        assertEquals(5, asc.size());
        // First should be funID 1
        assertEquals(1L, asc.get(0).getFunID());
        assertEquals(1L, asc.get(1).getFunID());
        // Then funID 2
        assertEquals(2L, asc.get(2).getFunID());
        // Then funID 3
        assertEquals(3L, asc.get(3).getFunID());
        assertEquals(3L, asc.get(4).getFunID());

        // DESC order
        List<CompPoint> desc = dao.sortedByFunID("DESC");
        assertEquals(5, desc.size());
        // First should be funID 3
        assertEquals(3L, desc.get(0).getFunID());
        assertEquals(3L, desc.get(1).getFunID());
        // Then funID 2
        assertEquals(2L, desc.get(2).getFunID());
        // Then funID 1
        assertEquals(1L, desc.get(3).getFunID());
        assertEquals(1L, desc.get(4).getFunID());
    }

    @Test
    public void testSortedByID() {
        System.out.println("\n=== Test SortedByID ===");

        // Create 4 points
        for (int i = 0; i < 4; i++) {
            CompPoint point = new CompPoint();
            point.setX(i * 2.0);
            point.setY(i * 3.0);
            point.setFunID(1L);
            dao.create(point);
        }

        // ASC order (default)
        List<CompPoint> asc = dao.sortedByID("ASC");
        assertEquals(4, asc.size());
        // IDs should be in increasing order
        for (int i = 0; i < 3; i++) {
            assertTrue(asc.get(i).getId() < asc.get(i + 1).getId(),
                    "IDs should be in increasing order");
        }

        // DESC order
        List<CompPoint> desc = dao.sortedByID("DESC");
        assertEquals(4, desc.size());
        // IDs should be in decreasing order
        for (int i = 0; i < 3; i++) {
            assertTrue(desc.get(i).getId() > desc.get(i + 1).getId(),
                    "IDs should be in decreasing order");
        }
    }

    @Test
    public void testForeignKeyConstraint() {
        System.out.println("\n=== Test Foreign Key Constraint ===");

        // Try to create point with non-existent funID
        CompPoint point = new CompPoint();
        point.setX(1.0);
        point.setY(2.0);
        point.setFunID(999L); // Non-existent funID

        Long id = dao.create(point);
        System.out.println("Created with invalid funID: " + id);
        // Should fail due to foreign key constraint
        assertNull(id, "Should not create with non-existent funID");
    }

    @Test
    public void testDuplicateXForSameFunID() {
        System.out.println("\n=== Test Duplicate X for Same FunID ===");

        CompPoint point1 = new CompPoint();
        point1.setX(3.14);
        point1.setY(2.71);
        point1.setFunID(1L);
        Long id1 = dao.create(point1);
        assertNotNull(id1);

        // Same X, same funID - should be allowed (no unique constraint in table)
        CompPoint point2 = new CompPoint();
        point2.setX(3.14); // Same X
        point2.setY(1.62); // Different Y
        point2.setFunID(1L); // Same funID
        Long id2 = dao.create(point2);

        // Should create successfully
        assertNotNull(id2, "Duplicate X for same funID should be allowed");
        assertNotEquals(id1, id2, "Should have different IDs");

        // Both should exist
        List<CompPoint> all = dao.findAll();
        assertEquals(2, all.size(), "Both points should exist");
    }

    @Test
    public void testNegativeCoordinates() {
        System.out.println("\n=== Test Negative Coordinates ===");

        CompPoint point = new CompPoint();
        point.setX(-2.5);
        point.setY(-3.7);
        point.setFunID(1L);

        Long id = dao.create(point);
        System.out.println("Created with negative coordinates: " + id);
        assertNotNull(id, "Should create with negative coordinates");

        Optional<CompPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(-2.5, found.get().getX(), 0.001);
        assertEquals(-3.7, found.get().getY(), 0.001);
    }

    @Test
    public void testZeroCoordinates() {
        System.out.println("\n=== Test Zero Coordinates ===");

        CompPoint point = new CompPoint();
        point.setX(0.0);
        point.setY(0.0);
        point.setFunID(1L);

        Long id = dao.create(point);
        System.out.println("Created with zero coordinates: " + id);
        assertNotNull(id, "Should create with zero coordinates");

        Optional<CompPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(0.0, found.get().getX(), 0.001);
        assertEquals(0.0, found.get().getY(), 0.001);
    }

    @Test
    public void testFindNonExistent() {
        System.out.println("\n=== Test Find Non-Existent ===");

        Optional<CompPoint> found = dao.findByID(999999L);
        System.out.println("Non-existent ID: " + found.isPresent());
        assertFalse(found.isPresent(), "Non-existent ID should not be found");

        Optional<CompPoint> foundByX = dao.findByXAndFunID(999.0, 1L);
        System.out.println("Non-existent X: " + foundByX.isPresent());
        assertFalse(foundByX.isPresent(), "Non-existent X should not be found");
    }

    @Test
    public void testDeleteNonExistent() {
        System.out.println("\n=== Test Delete Non-Existent ===");

        boolean deleted = dao.delete(999999L);
        System.out.println("Deleted non-existent: " + deleted);
        assertFalse(deleted, "Deleting non-existent point should return false");
    }

    @Test
    public void testEmptyDatabase() {
        System.out.println("\n=== Test Empty Database ===");

        List<CompPoint> all = dao.findAll();
        assertTrue(all.isEmpty(), "List should be empty");

        List<CompPoint> sorted = dao.sortedByX("ASC");
        assertTrue(sorted.isEmpty(), "Sorted list should be empty");
    }

    @Test
    public void testLargeCoordinates() {
        System.out.println("\n=== Test Large Coordinates ===");

        CompPoint point = new CompPoint();
        point.setX(1.0e6); // Large X
        point.setY(-1.0e6); // Large negative Y
        point.setFunID(1L);

        Long id = dao.create(point);
        System.out.println("Created with large coordinates: " + id);
        assertNotNull(id, "Should create with large coordinates");

        Optional<CompPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(1.0e6, found.get().getX(), 0.001);
        assertEquals(-1.0e6, found.get().getY(), 0.001);
    }

    @Test
    public void testMultipleFunctions() {
        System.out.println("\n=== Test Multiple Functions ===");

        // Create points for all 3 composite functions
        for (long funID = 1L; funID <= 3L; funID++) {
            for (int i = 0; i < 2; i++) { // 2 points per function
                CompPoint point = new CompPoint();
                point.setX(funID * 10 + i); // Different X for each
                point.setY(funID * 20 + i);
                point.setFunID(funID);

                Long id = dao.create(point);
                assertNotNull(id, "Point for funID " + funID + " should be created");
            }
        }

        List<CompPoint> all = dao.findAll();
        assertEquals(6, all.size(), "Should be 6 points total");

        // Count points per function
        long countFun1 = all.stream().filter(p -> p.getFunID() == 1L).count();
        long countFun2 = all.stream().filter(p -> p.getFunID() == 2L).count();
        long countFun3 = all.stream().filter(p -> p.getFunID() == 3L).count();

        assertEquals(2, countFun1, "Function 1 should have 2 points");
        assertEquals(2, countFun2, "Function 2 should have 2 points");
        assertEquals(2, countFun3, "Function 3 should have 2 points");
    }

    @Test
    public void testSamePointDifferentFunctions() {
        System.out.println("\n=== Test Same Point Different Functions ===");

        // Same coordinates, different functions
        CompPoint point1 = new CompPoint();
        point1.setX(5.0);
        point1.setY(10.0);
        point1.setFunID(1L);

        CompPoint point2 = new CompPoint();
        point2.setX(5.0); // Same X
        point2.setY(10.0); // Same Y
        point2.setFunID(2L); // Different function

        Long id1 = dao.create(point1);
        Long id2 = dao.create(point2);

        System.out.println("Same point for funID 1, ID: " + id1);
        System.out.println("Same point for funID 2, ID: " + id2);

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2, "Should have different IDs");
        assertEquals(2, dao.findAll().size(), "Both points should exist");
    }
}
