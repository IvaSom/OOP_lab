package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.AnalPoint;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class AnalPointDAOTest {
    private AnalPointDAO dao;
    private Connection connection;
    private static AtomicInteger dbCounter = new AtomicInteger(1000);
    private static String dbName;

    private static class TestDataSourceProvider implements DataSourceProvider {
        private final Connection connection;

        public TestDataSourceProvider(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return connection;
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        String dbName = "testdb_" + dbCounter.getAndIncrement();
        Class.forName("org.h2.Driver");

        // Создаем соединение только для setup
        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "")) {

            try (Statement stmt = setupConn.createStatement()) {
                // Создаем таблицу analFun для foreign key
                stmt.execute("DROP TABLE IF EXISTS anal_points");
                stmt.execute("DROP TABLE IF EXISTS analFun");

                stmt.execute("""
                CREATE TABLE analFun (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    type INTEGER
                );
            """);

                // Создаем тестовые функции
                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Function 1', 1)");
                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Function 2', 2)");

                // Создаем таблицу anal_points
                stmt.execute("""
                CREATE TABLE anal_points (
                    id BIGSERIAL PRIMARY KEY,
                    x DOUBLE PRECISION NOT NULL,
                    y DOUBLE PRECISION NOT NULL,
                    funID BIGINT NOT NULL,
                    CONSTRAINT funID_fk FOREIGN KEY (funID) REFERENCES analFun (id)
                );
            """);
            }
        }

        // DAO с провайдером, создающим новые соединения
        dao = new AnalPointDAO(() -> {
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
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
        AnalPoint point = new AnalPoint();
        point.setX(1.5);
        point.setY(2.3);
        point.setFunID(1L);

        Long id = dao.create(point);
        System.out.println("Created point with ID: " + id);
        assertNotNull(id, "ID should not be null");

        // READ by ID
        Optional<AnalPoint> found = dao.findByID(id);
        System.out.println("Found by ID: " + found.isPresent());
        assertTrue(found.isPresent());
        assertEquals(1.5, found.get().getX(), 0.001);
        assertEquals(2.3, found.get().getY(), 0.001);
        assertEquals(1L, found.get().getFunID());

        // READ by X and FunID
        Optional<AnalPoint> foundByXAndFunID = dao.findByXAndFunID(1.5, 1L);
        System.out.println("Found by X and FunID: " + foundByXAndFunID.isPresent());
        assertTrue(foundByXAndFunID.isPresent());

        // DELETE
        boolean deleted = dao.delete(id);
        System.out.println("Deleted: " + deleted);
        assertTrue(deleted);

        Optional<AnalPoint> notFound = dao.findByID(id);
        System.out.println("After deletion: " + notFound.isPresent());
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFindAll() {
        System.out.println("\n=== Test FindAll ===");

        // Create points for different functions
        for (int i = 0; i < 3; i++) {
            AnalPoint point = new AnalPoint();
            point.setX(i * 1.1);
            point.setY(i * 2.2);
            point.setFunID((i % 2) + 1L); // Alternate between function 1 and 2

            Long id = dao.create(point);
            assertNotNull(id);
            System.out.println("Created point " + (i+1) + " with ID: " + id);
        }

        List<AnalPoint> all = dao.findAll();
        System.out.println("Total points: " + all.size());
        assertEquals(3, all.size());
    }

    @Test
    public void testFindByXAndFunID() {
        System.out.println("\n=== Test FindByXAndFunID ===");

        // Create points with same X but different funID
        AnalPoint point1 = new AnalPoint();
        point1.setX(3.14);
        point1.setY(2.71);
        point1.setFunID(1L);
        Long id1 = dao.create(point1);
        assertNotNull(id1);

        AnalPoint point2 = new AnalPoint();
        point2.setX(3.14); // Same X
        point2.setY(1.62);
        point2.setFunID(2L); // Different funID
        Long id2 = dao.create(point2);
        assertNotNull(id2);

        // Should find point for funID 1
        Optional<AnalPoint> found1 = dao.findByXAndFunID(3.14, 1L);
        assertTrue(found1.isPresent());
        assertEquals(2.71, found1.get().getY(), 0.001);

        // Should find point for funID 2
        Optional<AnalPoint> found2 = dao.findByXAndFunID(3.14, 2L);
        assertTrue(found2.isPresent());
        assertEquals(1.62, found2.get().getY(), 0.001);

        // Should not find non-existent combination
        Optional<AnalPoint> notFound = dao.findByXAndFunID(3.14, 999L);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testSortedByX() {
        System.out.println("\n=== Test SortedByX ===");

        // Create points in reverse order
        double[] xValues = {5.0, 1.0, 3.0};
        for (double x : xValues) {
            AnalPoint point = new AnalPoint();
            point.setX(x);
            point.setY(x * 2);
            point.setFunID(1L);
            dao.create(point);
        }

        // ASC order
        List<AnalPoint> asc = dao.sortedByX("ASC");
        assertEquals(3, asc.size());
        assertEquals(1.0, asc.get(0).getX(), 0.001);
        assertEquals(3.0, asc.get(1).getX(), 0.001);
        assertEquals(5.0, asc.get(2).getX(), 0.001);

        // DESC order
        List<AnalPoint> desc = dao.sortedByX("DESC");
        assertEquals(3, desc.size());
        assertEquals(5.0, desc.get(0).getX(), 0.001);
        assertEquals(3.0, desc.get(1).getX(), 0.001);
        assertEquals(1.0, desc.get(2).getX(), 0.001);

        // Default (ASC) when invalid order
        List<AnalPoint> def = dao.sortedByX("INVALID");
        assertEquals(3, def.size());
        assertEquals(1.0, def.get(0).getX(), 0.001);
    }

    @Test
    public void testSortedByFunID() {
        System.out.println("\n=== Test SortedByFunID ===");

        // Create points with different funIDs
        long[] funIDs = {2L, 1L, 2L, 1L};
        for (long funID : funIDs) {
            AnalPoint point = new AnalPoint();
            point.setX(Math.random());
            point.setY(Math.random());
            point.setFunID(funID);
            dao.create(point);
        }

        // ASC order
        List<AnalPoint> asc = dao.sortedByFunID("ASC");
        assertEquals(4, asc.size());
        // First should be funID 1
        assertEquals(1L, asc.get(0).getFunID());
        assertEquals(1L, asc.get(1).getFunID());
        // Then funID 2
        assertEquals(2L, asc.get(2).getFunID());
        assertEquals(2L, asc.get(3).getFunID());

        // DESC order
        List<AnalPoint> desc = dao.sortedByFunID("DESC");
        assertEquals(4, desc.size());
        // First should be funID 2
        assertEquals(2L, desc.get(0).getFunID());
        assertEquals(2L, desc.get(1).getFunID());
        // Then funID 1
        assertEquals(1L, desc.get(2).getFunID());
        assertEquals(1L, desc.get(3).getFunID());
    }

    @Test
    public void testSortedByID() {
        System.out.println("\n=== Test SortedByID ===");

        // Create 3 points
        for (int i = 0; i < 3; i++) {
            AnalPoint point = new AnalPoint();
            point.setX(i);
            point.setY(i * 2);
            point.setFunID(1L);
            dao.create(point);
        }

        // ASC order (default)
        List<AnalPoint> asc = dao.sortedByID("ASC");
        assertEquals(3, asc.size());
        // IDs should be in increasing order
        assertTrue(asc.get(0).getId() < asc.get(1).getId());
        assertTrue(asc.get(1).getId() < asc.get(2).getId());

        // DESC order
        List<AnalPoint> desc = dao.sortedByID("DESC");
        assertEquals(3, desc.size());
        // IDs should be in decreasing order
        assertTrue(desc.get(0).getId() > desc.get(1).getId());
        assertTrue(desc.get(1).getId() > desc.get(2).getId());
    }

    @Test
    public void testForeignConstraint() {
        System.out.println("\n=== Test Foreign Constraint ===");

        // Try to create point with non-existent funID
        AnalPoint point = new AnalPoint();
        point.setX(1.0);
        point.setY(2.0);
        point.setFunID(999L); // Non-existent funID

        // Should handle error gracefully (return null or throw)
        Long id = dao.create(point);
        System.out.println("Created with invalid funID: " + id);
        // In H2 with FK constraint, this should fail
    }

    @Test
    public void testDuplicateXForSameFunID() {
        System.out.println("\n=== Test Duplicate X for Same FunID ===");

        AnalPoint point1 = new AnalPoint();
        point1.setX(2.5);
        point1.setY(3.0);
        point1.setFunID(1L);
        Long id1 = dao.create(point1);
        assertNotNull(id1);

        // Same X, same funID - should be allowed (no unique constraint in table)
        AnalPoint point2 = new AnalPoint();
        point2.setX(2.5);
        point2.setY(4.0);
        point2.setFunID(1L);
        Long id2 = dao.create(point2);

        // Should create successfully
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    public void testNegativeCoordinates() {
        System.out.println("\n=== Test Negative Coordinates ===");

        AnalPoint point = new AnalPoint();
        point.setX(-1.5);
        point.setY(-2.3);
        point.setFunID(1L);

        Long id = dao.create(point);
        System.out.println("Created with negative coordinates: " + id);
        assertNotNull(id);

        Optional<AnalPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(-1.5, found.get().getX(), 0.001);
        assertEquals(-2.3, found.get().getY(), 0.001);
    }

    @Test
    public void testFindNonExistent() {
        System.out.println("\n=== Test Find Non-Existent ===");

        Optional<AnalPoint> found = dao.findByID(999999L);
        assertFalse(found.isPresent());

        Optional<AnalPoint> foundByX = dao.findByXAndFunID(999.0, 1L);
        assertFalse(foundByX.isPresent());
    }

    @Test
    public void testEmptyDatabase() {
        System.out.println("\n=== Test Empty Database ===");

        List<AnalPoint> all = dao.findAll();
        assertTrue(all.isEmpty());

        List<AnalPoint> sorted = dao.sortedByX("ASC");
        assertTrue(sorted.isEmpty());
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
