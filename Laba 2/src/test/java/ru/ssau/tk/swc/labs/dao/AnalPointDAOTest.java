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

        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "")) {

            try (Statement stmt = setupConn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS anal_points");
                stmt.execute("DROP TABLE IF EXISTS analFun");

                stmt.execute("""
                CREATE TABLE analFun (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    type INTEGER
                );
            """);

                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Function 1', 1)");
                stmt.execute("INSERT INTO analFun (name, type) VALUES ('Function 2', 2)");

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
        AnalPoint point = new AnalPoint();
        point.setX(1.5);
        point.setY(2.3);
        point.setFunID(1L);

        Long id = dao.create(point);
        assertNotNull(id);

        Optional<AnalPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(1.5, found.get().getX(), 0.001);
        assertEquals(2.3, found.get().getY(), 0.001);
        assertEquals(1L, found.get().getFunID());

        Optional<AnalPoint> foundByXAndFunID = dao.findByXAndFunID(1.5, 1L);
        assertTrue(foundByXAndFunID.isPresent());

        boolean deleted = dao.delete(id);
        assertTrue(deleted);

        Optional<AnalPoint> notFound = dao.findByID(id);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFindAll() {
        for (int i = 0; i < 3; i++) {
            AnalPoint point = new AnalPoint();
            point.setX(i * 1.1);
            point.setY(i * 2.2);
            point.setFunID((i % 2) + 1L);

            Long id = dao.create(point);
            assertNotNull(id);
        }

        List<AnalPoint> all = dao.findAll();
        assertEquals(3, all.size());
    }

    @Test
    public void testFindByXAndFunID() {
        AnalPoint point1 = new AnalPoint();
        point1.setX(3.14);
        point1.setY(2.71);
        point1.setFunID(1L);
        Long id1 = dao.create(point1);
        assertNotNull(id1);

        AnalPoint point2 = new AnalPoint();
        point2.setX(3.14);
        point2.setY(1.62);
        point2.setFunID(2L);
        Long id2 = dao.create(point2);
        assertNotNull(id2);

        Optional<AnalPoint> found1 = dao.findByXAndFunID(3.14, 1L);
        assertTrue(found1.isPresent());
        assertEquals(2.71, found1.get().getY(), 0.001);

        Optional<AnalPoint> found2 = dao.findByXAndFunID(3.14, 2L);
        assertTrue(found2.isPresent());
        assertEquals(1.62, found2.get().getY(), 0.001);

        Optional<AnalPoint> notFound = dao.findByXAndFunID(3.14, 999L);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testSortedByX() {
        double[] xValues = {5.0, 1.0, 3.0};
        for (double x : xValues) {
            AnalPoint point = new AnalPoint();
            point.setX(x);
            point.setY(x * 2);
            point.setFunID(1L);
            dao.create(point);
        }

        List<AnalPoint> asc = dao.sortedByX("ASC");
        assertEquals(3, asc.size());
        assertEquals(1.0, asc.get(0).getX(), 0.001);
        assertEquals(3.0, asc.get(1).getX(), 0.001);
        assertEquals(5.0, asc.get(2).getX(), 0.001);

        List<AnalPoint> desc = dao.sortedByX("DESC");
        assertEquals(3, desc.size());
        assertEquals(5.0, desc.get(0).getX(), 0.001);
        assertEquals(3.0, desc.get(1).getX(), 0.001);
        assertEquals(1.0, desc.get(2).getX(), 0.001);

        List<AnalPoint> def = dao.sortedByX("INVALID");
        assertEquals(3, def.size());
        assertEquals(1.0, def.get(0).getX(), 0.001);
    }

    @Test
    public void testSortedByFunID() {
        long[] funIDs = {2L, 1L, 2L, 1L};
        for (long funID : funIDs) {
            AnalPoint point = new AnalPoint();
            point.setX(Math.random());
            point.setY(Math.random());
            point.setFunID(funID);
            dao.create(point);
        }

        List<AnalPoint> asc = dao.sortedByFunID("ASC");
        assertEquals(4, asc.size());
        assertEquals(1L, asc.get(0).getFunID());
        assertEquals(1L, asc.get(1).getFunID());
        assertEquals(2L, asc.get(2).getFunID());
        assertEquals(2L, asc.get(3).getFunID());

        List<AnalPoint> desc = dao.sortedByFunID("DESC");
        assertEquals(4, desc.size());
        assertEquals(2L, desc.get(0).getFunID());
        assertEquals(2L, desc.get(1).getFunID());
        assertEquals(1L, desc.get(2).getFunID());
        assertEquals(1L, desc.get(3).getFunID());
    }

    @Test
    public void testSortedByID() {
        for (int i = 0; i < 3; i++) {
            AnalPoint point = new AnalPoint();
            point.setX(i);
            point.setY(i * 2);
            point.setFunID(1L);
            dao.create(point);
        }

        List<AnalPoint> asc = dao.sortedByID("ASC");
        assertEquals(3, asc.size());
        assertTrue(asc.get(0).getId() < asc.get(1).getId());
        assertTrue(asc.get(1).getId() < asc.get(2).getId());

        List<AnalPoint> desc = dao.sortedByID("DESC");
        assertEquals(3, desc.size());
        assertTrue(desc.get(0).getId() > desc.get(1).getId());
        assertTrue(desc.get(1).getId() > desc.get(2).getId());
    }

    @Test
    public void testForeignConstraint() {
        AnalPoint point = new AnalPoint();
        point.setX(1.0);
        point.setY(2.0);
        point.setFunID(999L);

        Long id = dao.create(point);
    }

    @Test
    public void testDuplicateXForSameFunID() {
        AnalPoint point1 = new AnalPoint();
        point1.setX(2.5);
        point1.setY(3.0);
        point1.setFunID(1L);
        Long id1 = dao.create(point1);
        assertNotNull(id1);

        AnalPoint point2 = new AnalPoint();
        point2.setX(2.5);
        point2.setY(4.0);
        point2.setFunID(1L);
        Long id2 = dao.create(point2);

        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    public void testNegativeCoordinates() {
        AnalPoint point = new AnalPoint();
        point.setX(-1.5);
        point.setY(-2.3);
        point.setFunID(1L);

        Long id = dao.create(point);
        assertNotNull(id);

        Optional<AnalPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(-1.5, found.get().getX(), 0.001);
        assertEquals(-2.3, found.get().getY(), 0.001);
    }

    @Test
    public void testFindNonExistent() {
        Optional<AnalPoint> found = dao.findByID(999999L);
        assertFalse(found.isPresent());

        Optional<AnalPoint> foundByX = dao.findByXAndFunID(999.0, 1L);
        assertFalse(foundByX.isPresent());
    }

    @Test
    public void testEmptyDatabase() {
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