package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDTOTest {

    @Test
    public void testDefaultConstructor() {
        UserDTO dto = new UserDTO();
        assertNull(dto.getId());
        assertNull(dto.getLogin());
        assertNull(dto.getEmail());
        assertNull(dto.getName());
        assertNull(dto.getPassword());
    }

    @Test
    public void testParameterizedConstructor() {
        Long expectedId = 1L;
        String expectedLogin = "johndoe";
        String expectedEmail = "john@example.com";
        String expectedName = "John Doe";
        String expectedPassword = "password123";

        UserDTO dto = new UserDTO(expectedId, expectedLogin, expectedEmail, expectedName, expectedPassword);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedLogin, dto.getLogin());
        assertEquals(expectedEmail, dto.getEmail());
        assertEquals(expectedName, dto.getName());
        assertEquals(expectedPassword, dto.getPassword());
    }

    @Test
    public void testFromEntity() {
        User entity = new User(3L, "alice", "alice@example.com", "Alice Smith", "securepass");

        UserDTO dto = UserDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getLogin(), dto.getLogin());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getPassword(), dto.getPassword());
    }

    @Test
    public void testFromEntity_NullEntity() {
        UserDTO dto = UserDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    public void testToEntity() {
        UserDTO dto = new UserDTO(5L, "bob", "bob@example.com", "Bob Johnson", "bobpass");

        User entity = dto.toEntity();

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getLogin(), entity.getLogin());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getPassword(), entity.getPassword());
    }

    @Test
    public void testToEntity_EmptyDTO() {
        UserDTO dto = new UserDTO();

        User entity = dto.toEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getLogin());
        assertNull(entity.getEmail());
        assertNull(entity.getName());
        assertNull(entity.getPassword());
    }

    @Test
    public void testSettersAndGetters() {
        UserDTO dto = new UserDTO();
        Long expectedId = 12L;
        String expectedLogin = "testuser";
        String expectedEmail = "test@example.com";
        String expectedName = "Test User";
        String expectedPassword = "testpass";

        dto.setId(expectedId);
        dto.setLogin(expectedLogin);
        dto.setEmail(expectedEmail);
        dto.setName(expectedName);
        dto.setPassword(expectedPassword);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedLogin, dto.getLogin());
        assertEquals(expectedEmail, dto.getEmail());
        assertEquals(expectedName, dto.getName());
        assertEquals(expectedPassword, dto.getPassword());
    }

    @Test
    public void testEquals_SameObject() {
        UserDTO dto = new UserDTO(1L, "test", "test@example.com", "Test", "pass");
        assertTrue(dto.equals(dto));
    }

    @Test
    public void testEquals_EqualObjects() {
        UserDTO dto1 = new UserDTO(1L, "test", "test@example.com", "Test", "pass");
        UserDTO dto2 = new UserDTO(1L, "test", "test@example.com", "Test", "pass");

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_NotEqualObjects() {
        UserDTO dto1 = new UserDTO(1L, "user1", "user1@example.com", "User1", "pass1");
        UserDTO dto2 = new UserDTO(2L, "user2", "user2@example.com", "User2", "pass2");

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testEquals_NullObject() {
        UserDTO dto = new UserDTO(1L, "test", "test@example.com", "Test", "pass");
        assertFalse(dto.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        UserDTO dto = new UserDTO(1L, "test", "test@example.com", "Test", "pass");
        String otherObject = "String";
        assertFalse(dto.equals(otherObject));
    }

    @Test
    public void testEquals_WithNullFields() {
        UserDTO dto1 = new UserDTO(null, null, null, null, null);
        UserDTO dto2 = new UserDTO(null, null, null, null, null);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_PartialNullFields() {
        UserDTO dto1 = new UserDTO(1L, null, "test@example.com", null, "pass");
        UserDTO dto2 = new UserDTO(1L, null, "test@example.com", null, "pass");
        UserDTO dto3 = new UserDTO(1L, "different", "test@example.com", "different", "pass");

        assertTrue(dto1.equals(dto2));
        assertFalse(dto1.equals(dto3));
    }

    @Test
    public void testHashCode_Consistency() {
        UserDTO dto = new UserDTO(1L, "test", "test@example.com", "Test", "pass");
        int firstHash = dto.hashCode();
        int secondHash = dto.hashCode();

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void testHashCode_EqualObjectsHaveSameHash() {
        UserDTO dto1 = new UserDTO(1L, "test", "test@example.com", "Test", "pass");
        UserDTO dto2 = new UserDTO(1L, "test", "test@example.com", "Test", "pass");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testCompleteCycle_EntityToDTOToEntity() {
        User originalEntity = new User(100L, "original", "original@example.com", "Original User", "originalpass");

        UserDTO dto = UserDTO.fromEntity(originalEntity);
        User restoredEntity = dto.toEntity();

        assertEquals(originalEntity.getId(), restoredEntity.getId());
        assertEquals(originalEntity.getLogin(), restoredEntity.getLogin());
        assertEquals(originalEntity.getEmail(), restoredEntity.getEmail());
        assertEquals(originalEntity.getName(), restoredEntity.getName());
        assertEquals(originalEntity.getPassword(), restoredEntity.getPassword());
    }

    @Test
    public void testCompleteCycle_DTOToEntityToDTO() {
        UserDTO originalDTO = new UserDTO(200L, "dto", "dto@example.com", "DTO User", "dtopass");

        User entity = originalDTO.toEntity();
        UserDTO restoredDTO = UserDTO.fromEntity(entity);

        assertEquals(originalDTO.getId(), restoredDTO.getId());
        assertEquals(originalDTO.getLogin(), restoredDTO.getLogin());
        assertEquals(originalDTO.getEmail(), restoredDTO.getEmail());
        assertEquals(originalDTO.getName(), restoredDTO.getName());
        assertEquals(originalDTO.getPassword(), restoredDTO.getPassword());
    }

    @Test
    public void testEquals_CaseSensitive() {
        UserDTO dto1 = new UserDTO(1L, "User", "user@example.com", "User", "Pass");
        UserDTO dto2 = new UserDTO(1L, "user", "USER@example.com", "USER", "PASS");

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testNameWithSpaces() {
        UserDTO dto = new UserDTO(1L, "login", "email@example.com", "John Michael Doe", "pass");
        assertEquals("John Michael Doe", dto.getName());

        User entity = dto.toEntity();
        assertEquals("John Michael Doe", entity.getName());
    }

    @Test
    public void testEmailWithSpecialCharacters() {
        UserDTO dto = new UserDTO(1L, "login", "user.name+tag@example.co.uk", "Name", "pass");
        assertEquals("user.name+tag@example.co.uk", dto.getEmail());
    }

    @Test
    public void testLoginWithSpecialCharacters() {
        UserDTO dto = new UserDTO(1L, "user_123-abc", "email@example.com", "Name", "pass");
        assertEquals("user_123-abc", dto.getLogin());
    }

    @Test
    public void testPasswordWithSpecialCharacters() {
        UserDTO dto = new UserDTO(1L, "login", "email@example.com", "Name", "P@ssw0rd!123#");
        assertEquals("P@ssw0rd!123#", dto.getPassword());
    }

    @Test
    public void testNullFieldUpdates() {
        UserDTO dto = new UserDTO(1L, "login", "email@example.com", "Name", "pass");

        dto.setLogin(null);
        dto.setEmail(null);
        dto.setName(null);
        dto.setPassword(null);

        assertNull(dto.getLogin());
        assertNull(dto.getEmail());
        assertNull(dto.getName());
        assertNull(dto.getPassword());

        User entity = dto.toEntity();
        assertNull(entity.getLogin());
        assertNull(entity.getEmail());
        assertNull(entity.getName());
        assertNull(entity.getPassword());
    }

    @Test
    public void testMixedUnicodeCharacters() {
        UserDTO dto = new UserDTO(1L, "иван123", "иван@пример.рф", "Иван Петров", "пароль123");
        assertEquals("иван123", dto.getLogin());
        assertEquals("иван@пример.рф", dto.getEmail());
        assertEquals("Иван Петров", dto.getName());
        assertEquals("пароль123", dto.getPassword());

        User entity = dto.toEntity();
        assertEquals("иван123", entity.getLogin());
        assertEquals("иван@пример.рф", entity.getEmail());
        assertEquals("Иван Петров", entity.getName());
        assertEquals("пароль123", entity.getPassword());
    }
}