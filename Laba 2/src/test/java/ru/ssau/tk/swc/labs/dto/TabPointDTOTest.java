package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.TabPoint;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TabPointDTOTest {

    @Test
    public void testDefaultConstructor() {
        TabPointDTO dto = new TabPointDTO();
        assertNull(dto.getId());
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
        assertEquals(0.0, dto.getDerive());
        assertNull(dto.getFunID());
    }

    @Test
    public void testParameterizedConstructor() {
        Long expectedId = 5L;
        double expectedX = 2.5;
        double expectedY = 5.0;
        double expectedDerive = 1.5;
        Long expectedFunID = 3L;

        TabPointDTO dto = new TabPointDTO(expectedId, expectedX, expectedY, expectedDerive, expectedFunID);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedDerive, dto.getDerive());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testFromEntity() {
        TabPoint entity = new TabPoint(4L, 1.0, 2.0, 0.5, 6L);

        TabPointDTO dto = TabPointDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getX(), dto.getX());
        assertEquals(entity.getY(), dto.getY());
        assertEquals(entity.getDerive(), dto.getDerive());
        assertEquals(entity.getFunID(), dto.getFunID());
    }

    @Test
    public void testFromEntity_NullEntity() {
        TabPointDTO dto = TabPointDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    public void testToEntity() {
        TabPointDTO dto = new TabPointDTO(7L, 3.0, 6.0, 2.0, 8L);

        TabPoint entity = dto.toEntity();

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getX(), entity.getX());
        assertEquals(dto.getY(), entity.getY());
        assertEquals(dto.getDerive(), entity.getDerive());
        assertEquals(dto.getFunID(), entity.getFunID());
    }

    @Test
    public void testToEntity_EmptyDTO() {
        TabPointDTO dto = new TabPointDTO();

        TabPoint entity = dto.toEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(0.0, entity.getX());
        assertEquals(0.0, entity.getY());
        assertEquals(0.0, entity.getDerive());
        assertNull(entity.getFunID());
    }

    @Test
    public void testSettersAndGetters() {
        TabPointDTO dto = new TabPointDTO();
        Long expectedId = 15L;
        double expectedX = 10.5;
        double expectedY = 21.0;
        double expectedDerive = 3.5;
        Long expectedFunID = 25L;

        dto.setId(expectedId);
        dto.setX(expectedX);
        dto.setY(expectedY);
        dto.setDerive(expectedDerive);
        dto.setFunID(expectedFunID);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedDerive, dto.getDerive());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testEquals_SameObject() {
        TabPointDTO dto = new TabPointDTO(1L, 2.0, 4.0, 1.0, 5L);
        assertTrue(dto.equals(dto));
    }

    @Test
    public void testEquals_EqualObjects() {
        TabPointDTO dto1 = new TabPointDTO(1L, 2.0, 4.0, 1.0, 5L);
        TabPointDTO dto2 = new TabPointDTO(1L, 2.0, 4.0, 1.0, 5L);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_NotEqualObjects() {
        TabPointDTO dto1 = new TabPointDTO(1L, 2.0, 4.0, 1.0, 5L);
        TabPointDTO dto2 = new TabPointDTO(2L, 3.0, 6.0, 2.0, 7L);

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testEquals_NullObject() {
        TabPointDTO dto = new TabPointDTO(1L, 2.0, 4.0, 1.0, 5L);
        assertFalse(dto.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        TabPointDTO dto = new TabPointDTO(1L, 2.0, 4.0, 1.0, 5L);
        String otherObject = "String";
        assertFalse(dto.equals(otherObject));
    }

    @Test
    public void testEquals_WithNullFields() {
        TabPointDTO dto1 = new TabPointDTO(null, 0.0, 0.0, 0.0, null);
        TabPointDTO dto2 = new TabPointDTO(null, 0.0, 0.0, 0.0, null);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testHashCode_Consistency() {
        TabPointDTO dto = new TabPointDTO(1L, 2.0, 4.0, 1.0, 5L);
        int firstHash = dto.hashCode();
        int secondHash = dto.hashCode();

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void testHashCode_EqualObjectsHaveSameHash() {
        TabPointDTO dto1 = new TabPointDTO(1L, 2.0, 4.0, 1.0, 5L);
        TabPointDTO dto2 = new TabPointDTO(1L, 2.0, 4.0, 1.0, 5L);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testCompleteCycle_EntityToDTOToEntity() {
        TabPoint originalEntity = new TabPoint(100L, 50.0, 100.0, 25.0, 200L);

        TabPointDTO dto = TabPointDTO.fromEntity(originalEntity);
        TabPoint restoredEntity = dto.toEntity();

        assertEquals(originalEntity.getId(), restoredEntity.getId());
        assertEquals(originalEntity.getX(), restoredEntity.getX());
        assertEquals(originalEntity.getY(), restoredEntity.getY());
        assertEquals(originalEntity.getDerive(), restoredEntity.getDerive());
        assertEquals(originalEntity.getFunID(), restoredEntity.getFunID());
    }

    @Test
    public void testCompleteCycle_DTOToEntityToDTO() {
        TabPointDTO originalDTO = new TabPointDTO(300L, 150.0, 300.0, 75.0, 400L);

        TabPoint entity = originalDTO.toEntity();
        TabPointDTO restoredDTO = TabPointDTO.fromEntity(entity);

        assertEquals(originalDTO.getId(), restoredDTO.getId());
        assertEquals(originalDTO.getX(), restoredDTO.getX());
        assertEquals(originalDTO.getY(), restoredDTO.getY());
        assertEquals(originalDTO.getDerive(), restoredDTO.getDerive());
        assertEquals(originalDTO.getFunID(), restoredDTO.getFunID());
    }

    @Test
    public void testEquals_DifferentPrecision() {
        TabPointDTO dto1 = new TabPointDTO(1L, 1.234567, 2.345678, 3.456789, 4L);
        TabPointDTO dto2 = new TabPointDTO(1L, 1.234568, 2.345679, 3.456790, 4L);

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testNegativeValues() {
        TabPointDTO dto = new TabPointDTO(1L, -10.5, -21.0, -3.5, -7L);
        assertEquals(-10.5, dto.getX());
        assertEquals(-21.0, dto.getY());
        assertEquals(-3.5, dto.getDerive());
        assertEquals(-7L, dto.getFunID());

        TabPoint entity = dto.toEntity();
        assertEquals(-10.5, entity.getX());
        assertEquals(-21.0, entity.getY());
        assertEquals(-3.5, entity.getDerive());
        assertEquals(-7L, entity.getFunID());
    }

    @Test
    public void testZeroDerive() {
        TabPointDTO dto = new TabPointDTO(1L, 2.0, 4.0, 0.0, 3L);
        assertEquals(0.0, dto.getDerive());
    }

    @Test
    public void testLargeValues() {
        TabPointDTO dto = new TabPointDTO(Long.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, dto.getId());
        assertEquals(Double.MAX_VALUE, dto.getX());
        assertEquals(Double.MAX_VALUE, dto.getY());
        assertEquals(Double.MAX_VALUE, dto.getDerive());
        assertEquals(Long.MAX_VALUE, dto.getFunID());
    }

    @Test
    public void testMixedSignValues() {
        TabPointDTO dto = new TabPointDTO(1L, -5.5, 10.5, -2.5, -9L);
        assertEquals(-5.5, dto.getX());
        assertEquals(10.5, dto.getY());
        assertEquals(-2.5, dto.getDerive());
        assertEquals(-9L, dto.getFunID());
    }

    @Test
    public void testSetValuesInDifferentOrder() {
        TabPointDTO dto = new TabPointDTO();

        dto.setFunID(99L);
        dto.setDerive(2.5);
        dto.setY(10.0);
        dto.setX(5.0);
        dto.setId(1L);

        assertEquals(1L, dto.getId());
        assertEquals(5.0, dto.getX());
        assertEquals(10.0, dto.getY());
        assertEquals(2.5, dto.getDerive());
        assertEquals(99L, dto.getFunID());
    }

    @Test
    public void testDeriveSameAsSlope() {
        TabPointDTO dto = new TabPointDTO(1L, 1.0, 1.0, 1.0, 2L);
        assertEquals(1.0, dto.getX());
        assertEquals(1.0, dto.getY());
        assertEquals(1.0, dto.getDerive());
        assertEquals(2L, dto.getFunID());
    }
}