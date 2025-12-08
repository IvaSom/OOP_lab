package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.CompPoint;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompPointDTOTest {

    @Test
    public void testDefaultConstructor() {
        CompPointDTO dto = new CompPointDTO();
        assertNull(dto.getId());
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
        assertNull(dto.getFunID());
    }

    @Test
    public void testParameterizedConstructor() {
        Long expectedId = 8L;
        double expectedX = 12.7;
        double expectedY = 18.9;
        Long expectedFunID = 22L;

        CompPointDTO dto = new CompPointDTO(expectedId, expectedX, expectedY, expectedFunID);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testFromEntity() {
        CompPoint entity = new CompPoint(4L, 7.3, 11.5, 0.0, 14L);

        CompPointDTO dto = CompPointDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getX(), dto.getX());
        assertEquals(entity.getY(), dto.getY());
        assertEquals(entity.getFunID(), dto.getFunID());
    }

    @Test
    public void testFromEntity_NullEntity() {
        CompPointDTO dto = CompPointDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    public void testToEntity() {
        CompPointDTO dto = new CompPointDTO(6L, 9.2, 13.8, 17L);

        CompPoint entity = dto.toEntity();

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getX(), entity.getX());
        assertEquals(dto.getY(), entity.getY());
        assertEquals(dto.getFunID(), entity.getFunID());
    }

    @Test
    public void testToEntity_EmptyDTO() {
        CompPointDTO dto = new CompPointDTO();

        CompPoint entity = dto.toEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(0.0, entity.getX());
        assertEquals(0.0, entity.getY());
        assertNull(entity.getFunID());
    }

    @Test
    public void testSettersAndGetters() {
        CompPointDTO dto = new CompPointDTO();
        Long expectedId = 30L;
        double expectedX = 45.6;
        double expectedY = 78.9;
        Long expectedFunID = 55L;

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
        CompPointDTO dto = new CompPointDTO(1L, 2.5, 3.5, 4L);
        assertTrue(dto.equals(dto));
    }

    @Test
    public void testEquals_EqualObjects() {
        CompPointDTO dto1 = new CompPointDTO(1L, 2.5, 3.5, 4L);
        CompPointDTO dto2 = new CompPointDTO(1L, 2.5, 3.5, 4L);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_NotEqualObjects() {
        CompPointDTO dto1 = new CompPointDTO(1L, 2.5, 3.5, 4L);
        CompPointDTO dto2 = new CompPointDTO(5L, 6.5, 7.5, 8L);

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testEquals_NullObject() {
        CompPointDTO dto = new CompPointDTO(1L, 2.5, 3.5, 4L);
        assertFalse(dto.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        CompPointDTO dto = new CompPointDTO(1L, 2.5, 3.5, 4L);
        String otherObject = "String";
        assertFalse(dto.equals(otherObject));
    }

    @Test
    public void testEquals_WithNullFields() {
        CompPointDTO dto1 = new CompPointDTO(null, 0.0, 0.0, null);
        CompPointDTO dto2 = new CompPointDTO(null, 0.0, 0.0, null);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testHashCode_Consistency() {
        CompPointDTO dto = new CompPointDTO(1L, 2.5, 3.5, 4L);
        int firstHash = dto.hashCode();
        int secondHash = dto.hashCode();

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void testHashCode_EqualObjectsHaveSameHash() {
        CompPointDTO dto1 = new CompPointDTO(1L, 2.5, 3.5, 4L);
        CompPointDTO dto2 = new CompPointDTO(1L, 2.5, 3.5, 4L);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testCompleteCycle_EntityToDTOToEntity() {
        CompPoint originalEntity = new CompPoint(100L, 33.3, 66.6, 0.0, 200L);

        CompPointDTO dto = CompPointDTO.fromEntity(originalEntity);
        CompPoint restoredEntity = dto.toEntity();

        assertEquals(originalEntity.getId(), restoredEntity.getId());
        assertEquals(originalEntity.getX(), restoredEntity.getX());
        assertEquals(originalEntity.getY(), restoredEntity.getY());
        assertEquals(originalEntity.getFunID(), restoredEntity.getFunID());
    }

    @Test
    public void testCompleteCycle_DTOToEntityToDTO() {
        CompPointDTO originalDTO = new CompPointDTO(300L, 150.5, 250.5, 400L);

        CompPoint entity = originalDTO.toEntity();
        CompPointDTO restoredDTO = CompPointDTO.fromEntity(entity);

        assertEquals(originalDTO.getId(), restoredDTO.getId());
        assertEquals(originalDTO.getX(), restoredDTO.getX());
        assertEquals(originalDTO.getY(), restoredDTO.getY());
        assertEquals(originalDTO.getFunID(), restoredDTO.getFunID());
    }

    @Test
    public void testEquals_DifferentPrecision() {
        CompPointDTO dto1 = new CompPointDTO(1L, 1.234567, 2.345678, 3L);
        CompPointDTO dto2 = new CompPointDTO(1L, 1.234568, 2.345679, 3L);

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testNegativeCoordinates() {
        CompPointDTO dto = new CompPointDTO(1L, -10.5, -20.5, 3L);
        assertEquals(-10.5, dto.getX());
        assertEquals(-20.5, dto.getY());

        CompPoint entity = dto.toEntity();
        assertEquals(-10.5, entity.getX());
        assertEquals(-20.5, entity.getY());
    }

    @Test
    public void testZeroCoordinates() {
        CompPointDTO dto = new CompPointDTO(1L, 0.0, 0.0, 3L);
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
    }
}