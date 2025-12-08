package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.CompFun;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompFunDTOTest {

    @Test
    public void testDefaultConstructor() {
        CompFunDTO dto = new CompFunDTO();
        assertNull(dto.getId());
        assertNull(dto.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        Long expectedId = 10L;
        String expectedName = "Composite Function";

        CompFunDTO dto = new CompFunDTO(expectedId, expectedName);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedName, dto.getName());
    }

    @Test
    public void testFromEntity() {
        CompFun entity = new CompFun(5L, "Test Entity");

        CompFunDTO dto = CompFunDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
    }

    @Test
    public void testFromEntity_NullEntity() {
        CompFunDTO dto = CompFunDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    public void testToEntity() {
        CompFunDTO dto = new CompFunDTO(3L, "Test DTO");

        CompFun entity = dto.toEntity();

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
    }

    @Test
    public void testToEntity_EmptyDTO() {
        CompFunDTO dto = new CompFunDTO();

        CompFun entity = dto.toEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getName());
    }

    @Test
    public void testSettersAndGetters() {
        CompFunDTO dto = new CompFunDTO();
        Long expectedId = 15L;
        String expectedName = "Setter Test";

        dto.setId(expectedId);
        dto.setName(expectedName);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedName, dto.getName());
    }

    @Test
    public void testEquals_SameObject() {
        CompFunDTO dto = new CompFunDTO(1L, "Test");
        assertTrue(dto.equals(dto));
    }

    @Test
    public void testEquals_EqualObjects() {
        CompFunDTO dto1 = new CompFunDTO(1L, "Test");
        CompFunDTO dto2 = new CompFunDTO(1L, "Test");

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_NotEqualObjects() {
        CompFunDTO dto1 = new CompFunDTO(1L, "Test1");
        CompFunDTO dto2 = new CompFunDTO(2L, "Test2");

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testEquals_NullObject() {
        CompFunDTO dto = new CompFunDTO(1L, "Test");
        assertFalse(dto.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        CompFunDTO dto = new CompFunDTO(1L, "Test");
        String otherObject = "String";
        assertFalse(dto.equals(otherObject));
    }

    @Test
    public void testEquals_WithNullFields() {
        CompFunDTO dto1 = new CompFunDTO(null, null);
        CompFunDTO dto2 = new CompFunDTO(null, null);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_PartialNullFields() {
        CompFunDTO dto1 = new CompFunDTO(1L, null);
        CompFunDTO dto2 = new CompFunDTO(1L, null);
        CompFunDTO dto3 = new CompFunDTO(1L, "Different");

        assertTrue(dto1.equals(dto2));
        assertFalse(dto1.equals(dto3));
    }

    @Test
    public void testHashCode_Consistency() {
        CompFunDTO dto = new CompFunDTO(1L, "Test");
        int firstHash = dto.hashCode();
        int secondHash = dto.hashCode();

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void testHashCode_EqualObjectsHaveSameHash() {
        CompFunDTO dto1 = new CompFunDTO(1L, "Test");
        CompFunDTO dto2 = new CompFunDTO(1L, "Test");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testCompleteCycle_EntityToDTOToEntity() {
        CompFun originalEntity = new CompFun(100L, "Original CompFun");

        CompFunDTO dto = CompFunDTO.fromEntity(originalEntity);
        CompFun restoredEntity = dto.toEntity();

        assertEquals(originalEntity.getId(), restoredEntity.getId());
        assertEquals(originalEntity.getName(), restoredEntity.getName());
    }

    @Test
    public void testCompleteCycle_DTOToEntityToDTO() {
        CompFunDTO originalDTO = new CompFunDTO(200L, "DTO Original");

        CompFun entity = originalDTO.toEntity();
        CompFunDTO restoredDTO = CompFunDTO.fromEntity(entity);

        assertEquals(originalDTO.getId(), restoredDTO.getId());
        assertEquals(originalDTO.getName(), restoredDTO.getName());
    }

    @Test
    public void testEquals_CaseSensitiveName() {
        CompFunDTO dto1 = new CompFunDTO(1L, "test");
        CompFunDTO dto2 = new CompFunDTO(1L, "TEST");

        assertFalse(dto1.equals(dto2));
    }
}