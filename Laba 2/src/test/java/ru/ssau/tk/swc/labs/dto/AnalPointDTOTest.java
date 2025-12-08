package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.AnalPoint;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnalPointDTOTest {

    @Test
    public void testDefaultConstructor() {
        AnalPointDTO dto = new AnalPointDTO();
        assertNull(dto.getId());
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
        assertNull(dto.getFunID());
    }

    @Test
    public void testParameterizedConstructor() {
        Long expectedId = 5L;
        double expectedX = 10.5;
        double expectedY = 20.3;
        Long expectedFunID = 7L;

        AnalPointDTO dto = new AnalPointDTO(expectedId, expectedX, expectedY, expectedFunID);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testFromEntity() {
        AnalPoint entity = new AnalPoint(3L, 15.2, 25.7, 0.0, 9L);

        AnalPointDTO dto = AnalPointDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getX(), dto.getX());
        assertEquals(entity.getY(), dto.getY());
        assertEquals(entity.getFunID(), dto.getFunID());
    }

    @Test
    public void testFromEntity_NullEntity() {
        AnalPointDTO dto = AnalPointDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    public void testToEntity() {
        AnalPointDTO dto = new AnalPointDTO(2L, 5.5, 12.3, 8L);

        AnalPoint entity = dto.toEntity();

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getX(), entity.getX());
        assertEquals(dto.getY(), entity.getY());
        assertEquals(dto.getFunID(), entity.getFunID());
    }

    @Test
    public void testToEntity_EmptyDTO() {
        AnalPointDTO dto = new AnalPointDTO();

        AnalPoint entity = dto.toEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(0.0, entity.getX());
        assertEquals(0.0, entity.getY());
        assertNull(entity.getFunID());
    }

    @Test
    public void testSettersAndGetters() {
        AnalPointDTO dto = new AnalPointDTO();
        Long expectedId = 25L;
        double expectedX = 100.1;
        double expectedY = 200.2;
        Long expectedFunID = 50L;

        dto.setId(expectedId);
        dto.setX(expectedX);
        dto.setY(expectedY);
        dto.setFunID(expectedFunID);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testEquals_SameObject() {
        AnalPointDTO dto = new AnalPointDTO(1L, 2.0, 3.0, 4L);
        assertTrue(dto.equals(dto));
    }

    @Test
    public void testEquals_EqualObjects() {
        AnalPointDTO dto1 = new AnalPointDTO(1L, 2.0, 3.0, 4L);
        AnalPointDTO dto2 = new AnalPointDTO(1L, 2.0, 3.0, 4L);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_NotEqualObjects() {
        AnalPointDTO dto1 = new AnalPointDTO(1L, 2.0, 3.0, 4L);
        AnalPointDTO dto2 = new AnalPointDTO(2L, 3.0, 4.0, 5L);

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testEquals_NullObject() {
        AnalPointDTO dto = new AnalPointDTO(1L, 2.0, 3.0, 4L);
        assertFalse(dto.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        AnalPointDTO dto = new AnalPointDTO(1L, 2.0, 3.0, 4L);
        String otherObject = "Not a DTO";
        assertFalse(dto.equals(otherObject));
    }

    @Test
    public void testEquals_WithNullFields() {
        AnalPointDTO dto1 = new AnalPointDTO(null, 0.0, 0.0, null);
        AnalPointDTO dto2 = new AnalPointDTO(null, 0.0, 0.0, null);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testHashCode_Consistency() {
        AnalPointDTO dto = new AnalPointDTO(1L, 2.0, 3.0, 4L);
        int firstHash = dto.hashCode();
        int secondHash = dto.hashCode();

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void testHashCode_EqualObjectsHaveSameHash() {
        AnalPointDTO dto1 = new AnalPointDTO(1L, 2.0, 3.0, 4L);
        AnalPointDTO dto2 = new AnalPointDTO(1L, 2.0, 3.0, 4L);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testCompleteCycle_EntityToDTOToEntity() {
        AnalPoint originalEntity = new AnalPoint(100L, 50.5, 75.5, 0.0, 200L);

        AnalPointDTO dto = AnalPointDTO.fromEntity(originalEntity);
        AnalPoint restoredEntity = dto.toEntity();

        assertEquals(originalEntity.getId(), restoredEntity.getId());
        assertEquals(originalEntity.getX(), restoredEntity.getX());
        assertEquals(originalEntity.getY(), restoredEntity.getY());
        assertEquals(originalEntity.getFunID(), restoredEntity.getFunID());
    }

    @Test
    public void testCompleteCycle_DTOToEntityToDTO() {
        AnalPointDTO originalDTO = new AnalPointDTO(300L, 150.0, 250.0, 400L);

        AnalPoint entity = originalDTO.toEntity();
        AnalPointDTO restoredDTO = AnalPointDTO.fromEntity(entity);

        assertEquals(originalDTO.getId(), restoredDTO.getId());
        assertEquals(originalDTO.getX(), restoredDTO.getX());
        assertEquals(originalDTO.getY(), restoredDTO.getY());
        assertEquals(originalDTO.getFunID(), restoredDTO.getFunID());
    }

    @Test
    public void testEquals_DifferentPrecision() {
        AnalPointDTO dto1 = new AnalPointDTO(1L, 1.0, 2.0, 3L);
        AnalPointDTO dto2 = new AnalPointDTO(1L, 1.000001, 2.000001, 3L);

        assertFalse(dto1.equals(dto2));
    }
}