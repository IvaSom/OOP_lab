package ru.ssau.tk.swc.labs.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateUserDTOTest {

    @Test
    public void testDefaultConstructor() {
        CreateUserDTO dto = new CreateUserDTO();
        assertNull(dto.getName());
        assertNull(dto.getLogin());
        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
    }

    @Test
    public void testParameterizedConstructor() {
        String expectedName = "John Doe";
        String expectedLogin = "johndoe";
        String expectedEmail = "john@example.com";
        String expectedPassword = "password123";

        CreateUserDTO dto = new CreateUserDTO(expectedName, expectedLogin, expectedEmail, expectedPassword);

        assertEquals(expectedName, dto.getName());
        assertEquals(expectedLogin, dto.getLogin());
        assertEquals(expectedEmail, dto.getEmail());
        assertEquals(expectedPassword, dto.getPassword());
    }

    @Test
    public void testSettersAndGetters() {
        CreateUserDTO dto = new CreateUserDTO();
        String expectedName = "Alice Smith";
        String expectedLogin = "alice";
        String expectedEmail = "alice@example.com";
        String expectedPassword = "securepass";

        dto.setName(expectedName);
        dto.setLogin(expectedLogin);
        dto.setEmail(expectedEmail);
        dto.setPassword(expectedPassword);

        assertEquals(expectedName, dto.getName());
        assertEquals(expectedLogin, dto.getLogin());
        assertEquals(expectedEmail, dto.getEmail());
        assertEquals(expectedPassword, dto.getPassword());
    }

    @Test
    public void testNameBoundaryValues() {
        CreateUserDTO dto1 = new CreateUserDTO("A", "login", "email@test.com", "pass");
        CreateUserDTO dto2 = new CreateUserDTO("A".repeat(50), "login", "email@test.com", "pass");

        assertEquals("A", dto1.getName());
        assertEquals(50, dto2.getName().length());
    }

    @Test
    public void testLoginBoundaryValues() {
        CreateUserDTO dto1 = new CreateUserDTO("Name", "L", "email@test.com", "pass");
        CreateUserDTO dto2 = new CreateUserDTO("Name", "L".repeat(50), "email@test.com", "pass");

        assertEquals("L", dto1.getLogin());
        assertEquals(50, dto2.getLogin().length());
    }

    @Test
    public void testEmailBoundaryValues() {
        CreateUserDTO dto = new CreateUserDTO("Name", "login", "e@e.com", "pass");
        assertEquals("e@e.com", dto.getEmail());
    }

    @Test
    public void testPasswordBoundaryValues() {
        CreateUserDTO dto1 = new CreateUserDTO("Name", "login", "email@test.com", "P");
        CreateUserDTO dto2 = new CreateUserDTO("Name", "login", "email@test.com", "P".repeat(100));

        assertEquals("P", dto1.getPassword());
        assertEquals(100, dto2.getPassword().length());
    }

    @Test
    public void testEmptyStrings() {
        CreateUserDTO dto = new CreateUserDTO("", "", "", "");
        assertEquals("", dto.getName());
        assertEquals("", dto.getLogin());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getPassword());
    }

    @Test
    public void testWhitespaceValues() {
        CreateUserDTO dto = new CreateUserDTO("   ", "   ", "   ", "   ");
        assertEquals("   ", dto.getName());
        assertEquals("   ", dto.getLogin());
        assertEquals("   ", dto.getEmail());
        assertEquals("   ", dto.getPassword());
    }

    @Test
    public void testNullValues() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setName(null);
        dto.setLogin(null);
        dto.setEmail(null);
        dto.setPassword(null);

        assertNull(dto.getName());
        assertNull(dto.getLogin());
        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
    }

    @Test
    public void testConstructorWithNullValues() {
        CreateUserDTO dto = new CreateUserDTO(null, null, null, null);
        assertNull(dto.getName());
        assertNull(dto.getLogin());
        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
    }

    @Test
    public void testUpdateValues() {
        CreateUserDTO dto = new CreateUserDTO("Old Name", "oldlogin", "old@email.com", "oldpass");

        dto.setName("New Name");
        dto.setLogin("newlogin");
        dto.setEmail("new@email.com");
        dto.setPassword("newpass");

        assertEquals("New Name", dto.getName());
        assertEquals("newlogin", dto.getLogin());
        assertEquals("new@email.com", dto.getEmail());
        assertEquals("newpass", dto.getPassword());
    }

    @Test
    public void testSameValuesMultipleTimes() {
        CreateUserDTO dto = new CreateUserDTO();

        dto.setName("Test User");
        dto.setLogin("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("testpass");

        assertEquals("Test User", dto.getName());
        assertEquals("testuser", dto.getLogin());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("testpass", dto.getPassword());

        dto.setName("Test User");
        dto.setLogin("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("testpass");

        assertEquals("Test User", dto.getName());
        assertEquals("testuser", dto.getLogin());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("testpass", dto.getPassword());
    }

    @Test
    public void testNameWithSpaces() {
        CreateUserDTO dto = new CreateUserDTO("John Michael Doe", "jdoe", "john@example.com", "pass");
        assertEquals("John Michael Doe", dto.getName());
    }

    @Test
    public void testLoginWithSpecialCharacters() {
        CreateUserDTO dto = new CreateUserDTO("Name", "user_123", "email@test.com", "pass");
        assertEquals("user_123", dto.getLogin());
    }

    @Test
    public void testEmailWithPlus() {
        CreateUserDTO dto = new CreateUserDTO("Name", "login", "user+tag@example.com", "pass");
        assertEquals("user+tag@example.com", dto.getEmail());
    }

    @Test
    public void testEmailWithDot() {
        CreateUserDTO dto = new CreateUserDTO("Name", "login", "user.name@example.com", "pass");
        assertEquals("user.name@example.com", dto.getEmail());
    }

    @Test
    public void testPasswordWithSpecialCharacters() {
        CreateUserDTO dto = new CreateUserDTO("Name", "login", "email@test.com", "P@ssw0rd!123");
        assertEquals("P@ssw0rd!123", dto.getPassword());
    }

    @Test
    public void testNameExactly50Chars() {
        String name50 = "N".repeat(50);
        CreateUserDTO dto = new CreateUserDTO(name50, "login", "email@test.com", "pass");
        assertEquals(50, dto.getName().length());
    }

    @Test
    public void testLoginExactly50Chars() {
        String login50 = "L".repeat(50);
        CreateUserDTO dto = new CreateUserDTO("Name", login50, "email@test.com", "pass");
        assertEquals(50, dto.getLogin().length());
    }

    @Test
    public void testPasswordExactly100Chars() {
        String password100 = "P".repeat(100);
        CreateUserDTO dto = new CreateUserDTO("Name", "login", "email@test.com", password100);
        assertEquals(100, dto.getPassword().length());
    }

    @Test
    public void testSetEmptyStrings() {
        CreateUserDTO dto = new CreateUserDTO("Name", "login", "email@test.com", "pass");

        dto.setName("");
        dto.setLogin("");
        dto.setEmail("");
        dto.setPassword("");

        assertEquals("", dto.getName());
        assertEquals("", dto.getLogin());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getPassword());
    }

    @Test
    public void testMixedUnicodeCharacters() {
        CreateUserDTO dto = new CreateUserDTO("Иван Петров", "иван123", "иван@пример.рф", "пароль123");
        assertEquals("Иван Петров", dto.getName());
        assertEquals("иван123", dto.getLogin());
        assertEquals("иван@пример.рф", dto.getEmail());
        assertEquals("пароль123", dto.getPassword());
    }

    @Test
    public void testLongEmail() {
        String longEmail = "very.long.email.address.with.many.dots.and.subdomains@example.co.uk";
        CreateUserDTO dto = new CreateUserDTO("Name", "login", longEmail, "pass");
        assertEquals(longEmail, dto.getEmail());
    }
}