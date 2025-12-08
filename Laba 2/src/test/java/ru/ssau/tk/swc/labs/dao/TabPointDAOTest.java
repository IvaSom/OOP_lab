package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.TabPoint;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TabPointDAOTest {
    private TabPointDAO dao;
    private String dbName;

    private Long testFunId1;
    private Long testFunId2;

    @BeforeEach
    public void setup() throws Exception {
        dbName = "testdb_" + System.currentTimeMillis() + "_" + Math.random();

        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "");
             Statement stmt = setupConn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS tab_points");
            stmt.execute("DROP TABLE IF EXISTS tabFun");

            stmt.execute("""
                        CREATE TABLE tabFun (
                            id BIGSERIAL PRIMARY KEY,
                            type VARCHAR(50) NOT NULL
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE tab_points (
                            id BIGSERIAL PRIMARY KEY,
                            x DOUBLE PRECISION NOT NULL,
                            y DOUBLE PRECISION NOT NULL,
                            derive DOUBLE PRECISION NOT NULL,
                            funID BIGINT NOT NULL,
                            CONSTRAINT funID_fk FOREIGN KEY (funID) REFERENCES tabFun (id)
                        );
                    """);

            stmt.executeUpdate("INSERT INTO tabFun (type) VALUES ('Функция 1')");
            stmt.executeUpdate("INSERT INTO tabFun (type) VALUES ('Функция 2')");

            try (ResultSet rs = stmt.executeQuery("SELECT id FROM tabFun ORDER BY id")) {
                if (rs.next()) testFunId1 = rs.getLong("id");
                if (rs.next()) testFunId2 = rs.getLong("id");
            }
        }

        dao = new TabPointDAO(new TestDataSourceProvider());
    }

    private class TestDataSourceProvider implements DataSourceProvider {
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
        TabPoint point = new TabPoint();
        point.setX(1.5);
        point.setY(2.3);
        point.setDerive(0.8);
        point.setFunID(testFunId1);

        Long id = dao.create(point);
        assertNotNull(id);

        Optional<TabPoint> found = dao.findByID(id);
        assertTrue(found.isPresent());
        assertEquals(1.5, found.get().getX(), 0.001);
        assertEquals(2.3, found.get().getY(), 0.001);
        assertEquals(0.8, found.get().getDerive(), 0.001);
        assertEquals(testFunId1, found.get().getFunID());

        Optional<TabPoint> foundByXAndFunID = dao.findByXAndFunID(1.5, testFunId1);
        assertTrue(foundByXAndFunID.isPresent());

        boolean deleted = dao.delete(id);
        assertTrue(deleted);

        Optional<TabPoint> notFound = dao.findByID(id);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFindAll() {
        for (int i = 1; i <= 3; i++) {
            TabPoint point = new TabPoint();
            point.setX(i * 1.0);
            point.setY(i * 2.0);
            point.setDerive(i * 0.5);
            point.setFunID(testFunId1);

            Long id = dao.create(point);
            assertNotNull(id);
        }

        List<TabPoint> all = dao.findAll();
        assertEquals(3, all.size());
    }

    @Test
    public void testCreateWithNegativeValues() {
        TabPoint point = new TabPoint();
        point.setX(-1.5);
        point.setY(-2.3);
        point.setDerive(-0.8);
        point.setFunID(testFunId1);

        Long id = dao.create(point);

        if (id != null) {
            Optional<TabPoint> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals(-1.5, found.get().getX(), 0.001);
            assertEquals(-2.3, found.get().getY(), 0.001);
            assertEquals(-0.8, found.get().getDerive(), 0.001);
        }
    }

    @Test
    public void testCreateWithZeroValues() {
        TabPoint point = new TabPoint();
        point.setX(0.0);
        point.setY(0.0);
        point.setDerive(0.0);
        point.setFunID(testFunId1);

        Long id = dao.create(point);

        if (id != null) {
            Optional<TabPoint> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals(0.0, found.get().getX(), 0.001);
            assertEquals(0.0, found.get().getY(), 0.001);
            assertEquals(0.0, found.get().getDerive(), 0.001);
        }
    }

    @Test
    public void testMultiplePointsSameFunID() {
        for (int i = 0; i < 4; i++) {
            TabPoint point = new TabPoint();
            point.setX(i * 2.0);
            point.setY(i * 3.0);
            point.setDerive(i * 0.5);
            point.setFunID(testFunId1);

            Long id = dao.create(point);
            assertNotNull(id);
        }

        List<TabPoint> all = dao.findAll();
        assertEquals(4, all.size());

        for (TabPoint point : all) {
            assertEquals(testFunId1, point.getFunID());
        }
    }

    @Test
    public void testPointsWithDifferentFunIDs() {
        TabPoint point1 = new TabPoint();
        point1.setX(1.0);
        point1.setY(2.0);
        point1.setDerive(0.5);
        point1.setFunID(testFunId1);

        TabPoint point2 = new TabPoint();
        point2.setX(3.0);
        point2.setY(4.0);
        point2.setDerive(1.0);
        point2.setFunID(testFunId2);

        Long id1 = dao.create(point1);
        Long id2 = dao.create(point2);

        assertNotNull(id1);
        assertNotNull(id2);

        Optional<TabPoint> found1 = dao.findByID(id1);
        Optional<TabPoint> found2 = dao.findByID(id2);

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertNotEquals(found1.get().getFunID(), found2.get().getFunID());
    }

    @Test
    public void testFindByXAndFunID() {
        TabPoint point1 = new TabPoint();
        point1.setX(5.0);
        point1.setY(10.0);
        point1.setDerive(2.0);
        point1.setFunID(testFunId1);

        TabPoint point2 = new TabPoint();
        point2.setX(5.0);
        point2.setY(20.0);
        point2.setDerive(3.0);
        point2.setFunID(testFunId2);

        Long id1 = dao.create(point1);
        Long id2 = dao.create(point2);

        Optional<TabPoint> found1 = dao.findByXAndFunID(5.0, testFunId1);
        assertTrue(found1.isPresent());
        assertEquals(id1, found1.get().getId());

        Optional<TabPoint> found2 = dao.findByXAndFunID(5.0, testFunId2);
        assertTrue(found2.isPresent());
        assertEquals(id2, found2.get().getId());

        Optional<TabPoint> notFound = dao.findByXAndFunID(5.0, 999999L);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testSortedByX() {
        double[] xValues = {3.0, 1.0, 4.0, 2.0};

        for (double x : xValues) {
            TabPoint point = new TabPoint();
            point.setX(x);
            point.setY(x * 2);
            point.setDerive(x * 0.5);
            point.setFunID(testFunId1);
            dao.create(point);
        }

        List<TabPoint> ascSorted = dao.sortedByX("ASC");
        for (int i = 1; i < ascSorted.size(); i++) {
            assertTrue(ascSorted.get(i).getX() >= ascSorted.get(i-1).getX());
        }

        List<TabPoint> descSorted = dao.sortedByX("DESC");
        for (int i = 1; i < descSorted.size(); i++) {
            assertTrue(descSorted.get(i).getX() <= descSorted.get(i-1).getX());
        }

        List<TabPoint> defaultSorted = dao.sortedByX("INVALID");
        assertEquals(ascSorted.size(), defaultSorted.size());
    }

    @Test
    public void testSortedByFunID() {
        Long[] funIDs = {testFunId2, testFunId1, testFunId2, testFunId1};

        for (int i = 0; i < funIDs.length; i++) {
            TabPoint point = new TabPoint();
            point.setX(i * 1.0);
            point.setY(i * 2.0);
            point.setDerive(i * 0.5);
            point.setFunID(funIDs[i]);
            dao.create(point);
        }

        List<TabPoint> ascSorted = dao.sortedByFunID("ASC");
        for (int i = 1; i < ascSorted.size(); i++) {
            assertTrue(ascSorted.get(i).getFunID() >= ascSorted.get(i-1).getFunID());
        }

        List<TabPoint> descSorted = dao.sortedByFunID("DESC");
        for (int i = 1; i < descSorted.size(); i++) {
            assertTrue(descSorted.get(i).getFunID() <= descSorted.get(i-1).getFunID());
        }
    }

    @Test
    public void testSortedByID() {
        for (int i = 0; i < 5; i++) {
            TabPoint point = new TabPoint();
            point.setX(i * 2.0);
            point.setY(i * 3.0);
            point.setDerive(i * 0.5);
            point.setFunID(testFunId1);
            dao.create(point);
        }

        List<TabPoint> ascSorted = dao.sortedByID("ASC");
        for (int i = 1; i < ascSorted.size(); i++) {
            assertTrue(ascSorted.get(i).getId() > ascSorted.get(i-1).getId());
        }

        List<TabPoint> descSorted = dao.sortedByID("DESC");
        for (int i = 1; i < descSorted.size(); i++) {
            assertTrue(descSorted.get(i).getId() < descSorted.get(i-1).getId());
        }
    }

    @Test
    public void testDeleteNonExistent() {
        boolean deleted = dao.delete(999999L);
        assertFalse(deleted);
    }

    @Test
    public void testFindNonExistent() {
        Optional<TabPoint> foundById = dao.findByID(999999L);
        assertFalse(foundById.isPresent());

        Optional<TabPoint> foundByXAndFunID = dao.findByXAndFunID(999.0, 999999L);
        assertFalse(foundByXAndFunID.isPresent());
    }

    @Test
    public void testEmptyFindAll() {
        List<TabPoint> all = dao.findAll();
        assertTrue(all.isEmpty());
        assertEquals(0, all.size());
    }

    @Test
    public void testLargeValues() {
        TabPoint point = new TabPoint();
        point.setX(Double.MAX_VALUE);
        point.setY(Double.MIN_NORMAL);
        point.setDerive(1.7976931348623157E308);
        point.setFunID(testFunId1);

        Long id = dao.create(point);

        if (id != null) {
            Optional<TabPoint> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals(Double.MAX_VALUE, found.get().getX(), 0.001);
            assertEquals(Double.MIN_NORMAL, found.get().getY(), 0.001);
        }
    }

    @Test
    public void testFindByIdAfterMultipleOperations() {
        TabPoint[] points = new TabPoint[5];
        Long[] ids = new Long[5];

        for (int i = 0; i < 5; i++) {
            points[i] = new TabPoint();
            points[i].setX(i * 1.5);
            points[i].setY(i * 2.5);
            points[i].setDerive(i * 0.3);
            points[i].setFunID(i % 2 == 0 ? testFunId1 : testFunId2);

            ids[i] = dao.create(points[i]);
            assertNotNull(ids[i]);
        }

        for (int i = 0; i < 5; i += 2) {
            boolean deleted = dao.delete(ids[i]);
            assertTrue(deleted);
        }

        for (int i = 0; i < 5; i++) {
            Optional<TabPoint> found = dao.findByID(ids[i]);
            if (i % 2 == 0) {
                assertFalse(found.isPresent());
            } else {
                assertTrue(found.isPresent());
                assertEquals(points[i].getX(), found.get().getX(), 0.001);
                assertEquals(points[i].getFunID(), found.get().getFunID());
            }
        }
    }
}