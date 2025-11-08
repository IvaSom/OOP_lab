package ru.ssau.tk.swc.labs;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import static org.junit.jupiter.api.Assertions.*;

class PostgreSQLConnectionTest {

    @Test
    void testDatabaseConnection() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "123456";

        System.out.println("Тестируем подключение к PostgreSQL...");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            assertNotNull(connection, "Подключение не должно быть null");
            assertTrue(connection.isValid(2), "Подключение должно быть валидным");
            System.out.println("Подключение к PostgreSQL работает");

        } catch (Exception e) {
            fail("Не удалось подключиться к PostgreSQL: " + e.getMessage());
        }
    }
}
