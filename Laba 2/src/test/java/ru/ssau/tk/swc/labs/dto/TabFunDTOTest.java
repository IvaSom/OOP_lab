package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.TabFun;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TabFunDTOTest {

    @Test
    public void testDefaultConstructor() {
        TabFunDTO dto = new TabFunDTO();
        assertNull(dto.getId());
        assertNull(dto.getType());
    }

    @Test
    public void testParameterizedConstructor() {
        Long expectedId = 7L;
        String expectedType = "Linear";

        TabFunDTO dto = new TabFunDTO(expectedId, expectedType);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedType, dto.getType());
    }

    @Test
    public void testFromEntity() {
        TabFun entity = new TabFun(3L, "Quadratic");

        TabFunDTO dto = TabFunDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getType(), dto.getType());
    }

    @Test
    public void testFromEntity_NullEntity() {
        TabFunDTO dto = TabFunDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    public void testToEntity() {
        TabFunDTO dto = new TabFunDTO(5L, "Exponential");

        TabFun entity = dto.toEntity();

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getType(), entity.getType());
    }

    @Test
    public void testToEntity_EmptyDTO() {
        TabFunDTO dto = new TabFunDTO();

        TabFun entity = dto.toEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getType());
    }

    @Test
    public void testSettersAndGetters() {
        TabFunDTO dto = new TabFunDTO();
        Long expectedId = 12L;
        String expectedType = "Polynomial";

        dto.setId(expectedId);
        dto.setType(expectedType);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedType, dto.getType());
    }

    @Test
    public void testEquals_SameObject() {
        TabFunDTO dto = new TabFunDTO(1L, "Test");
        assertTrue(dto.equals(dto));
    }

    @Test
    public void testEquals_EqualObjects() {
        TabFunDTO dto1 = new TabFunDTO(1L, "Test");
        TabFunDTO dto2 = new TabFunDTO(1L, "Test");

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_NotEqualObjects() {
        TabFunDTO dto1 = new TabFunDTO(1L, "Test1");
        TabFunDTO dto2 = new TabFunDTO(2L, "Test2");

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testEquals_NullObject() {
        TabFunDTO dto = new TabFunDTO(1L, "Test");
        assertFalse(dto.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        TabFunDTO dto = new TabFunDTO(1L, "Test");
        String otherObject = "String";
        assertFalse(dto.equals(otherObject));
    }

    @Test
    public void testEquals_WithNullFields() {
        TabFunDTO dto1 = new TabFunDTO(null, null);
        TabFunDTO dto2 = new TabFunDTO(null, null);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_PartialNullFields() {
        TabFunDTO dto1 = new TabFunDTO(1L, null);
        TabFunDTO dto2 = new TabFunDTO(1L, null);
        TabFunDTO dto3 = new TabFunDTO(1L, "Different");

        assertTrue(dto1.equals(dto2));
        assertFalse(dto1.equals(dto3));
    }

    @Test
    public void testHashCode_Consistency() {
        TabFunDTO dto = new TabFunDTO(1L, "Test");
        int firstHash = dto.hashCode();
        int secondHash = dto.hashCode();

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void testHashCode_EqualObjectsHaveSameHash() {
        TabFunDTO dto1 = new TabFunDTO(1L, "Test");
        TabFunDTO dto2 = new TabFunDTO(1L, "Test");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testCompleteCycle_EntityToDTOToEntity() {
        TabFun originalEntity = new TabFun(100L, "Original Type");

        TabFunDTO dto = TabFunDTO.fromEntity(originalEntity);
        TabFun restoredEntity = dto.toEntity();

        assertEquals(originalEntity.getId(), restoredEntity.getId());
        assertEquals(originalEntity.getType(), restoredEntity.getType());
    }

    @Test
    public void testCompleteCycle_DTOToEntityToDTO() {
        TabFunDTO originalDTO = new TabFunDTO(200L, "DTO Type");

        TabFun entity = originalDTO.toEntity();
        TabFunDTO restoredDTO = TabFunDTO.fromEntity(entity);

        assertEquals(originalDTO.getId(), restoredDTO.getId());
        assertEquals(originalDTO.getType(), restoredDTO.getType());
    }

    @Test
    public void testEquals_CaseSensitiveType() {
        TabFunDTO dto1 = new TabFunDTO(1L, "linear");
        TabFunDTO dto2 = new TabFunDTO(1L, "LINEAR");

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testTypeWithSpaces() {
        TabFunDTO dto = new TabFunDTO(1L, "Piecewise Linear");
        assertEquals("Piecewise Linear", dto.getType());

        TabFun entity = dto.toEntity();
        assertEquals("Piecewise Linear", entity.getType());
    }

    @Test
    public void testTypeWithSpecialCharacters() {
        TabFunDTO dto = new TabFunDTO(1L, "Type_1-2.3");
        assertEquals("Type_1-2.3", dto.getType());
    }

    @Test
    public void testNullTypeUpdate() {
        TabFunDTO dto = new TabFunDTO(1L, "Initial");
        dto.setType(null);
        assertNull(dto.getType());

        TabFun entity = dto.toEntity();
        assertNull(entity.getType());
    }
}