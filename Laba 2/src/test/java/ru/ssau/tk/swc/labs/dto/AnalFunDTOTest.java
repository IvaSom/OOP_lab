package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.AnalFun;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnalFunDTOTest {

    @Test
    public void testDefaultConstructor() {
        AnalFunDTO dto = new AnalFunDTO();

        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getType());
    }

    @Test
    public void testParameterizedConstructor() {
        Long expectedId = 1L;
        String expectedName = "Test Function";
        Integer expectedType = 2;

        AnalFunDTO dto = new AnalFunDTO(expectedId, expectedName, expectedType);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedName, dto.getName());
        assertEquals(expectedType, dto.getType());
    }

    @Test
    public void testFromEntity() {
        AnalFun entity = new AnalFun(1L, "Entity Function", 3);

        AnalFunDTO dto = AnalFunDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getType(), dto.getType());
    }

    @Test
    public void testFromEntity_NullEntity() {
        AnalFunDTO dto = AnalFunDTO.fromEntity(null);

        assertNull(dto);
    }

    @Test
    public void testToEntity() {
        AnalFunDTO dto = new AnalFunDTO(1L, "DTO Function", 4);

        AnalFun entity = dto.toEntity();

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getType(), entity.getType());
    }

    @Test
    public void testToEntity_EmptyDTO() {
        AnalFunDTO dto = new AnalFunDTO();

        AnalFun entity = dto.toEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getName());
        assertNull(entity.getType());
    }

    @Test
    public void testSettersAndGetters() {
        AnalFunDTO dto = new AnalFunDTO();
        Long expectedId = 10L;
        String expectedName = "Setter Function";
        Integer expectedType = 5;

        dto.setId(expectedId);
        dto.setName(expectedName);
        dto.setType(expectedType);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedName, dto.getName());
        assertEquals(expectedType, dto.getType());
    }

    @Test
    public void testEquals_SameObject() {
        AnalFunDTO dto = new AnalFunDTO(1L, "Test", 2);

        assertTrue(dto.equals(dto));
    }

    @Test
    public void testEquals_EqualObjects() {
        AnalFunDTO dto1 = new AnalFunDTO(1L, "Test", 2);
        AnalFunDTO dto2 = new AnalFunDTO(1L, "Test", 2);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_NotEqualObjects() {
        AnalFunDTO dto1 = new AnalFunDTO(1L, "Test1", 2);
        AnalFunDTO dto2 = new AnalFunDTO(2L, "Test2", 3);

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testEquals_NullObject() {
        AnalFunDTO dto = new AnalFunDTO(1L, "Test", 2);

        assertFalse(dto.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        AnalFunDTO dto = new AnalFunDTO(1L, "Test", 2);
        String otherObject = "Not a DTO";

        assertFalse(dto.equals(otherObject));
    }

    @Test
    public void testEquals_WithNullFields() {
        AnalFunDTO dto1 = new AnalFunDTO(null, null, null);
        AnalFunDTO dto2 = new AnalFunDTO(null, null, null);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_PartialNullFields() {
        AnalFunDTO dto1 = new AnalFunDTO(1L, null, 2);
        AnalFunDTO dto2 = new AnalFunDTO(1L, null, 2);
        AnalFunDTO dto3 = new AnalFunDTO(1L, "Different", 2);

        assertTrue(dto1.equals(dto2));
        assertFalse(dto1.equals(dto3));
    }

    @Test
    public void testHashCode_Consistency() {
        AnalFunDTO dto = new AnalFunDTO(1L, "Test", 2);
        int firstHash = dto.hashCode();
        int secondHash = dto.hashCode();

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void testHashCode_EqualObjectsHaveSameHash() {
        AnalFunDTO dto1 = new AnalFunDTO(1L, "Test", 2);
        AnalFunDTO dto2 = new AnalFunDTO(1L, "Test", 2);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testCompleteCycle_EntityToDTOToEntity() {
        AnalFun originalEntity = new AnalFun(100L, "Original", 99);

        AnalFunDTO dto = AnalFunDTO.fromEntity(originalEntity);
        AnalFun restoredEntity = dto.toEntity();

        assertEquals(originalEntity.getId(), restoredEntity.getId());
        assertEquals(originalEntity.getName(), restoredEntity.getName());
        assertEquals(originalEntity.getType(), restoredEntity.getType());
    }

    @Test
    public void testCompleteCycle_DTOToEntityToDTO() {
        AnalFunDTO originalDTO = new AnalFunDTO(200L, "DTO Original", 77);

        AnalFun entity = originalDTO.toEntity();
        AnalFunDTO restoredDTO = AnalFunDTO.fromEntity(entity);

        assertEquals(originalDTO.getId(), restoredDTO.getId());
        assertEquals(originalDTO.getName(), restoredDTO.getName());
        assertEquals(originalDTO.getType(), restoredDTO.getType());
    }
}