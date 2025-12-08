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
        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + DB_NAME + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "")) {

            try (Statement stmt = setupConn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS comp_points CASCADE");
                stmt.execute("DROP TABLE IF EXISTS compFun CASCADE");

                stmt.execute("""
                    CREATE TABLE compFun (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
                    );
                """);

                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite Function 1')");
                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite Function 2')");
                stmt.execute("INSERT INTO compFun (name) VALUES ('Composite Function 3')");

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
        CompPoint point = new CompPoint();
        point.setX(2.5);
        point.setY(3.7);
        point.setFunID(1L);

        Long id = dao.create(point);
        assertNotNull(id);

        Optional<CompPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(2.5, found.get().getX(), 0.001);
        assertEquals(3.7, found.get().getY(), 0.001);
        assertEquals(1L, found.get().getFunID());

        Optional<CompPoint> foundByXAndFunID = dao.findByXAndFunID(2.5, 1L);
        assertTrue(foundByXAndFunID.isPresent());

        boolean deleted = dao.delete(id);
        assertTrue(deleted);

        Optional<CompPoint> notFound = dao.findByID(id);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFindAll() {
        for (int i = 0; i < 4; i++) {
            CompPoint point = new CompPoint();
            point.setX(i * 1.5);
            point.setY(i * 2.2);
            point.setFunID((i % 3) + 1L);

            Long id = dao.create(point);
            assertNotNull(id);
        }

        List<CompPoint> all = dao.findAll();
        assertEquals(4, all.size());
    }

    @Test
    public void testFindByXAndFunID() {
        CompPoint point1 = new CompPoint();
        point1.setX(5.0);
        point1.setY(10.0);
        point1.setFunID(1L);
        Long id1 = dao.create(point1);
        assertNotNull(id1);

        CompPoint point2 = new CompPoint();
        point2.setX(5.0);
        point2.setY(15.0);
        point2.setFunID(2L);
        Long id2 = dao.create(point2);
        assertNotNull(id2);

        Optional<CompPoint> found1 = dao.findByXAndFunID(5.0, 1L);
        assertTrue(found1.isPresent());
        assertEquals(10.0, found1.get().getY(), 0.001);

        Optional<CompPoint> found2 = dao.findByXAndFunID(5.0, 2L);
        assertTrue(found2.isPresent());
        assertEquals(15.0, found2.get().getY(), 0.001);

        Optional<CompPoint> notFound = dao.findByXAndFunID(5.0, 999L);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testSortedByX() {
        double[] xValues = {7.0, 2.0, 5.0, 1.0};
        for (double x : xValues) {
            CompPoint point = new CompPoint();
            point.setX(x);
            point.setY(x * 3);
            point.setFunID(1L);
            dao.create(point);
        }

        List<CompPoint> asc = dao.sortedByX("ASC");
        assertEquals(4, asc.size());
        assertEquals(1.0, asc.get(0).getX(), 0.001);
        assertEquals(2.0, asc.get(1).getX(), 0.001);
        assertEquals(5.0, asc.get(2).getX(), 0.001);
        assertEquals(7.0, asc.get(3).getX(), 0.001);

        List<CompPoint> desc = dao.sortedByX("DESC");
        assertEquals(4, desc.size());
        assertEquals(7.0, desc.get(0).getX(), 0.001);
        assertEquals(5.0, desc.get(1).getX(), 0.001);
        assertEquals(2.0, desc.get(2).getX(), 0.001);
        assertEquals(1.0, desc.get(3).getX(), 0.001);

        List<CompPoint> def = dao.sortedByX("INVALID");
        assertEquals(4, def.size());
        assertEquals(1.0, def.get(0).getX(), 0.001);
    }

    @Test
    public void testSortedByFunID() {
        long[] funIDs = {3L, 1L, 2L, 3L, 1L};
        for (int i = 0; i < funIDs.length; i++) {
            CompPoint point = new CompPoint();
            point.setX(i * 1.1);
            point.setY(i * 2.2);
            point.setFunID(funIDs[i]);
            dao.create(point);
        }

        List<CompPoint> asc = dao.sortedByFunID("ASC");
        assertEquals(5, asc.size());
        assertEquals(1L, asc.get(0).getFunID());
        assertEquals(1L, asc.get(1).getFunID());
        assertEquals(2L, asc.get(2).getFunID());
        assertEquals(3L, asc.get(3).getFunID());
        assertEquals(3L, asc.get(4).getFunID());

        List<CompPoint> desc = dao.sortedByFunID("DESC");
        assertEquals(5, desc.size());
        assertEquals(3L, desc.get(0).getFunID());
        assertEquals(3L, desc.get(1).getFunID());
        assertEquals(2L, desc.get(2).getFunID());
        assertEquals(1L, desc.get(3).getFunID());
        assertEquals(1L, desc.get(4).getFunID());
    }

    @Test
    public void testSortedByID() {
        for (int i = 0; i < 4; i++) {
            CompPoint point = new CompPoint();
            point.setX(i * 2.0);
            point.setY(i * 3.0);
            point.setFunID(1L);
            dao.create(point);
        }

        List<CompPoint> asc = dao.sortedByID("ASC");
        assertEquals(4, asc.size());
        for (int i = 0; i < 3; i++) {
            assertTrue(asc.get(i).getId() < asc.get(i + 1).getId());
        }

        List<CompPoint> desc = dao.sortedByID("DESC");
        assertEquals(4, desc.size());
        for (int i = 0; i < 3; i++) {
            assertTrue(desc.get(i).getId() > desc.get(i + 1).getId());
        }
    }

    @Test
    public void testForeignKeyConstraint() {
        CompPoint point = new CompPoint();
        point.setX(1.0);
        point.setY(2.0);
        point.setFunID(999L);

        Long id = dao.create(point);
        assertNull(id);
    }

    @Test
    public void testDuplicateXForSameFunID() {
        CompPoint point1 = new CompPoint();
        point1.setX(3.14);
        point1.setY(2.71);
        point1.setFunID(1L);
        Long id1 = dao.create(point1);
        assertNotNull(id1);

        CompPoint point2 = new CompPoint();
        point2.setX(3.14);
        point2.setY(1.62);
        point2.setFunID(1L);
        Long id2 = dao.create(point2);

        assertNotNull(id2);
        assertNotEquals(id1, id2);

        List<CompPoint> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testNegativeCoordinates() {
        CompPoint point = new CompPoint();
        point.setX(-2.5);
        point.setY(-3.7);
        point.setFunID(1L);

        Long id = dao.create(point);
        assertNotNull(id);

        Optional<CompPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(-2.5, found.get().getX(), 0.001);
        assertEquals(-3.7, found.get().getY(), 0.001);
    }

    @Test
    public void testZeroCoordinates() {
        CompPoint point = new CompPoint();
        point.setX(0.0);
        point.setY(0.0);
        point.setFunID(1L);

        Long id = dao.create(point);
        assertNotNull(id);

        Optional<CompPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(0.0, found.get().getX(), 0.001);
        assertEquals(0.0, found.get().getY(), 0.001);
    }

    @Test
    public void testFindNonExistent() {
        Optional<CompPoint> found = dao.findByID(999999L);
        assertFalse(found.isPresent());

        Optional<CompPoint> foundByX = dao.findByXAndFunID(999.0, 1L);
        assertFalse(foundByX.isPresent());
    }

    @Test
    public void testDeleteNonExistent() {
        boolean deleted = dao.delete(999999L);
        assertFalse(deleted);
    }

    @Test
    public void testEmptyDatabase() {
        List<CompPoint> all = dao.findAll();
        assertTrue(all.isEmpty());

        List<CompPoint> sorted = dao.sortedByX("ASC");
        assertTrue(sorted.isEmpty());
    }

    @Test
    public void testLargeCoordinates() {
        CompPoint point = new CompPoint();
        point.setX(1.0e6);
        point.setY(-1.0e6);
        point.setFunID(1L);

        Long id = dao.create(point);
        assertNotNull(id);

        Optional<CompPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(1.0e6, found.get().getX(), 0.001);
        assertEquals(-1.0e6, found.get().getY(), 0.001);
    }

    @Test
    public void testMultipleFunctions() {
        for (long funID = 1L; funID <= 3L; funID++) {
            for (int i = 0; i < 2; i++) {
                CompPoint point = new CompPoint();
                point.setX(funID * 10 + i);
                point.setY(funID * 20 + i);
                point.setFunID(funID);

                Long id = dao.create(point);
                assertNotNull(id);
            }
        }

        List<CompPoint> all = dao.findAll();
        assertEquals(6, all.size());

        long countFun1 = all.stream().filter(p -> p.getFunID() == 1L).count();
        long countFun2 = all.stream().filter(p -> p.getFunID() == 2L).count();
        long countFun3 = all.stream().filter(p -> p.getFunID() == 3L).count();

        assertEquals(2, countFun1);
        assertEquals(2, countFun2);
        assertEquals(2, countFun3);
    }

    @Test
    public void testSamePointDifferentFunctions() {
        CompPoint point1 = new CompPoint();
        point1.setX(5.0);
        point1.setY(10.0);
        point1.setFunID(1L);

        CompPoint point2 = new CompPoint();
        point2.setX(5.0);
        point2.setY(10.0);
        point2.setFunID(2L);

        Long id1 = dao.create(point1);
        Long id2 = dao.create(point2);

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
        assertEquals(2, dao.findAll().size());
    }
}