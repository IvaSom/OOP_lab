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
        // Полная очистка и пересоздание БД для каждого теста
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

        // DAO с провайдером, создающим новые соединения
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
        System.out.println("=== Test Basic CRUD ===");

        // CREATE
        CompFun fun = new CompFun();
        fun.setName("Composite Function 1");

        Long id = dao.create(fun);
        System.out.println("Created composite function with ID: " + id);
        assertNotNull(id, "ID should not be null");

        // READ by ID
        Optional<CompFun> found = dao.findByID(id);
        System.out.println("Found by ID: " + found.isPresent());
        assertTrue(found.isPresent(), "Function should be found by ID");
        assertEquals("Composite Function 1", found.get().getName());

        // READ by Name
        Optional<CompFun> foundByName = dao.findByName("Composite Function 1");
        System.out.println("Found by name: " + foundByName.isPresent());
        assertTrue(foundByName.isPresent(), "Function should be found by name");

        // DELETE
        boolean deleted = dao.delete(id);
        System.out.println("Deleted: " + deleted);
        assertTrue(deleted, "Function should be deleted");

        Optional<CompFun> notFound = dao.findByID(id);
        System.out.println("After deletion: " + notFound.isPresent());
        assertFalse(notFound.isPresent(), "Function should not be found after deletion");
    }

    @Test
    public void testFindAll() {
        System.out.println("\n=== Test FindAll ===");

        // Create multiple functions
        for (int i = 1; i <= 3; i++) {
            CompFun fun = new CompFun();
            fun.setName("Function " + i);
            Long id = dao.create(fun);
            assertNotNull(id, "ID should not be null for function " + i);
            System.out.println("Created function " + i + " with ID: " + id);
        }

        List<CompFun> all = dao.findAll();
        System.out.println("Total functions: " + all.size());
        assertEquals(3, all.size(), "Should be 3 functions");

        // Check sorting by name
        assertEquals("Function 1", all.get(0).getName());
        assertEquals("Function 2", all.get(1).getName());
        assertEquals("Function 3", all.get(2).getName());
    }

    @Test
    public void testFindNonExistent() {
        System.out.println("\n=== Test Find Non-Existent ===");

        Optional<CompFun> found = dao.findByID(999999L);
        System.out.println("Non-existent ID: " + found.isPresent());
        assertFalse(found.isPresent(), "Non-existent ID should not be found");

        Optional<CompFun> foundByName = dao.findByName("Non-existent Function");
        System.out.println("Non-existent name: " + foundByName.isPresent());
        assertFalse(foundByName.isPresent(), "Non-existent name should not be found");
    }

    @Test
    public void testDeleteNonExistent() {
        System.out.println("\n=== Test Delete Non-Existent ===");

        boolean deleted = dao.delete(999999L);
        System.out.println("Deleted non-existent: " + deleted);
        assertFalse(deleted, "Deleting non-existent function should return false");
    }

    @Test
    public void testDuplicateNames() {
        System.out.println("\n=== Test Duplicate Names ===");

        CompFun fun1 = new CompFun();
        fun1.setName("Same Name");
        Long id1 = dao.create(fun1);
        System.out.println("First function with ID: " + id1);
        assertNotNull(id1, "First function should be created");

        // Try to create another function with the same name
        CompFun fun2 = new CompFun();
        fun2.setName("Same Name"); // Same name!
        Long id2 = dao.create(fun2);
        System.out.println("Second function with same name, ID: " + id2);

        // In H2 without UNIQUE constraint, this might succeed
        // Check behavior
        if (id2 != null) {
            System.out.println("Duplicate name allowed - both functions created");
            assertNotEquals(id1, id2, "IDs should be different");
            assertEquals(2, dao.findAll().size(), "Both functions should exist");
        } else {
            System.out.println("Duplicate name rejected - only first function exists");
            assertEquals(1, dao.findAll().size(), "Only first function should exist");
        }
    }

    @Test
    public void testEmptyName() {
        System.out.println("\n=== Test Empty Name ===");

        CompFun fun = new CompFun();
        fun.setName(""); // Empty string
        Long id = dao.create(fun);
        System.out.println("Function with empty name created with ID: " + id);

        // Should be allowed (empty string is not NULL)
        if (id != null) {
            assertNotNull(id);
            Optional<CompFun> found = dao.findByID(id);
            assertTrue(found.isPresent());
            assertEquals("", found.get().getName());
        }
    }

    @Test
    public void testNullName() {
        System.out.println("\n=== Test Null Name ===");

        CompFun fun = new CompFun();
        fun.setName(null); // Null name
        Long id = dao.create(fun);
        System.out.println("Function with null name created with ID: " + id);

        // Should fail (NOT NULL constraint in DB)
        assertNull(id, "Function with null name should not be created");
    }

    @Test
    public void testVeryLongName() {
        System.out.println("\n=== Test Very Long Name ===");

        CompFun fun = new CompFun();
        String longName = "Very very very very very very very very very long function name";
        fun.setName(longName);
        Long id = dao.create(fun);
        System.out.println("Function with long name created with ID: " + id);

        if (id != null) {
            assertNotNull(id);
            Optional<CompFun> found = dao.findByID(id);
            assertTrue(found.isPresent());
            // Name might be truncated
            assertTrue(found.get().getName().length() <= 50,
                    "Name should be truncated to 50 characters");
        }
    }

    @Test
    public void testMultipleOperations() {
        System.out.println("\n=== Test Multiple Operations ===");

        // Create multiple functions
        Long[] ids = new Long[5];
        for (int i = 0; i < 5; i++) {
            CompFun fun = new CompFun();
            fun.setName("Function " + (i + 1));
            ids[i] = dao.create(fun);
            assertNotNull(ids[i], "Function " + (i + 1) + " should be created");
        }

        // Delete every second function
        for (int i = 0; i < 5; i += 2) {
            boolean deleted = dao.delete(ids[i]);
            assertTrue(deleted, "Function " + (i + 1) + " should be deleted");
        }

        // Verify remaining functions
        List<CompFun> remaining = dao.findAll();
        assertEquals(2, remaining.size(), "Should be 3 functions remaining");

        // Check specific remaining functions
        Optional<CompFun> found2 = dao.findByID(ids[1]);
        assertTrue(found2.isPresent(), "Function 2 should exist");

        Optional<CompFun> found4 = dao.findByID(ids[3]);
        assertTrue(found4.isPresent(), "Function 4 should exist");
    }

    @Test
    public void testFindByNameCaseSensitive() {
        System.out.println("\n=== Test Find By Name Case Sensitive ===");

        CompFun fun = new CompFun();
        fun.setName("MixedCaseFunction");
        Long id = dao.create(fun);
        assertNotNull(id);

        // Exact match
        Optional<CompFun> exact = dao.findByName("MixedCaseFunction");
        assertTrue(exact.isPresent(), "Exact match should be found");

        // Different case
        Optional<CompFun> lower = dao.findByName("mixedcasefunction");
        Optional<CompFun> upper = dao.findByName("MIXEDCASEFUNCTION");

        // Depends on DB collation settings
        System.out.println("Lowercase search found: " + lower.isPresent());
        System.out.println("Uppercase search found: " + upper.isPresent());
    }

    @Test
    public void testEmptyDatabase() {
        System.out.println("\n=== Test Empty Database ===");

        List<CompFun> all = dao.findAll();
        assertTrue(all.isEmpty(), "List should be empty");
        assertEquals(0, all.size(), "Size should be 0");
    }

    @Test
    public void testToString() {
        System.out.println("\n=== Test ToString ===");

        CompFun fun = new CompFun();
        fun.setName("Test Function");
        Long id = dao.create(fun);
        assertNotNull(id);

        Optional<CompFun> found = dao.findByID(id);
        assertTrue(found.isPresent());

        String toString = found.get().toString();
        System.out.println("toString: " + toString);
        assertTrue(toString.contains("Test Function"),
                "toString should contain function name");
        assertTrue(toString.contains(id.toString()),
                "toString should contain function ID");
    }
}
