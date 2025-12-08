package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.User;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO dao;
    private String dbName;

    @BeforeEach
    public void setup() throws Exception {
        dbName = "testdb_" + System.currentTimeMillis() + "_" + Math.random();

        try (Connection setupConn = DriverManager.getConnection(
                "jdbc:h2:mem:" + dbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "");
             Statement stmt = setupConn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute("""
                        CREATE TABLE users (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(50) NOT NULL,
                            login VARCHAR(50) UNIQUE NOT NULL,
                            email VARCHAR(100) UNIQUE NOT NULL,
                            password VARCHAR(100) NOT NULL
                        );
                    """);
        }

        dao = new UserDAO(new TestDataSourceProvider());
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
        System.out.println("=== Тест Basic CRUD для User ===");

        User user = new User();
        user.setName("Иван Иванов");
        user.setLogin("ivanov");
        user.setEmail("ivanov@example.com");
        user.setPassword("password123");

        Long id = dao.create(user);
        System.out.println("Создан пользователь с ID: " + id);
        assertNotNull(id, "ID не должен быть null");

        Optional<User> found = dao.findByID(id);
        System.out.println("Найден по ID: " + found.isPresent());
        assertTrue(found.isPresent(), "Пользователь должен быть найден по ID");
        assertEquals("Иван Иванов", found.get().getName());
        assertEquals("ivanov", found.get().getLogin());
        assertEquals("ivanov@example.com", found.get().getEmail());
        assertEquals("password123", found.get().getPassword());

        Optional<User> foundByLogin = dao.findByLogin("ivanov");
        System.out.println("Найден по логину: " + foundByLogin.isPresent());
        assertTrue(foundByLogin.isPresent(), "Пользователь должен быть найден по логину");

        Optional<User> foundByEmail = dao.findByEmail("ivanov@example.com");
        System.out.println("Найден по email: " + foundByEmail.isPresent());
        assertTrue(foundByEmail.isPresent(), "Пользователь должен быть найден по email");

        boolean deleted = dao.delete(id);
        System.out.println("Удален: " + deleted);
        assertTrue(deleted, "Пользователь должен быть удален");

        Optional<User> notFound = dao.findByID(id);
        System.out.println("После удаления: " + notFound.isPresent());
        assertFalse(notFound.isPresent(), "Пользователь не должен быть найден после удаления");
    }

    @Test
    public void testFindByLoginAndPassword() {
        System.out.println("\n=== Тест аутентификации для User ===");

        User user = new User();
        user.setName("Петр Петров");
        user.setLogin("petrov");
        user.setEmail("petrov@example.com");
        user.setPassword("securePass");

        Long id = dao.create(user);
        assertNotNull(id, "Пользователь должен быть создан");

        Optional<User> authenticated = dao.findByLoginAndPassword("petrov", "securePass");
        System.out.println("Аутентификация с правильными данными: " + authenticated.isPresent());
        assertTrue(authenticated.isPresent(), "Пользователь должен быть аутентифицирован");
        assertEquals(id, authenticated.get().getId());

        Optional<User> wrongPassword = dao.findByLoginAndPassword("petrov", "wrongPass");
        System.out.println("Аутентификация с неправильным паролем: " + wrongPassword.isPresent());
        assertFalse(wrongPassword.isPresent(), "Пользователь не должен быть аутентифицирован с неправильным паролем");

        Optional<User> wrongLogin = dao.findByLoginAndPassword("nonexistent", "securePass");
        System.out.println("Аутентификация с неправильным логином: " + wrongLogin.isPresent());
        assertFalse(wrongLogin.isPresent(), "Несуществующий пользователь не должен быть аутентифицирован");
    }

    @Test
    public void testUpdatePassword() {
        System.out.println("\n=== Тест обновления пароля для User ===");

        User user = new User();
        user.setName("Алексей Смирнов");
        user.setLogin("smirnov");
        user.setEmail("smirnov@example.com");
        user.setPassword("oldPassword");

        Long id = dao.create(user);
        assertNotNull(id, "Пользователь должен быть создан");
        user.setId(id);

        user.setPassword("newSecurePassword");
        boolean updated = dao.updatePassword(user);
        System.out.println("Пароль обновлен: " + updated);
        assertTrue(updated, "Пароль должен быть обновлен");

        Optional<User> authenticated = dao.findByLoginAndPassword("smirnov", "newSecurePassword");
        assertTrue(authenticated.isPresent(), "Должна работать аутентификация с новым паролем");

        Optional<User> oldPasswordAuth = dao.findByLoginAndPassword("smirnov", "oldPassword");
        assertFalse(oldPasswordAuth.isPresent(), "Старый пароль не должен работать");
    }

    @Test
    public void testUpdateEmail() {
        System.out.println("\n=== Тест обновления email для User ===");

        User user = new User();
        user.setName("Мария Иванова");
        user.setLogin("maria");
        user.setEmail("old@example.com");
        user.setPassword("password");

        Long id = dao.create(user);
        assertNotNull(id, "Пользователь должен быть создан");
        user.setId(id);

        user.setEmail("new@example.com");
        boolean updated = dao.updateEmail(user);
        System.out.println("Email обновлен: " + updated);
        assertTrue(updated, "Email должен быть обновлен");

        Optional<User> foundByNewEmail = dao.findByEmail("new@example.com");
        assertTrue(foundByNewEmail.isPresent(), "Пользователь должен быть найден по новому email");
        assertEquals(id, foundByNewEmail.get().getId());

        Optional<User> foundByOldEmail = dao.findByEmail("old@example.com");
        assertFalse(foundByOldEmail.isPresent(), "Пользователь не должен быть найден по старому email");
    }

    @Test
    public void testUpdateLogin() {
        System.out.println("\n=== Тест обновления логина для User ===");

        User user = new User();
        user.setName("Сергей Козлов");
        user.setLogin("oldlogin");
        user.setEmail("kozlov@example.com");
        user.setPassword("password");

        Long id = dao.create(user);
        assertNotNull(id, "Пользователь должен быть создан");
        user.setId(id);

        user.setLogin("newlogin");
        boolean updated = dao.updateLogin(user);
        System.out.println("Логин обновлен: " + updated);
        assertTrue(updated, "Логин должен быть обновлен");

        Optional<User> foundByNewLogin = dao.findByLogin("newlogin");
        assertTrue(foundByNewLogin.isPresent(), "Пользователь должен быть найден по новому логину");
        assertEquals(id, foundByNewLogin.get().getId());

        Optional<User> foundByOldLogin = dao.findByLogin("oldlogin");
        assertFalse(foundByOldLogin.isPresent(), "Пользователь не должен быть найден по старому логину");

        Optional<User> authenticated = dao.findByLoginAndPassword("newlogin", "password");
        assertTrue(authenticated.isPresent(), "Должна работать аутентификация с новым логином");
    }

    @Test
    public void testUpdateName() {
        System.out.println("\n=== Тест обновления имени для User ===");

        User user = new User();
        user.setName("Старое Имя");
        user.setLogin("user1");
        user.setEmail("user1@example.com");
        user.setPassword("password");

        Long id = dao.create(user);
        assertNotNull(id, "Пользователь должен быть создан");
        user.setId(id);

        user.setName("Новое Имя");
        boolean updated = dao.updateName(user);
        System.out.println("Имя обновлено: " + updated);
        assertTrue(updated, "Имя должно быть обновлено");

        Optional<User> found = dao.findByID(id);
        assertTrue(found.isPresent(), "Пользователь должен быть найден");
        assertEquals("Новое Имя", found.get().getName());
    }

    @Test
    public void testUniqueConstraints() {
        System.out.println("\n=== Тест уникальных ограничений для User ===");

        User user1 = new User();
        user1.setName("Первый Пользователь");
        user1.setLogin("unique1");
        user1.setEmail("unique1@example.com");
        user1.setPassword("pass1");

        Long id1 = dao.create(user1);
        assertNotNull(id1, "Первый пользователь должен быть создан");

        User user2 = new User();
        user2.setName("Второй Пользователь");
        user2.setLogin("unique1");
        user2.setEmail("unique2@example.com");
        user2.setPassword("pass2");

        Long id2 = dao.create(user2);
        System.out.println("Создание с дубликатом логина: " + id2);
        assertNull(id2, "Не должен быть создан пользователь с дублирующимся логином");

        User user3 = new User();
        user3.setName("Третий Пользователь");
        user3.setLogin("unique3");
        user3.setEmail("unique1@example.com");
        user3.setPassword("pass3");

        Long id3 = dao.create(user3);
        System.out.println("Создание с дубликатом email: " + id3);
        assertNull(id3, "Не должен быть создан пользователь с дублирующимся email");
    }

    @Test
    public void testFindAll() {
        System.out.println("\n=== Тест FindAll для User ===");

        String[] names = {"Александр", "Борис", "Вадим"};

        for (int i = 0; i < names.length; i++) {
            User user = new User();
            user.setName(names[i] + " " + "Фамилия");
            user.setLogin("user" + (i + 1));
            user.setEmail("user" + (i + 1) + "@example.com");
            user.setPassword("pass" + (i + 1));

            Long id = dao.create(user);
            assertNotNull(id, "Пользователь " + names[i] + " должен быть создан");
            System.out.println("Создан пользователь " + names[i] + " с ID: " + id);
        }

        List<User> all = dao.findAll();
        System.out.println("Всего пользователей: " + all.size());
        assertEquals(3, all.size(), "Должно быть 3 пользователя");

        System.out.println("Список пользователей (должен быть отсортирован по имени):");
        for (int i = 0; i < all.size(); i++) {
            System.out.println("  " + all.get(i).getName());
            if (i > 0) {
                assertTrue(all.get(i).getName().compareTo(all.get(i-1).getName()) >= 0,
                        "Пользователи должны быть отсортированы по имени");
            }
        }
    }

    @Test
    public void testCaseSensitiveLoginAndEmail() {
        System.out.println("\n=== Тест чувствительности к регистру для User ===");

        User user = new User();
        user.setName("Тестовый Пользователь");
        user.setLogin("TestUser");
        user.setEmail("Test@Example.com");
        user.setPassword("password");

        Long id = dao.create(user);
        assertNotNull(id, "Пользователь должен быть создан");

        Optional<User> foundLowerLogin = dao.findByLogin("testuser");
        Optional<User> foundUpperLogin = dao.findByLogin("TESTUSER");

        Optional<User> foundLowerEmail = dao.findByEmail("test@example.com");
        Optional<User> foundUpperEmail = dao.findByEmail("TEST@EXAMPLE.COM");

        System.out.println("Найден с login в нижнем регистре: " + foundLowerLogin.isPresent());
        System.out.println("Найден с login в верхнем регистре: " + foundUpperLogin.isPresent());
        System.out.println("Найден с email в нижнем регистре: " + foundLowerEmail.isPresent());
        System.out.println("Найден с email в верхнем регистре: " + foundUpperEmail.isPresent());
    }

    @Test
    public void testEmptyAndNullValues() {
        System.out.println("\n=== Тест пустых и null значений для User ===");

        User user1 = new User();
        user1.setName("");
        user1.setLogin("emptyuser");
        user1.setEmail("empty@example.com");
        user1.setPassword("pass");

        Long id1 = dao.create(user1);

        User user2 = new User();
        user2.setName("Очень очень очень очень очень длинное имя пользователя которое может быть обрезано");
        user2.setLogin("verylongloginname");
        user2.setEmail("verylongemailaddress@example.com");
        user2.setPassword("verylongpasswordthatexceedsnormallimitsbutshouldbestored");

        Long id2 = dao.create(user2);
        System.out.println("Создан пользователь с длинными значениями, ID: " + id2);

        if (id2 != null) {
            Optional<User> found = dao.findByID(id2);
            assertTrue(found.isPresent(), "Пользователь должен быть найден");
            assertTrue(found.get().getName().length() <= 50, "Имя должно быть обрезано до 50 символов");
            assertTrue(found.get().getLogin().length() <= 50, "Логин должен быть обрезан до 50 символов");
            assertTrue(found.get().getEmail().length() <= 100, "Email должен быть обрезан до 100 символов");
        }
    }

    @Test
    public void testFindNonExistent() {
        System.out.println("\n=== Тест поиска несуществующего для User ===");

        Optional<User> foundById = dao.findByID(999999L);
        System.out.println("Найден несуществующий ID: " + foundById.isPresent());
        assertFalse(foundById.isPresent(), "Несуществующий ID не должен быть найден");

        Optional<User> foundByLogin = dao.findByLogin("nonexistent");
        System.out.println("Найден несуществующий логин: " + foundByLogin.isPresent());
        assertFalse(foundByLogin.isPresent(), "Несуществующий логин не должен быть найден");

        Optional<User> foundByEmail = dao.findByEmail("nonexistent@example.com");
        System.out.println("Найден несуществующий email: " + foundByEmail.isPresent());
        assertFalse(foundByEmail.isPresent(), "Несуществующий email не должен быть найден");

        Optional<User> foundByAuth = dao.findByLoginAndPassword("nonexistent", "password");
        System.out.println("Аутентификация несуществующего: " + foundByAuth.isPresent());
        assertFalse(foundByAuth.isPresent(), "Несуществующий пользователь не должен быть аутентифицирован");
    }

    @Test
    public void testDeleteNonExistent() {
        System.out.println("\n=== Тест удаления несуществующего для User ===");

        boolean deleted = dao.delete(999999L);
        System.out.println("Удален несуществующий пользователь: " + deleted);
        assertFalse(deleted, "Удаление несуществующего пользователя должно вернуть false");
    }

    @Test
    public void testEmptyFindAll() {
        System.out.println("\n=== Тест пустого FindAll для User ===");

        List<User> all = dao.findAll();
        System.out.println("Пользователей в пустой базе: " + all.size());
        assertTrue(all.isEmpty(), "Список должен быть пустым при пустой базе");
        assertEquals(0, all.size(), "Размер должен быть 0");
    }

    @Test
    public void testMultipleUpdateOperations() {
        System.out.println("\n=== Тест нескольких операций обновления для User ===");

        User user = new User();
        user.setName("Исходное Имя");
        user.setLogin("originallogin");
        user.setEmail("original@example.com");
        user.setPassword("originalpass");

        Long id = dao.create(user);
        assertNotNull(id, "Пользователь должен быть создан");
        user.setId(id);

        user.setName("Обновленное Имя");
        boolean nameUpdated = dao.updateName(user);
        assertTrue(nameUpdated, "Имя должно быть обновлено");

        user.setLogin("updatedlogin");
        boolean loginUpdated = dao.updateLogin(user);
        assertTrue(loginUpdated, "Логин должен быть обновлен");

        user.setEmail("updated@example.com");
        boolean emailUpdated = dao.updateEmail(user);
        assertTrue(emailUpdated, "Email должен быть обновлен");

        user.setPassword("updatedpass");
        boolean passwordUpdated = dao.updatePassword(user);
        assertTrue(passwordUpdated, "Пароль должен быть обновлен");

        Optional<User> found = dao.findByID(id);
        assertTrue(found.isPresent(), "Пользователь должен быть найден");
        assertEquals("Обновленное Имя", found.get().getName());
        assertEquals("updatedlogin", found.get().getLogin());
        assertEquals("updated@example.com", found.get().getEmail());

        Optional<User> authenticated = dao.findByLoginAndPassword("updatedlogin", "updatedpass");
        assertTrue(authenticated.isPresent(), "Должна работать аутентификация с обновленными данными");
    }

    @Test
    public void testUpdateNonExistentUser() {

        User nonExistentUser = new User();
        nonExistentUser.setId(999999L);
        nonExistentUser.setName("Несуществующий");
        nonExistentUser.setLogin("nonexistent");
        nonExistentUser.setEmail("nonexistent@example.com");
        nonExistentUser.setPassword("password");

        boolean nameUpdated = dao.updateName(nonExistentUser);
        assertFalse(nameUpdated, "Обновление имени несуществующего пользователя должно вернуть false");

        boolean loginUpdated = dao.updateLogin(nonExistentUser);
        assertFalse(loginUpdated, "Обновление логина несуществующего пользователя должно вернуть false");

        boolean emailUpdated = dao.updateEmail(nonExistentUser);
        assertFalse(emailUpdated, "Обновление email несуществующего пользователя должно вернуть false");

        boolean passwordUpdated = dao.updatePassword(nonExistentUser);
        assertFalse(passwordUpdated, "Обновление пароля несуществующего пользователя должно вернуть false");
    }
}
