package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.CompositeStructure;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompositeStructureDTOTest {

    @Test
    public void testDefaultConstructor() {
        CompositeStructureDTO dto = new CompositeStructureDTO();
        assertNull(dto.getId());
        assertNull(dto.getCompositeId());
        assertNull(dto.getAnalyticId());
        assertNull(dto.getExecutionOrder());
    }

    @Test
    public void testParameterizedConstructor() {
        Long expectedId = 1L;
        Long expectedCompositeId = 2L;
        Long expectedAnalyticId = 3L;
        Integer expectedOrder = 1;

        CompositeStructureDTO dto = new CompositeStructureDTO(expectedId, expectedCompositeId, expectedAnalyticId, expectedOrder);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedCompositeId, dto.getCompositeId());
        assertEquals(expectedAnalyticId, dto.getAnalyticId());
        assertEquals(expectedOrder, dto.getExecutionOrder());
    }

    @Test
    public void testFromEntity() {
        CompositeStructure entity = new CompositeStructure(5L, 10L, 15L, 2);

        CompositeStructureDTO dto = CompositeStructureDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getComposite_id(), dto.getCompositeId());
        assertEquals(entity.getAnalytic_id(), dto.getAnalyticId());
        assertEquals(entity.getOrder(), dto.getExecutionOrder());
    }

    @Test
    public void testFromEntity_NullEntity() {
        CompositeStructureDTO dto = CompositeStructureDTO.fromEntity(null);
        assertNull(dto);
    }

    @Test
    public void testToEntity() {
        CompositeStructureDTO dto = new CompositeStructureDTO(3L, 6L, 9L, 3);

        CompositeStructure entity = dto.toEntity();

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getCompositeId(), entity.getComposite_id());
        assertEquals(dto.getAnalyticId(), entity.getAnalytic_id());
        assertEquals(dto.getExecutionOrder(), entity.getOrder());
    }

    @Test
    public void testSettersAndGetters() {
        CompositeStructureDTO dto = new CompositeStructureDTO();
        Long expectedId = 20L;
        Long expectedCompositeId = 30L;
        Long expectedAnalyticId = 40L;
        Integer expectedOrder = 5;

        dto.setId(expectedId);
        dto.setCompositeId(expectedCompositeId);
        dto.setAnalyticId(expectedAnalyticId);
        dto.setExecutionOrder(expectedOrder);

        assertEquals(expectedId, dto.getId());
        assertEquals(expectedCompositeId, dto.getCompositeId());
        assertEquals(expectedAnalyticId, dto.getAnalyticId());
        assertEquals(expectedOrder, dto.getExecutionOrder());
    }

    @Test
    public void testEquals_SameObject() {
        CompositeStructureDTO dto = new CompositeStructureDTO(1L, 2L, 3L, 1);
        assertTrue(dto.equals(dto));
    }

    @Test
    public void testEquals_EqualObjects() {
        CompositeStructureDTO dto1 = new CompositeStructureDTO(1L, 2L, 3L, 1);
        CompositeStructureDTO dto2 = new CompositeStructureDTO(1L, 2L, 3L, 1);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_NotEqualObjects() {
        CompositeStructureDTO dto1 = new CompositeStructureDTO(1L, 2L, 3L, 1);
        CompositeStructureDTO dto2 = new CompositeStructureDTO(4L, 5L, 6L, 2);

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testEquals_NullObject() {
        CompositeStructureDTO dto = new CompositeStructureDTO(1L, 2L, 3L, 1);
        assertFalse(dto.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        CompositeStructureDTO dto = new CompositeStructureDTO(1L, 2L, 3L, 1);
        String otherObject = "String";
        assertFalse(dto.equals(otherObject));
    }

    @Test
    public void testEquals_WithNullFields() {
        CompositeStructureDTO dto1 = new CompositeStructureDTO(null, null, null, null);
        CompositeStructureDTO dto2 = new CompositeStructureDTO(null, null, null, null);

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testEquals_PartialNullFields() {
        CompositeStructureDTO dto1 = new CompositeStructureDTO(1L, null, 3L, null);
        CompositeStructureDTO dto2 = new CompositeStructureDTO(1L, null, 3L, null);
        CompositeStructureDTO dto3 = new CompositeStructureDTO(1L, 2L, 3L, 1);

        assertTrue(dto1.equals(dto2));
        assertFalse(dto1.equals(dto3));
    }

    @Test
    public void testHashCode_Consistency() {
        CompositeStructureDTO dto = new CompositeStructureDTO(1L, 2L, 3L, 1);
        int firstHash = dto.hashCode();
        int secondHash = dto.hashCode();

        assertEquals(firstHash, secondHash);
    }

    @Test
    public void testHashCode_EqualObjectsHaveSameHash() {
        CompositeStructureDTO dto1 = new CompositeStructureDTO(1L, 2L, 3L, 1);
        CompositeStructureDTO dto2 = new CompositeStructureDTO(1L, 2L, 3L, 1);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testCompleteCycle_EntityToDTOToEntity() {
        CompositeStructure originalEntity = new CompositeStructure(100L, 200L, 300L, 5);

        CompositeStructureDTO dto = CompositeStructureDTO.fromEntity(originalEntity);
        CompositeStructure restoredEntity = dto.toEntity();

        assertEquals(originalEntity.getId(), restoredEntity.getId());
        assertEquals(originalEntity.getComposite_id(), restoredEntity.getComposite_id());
        assertEquals(originalEntity.getAnalytic_id(), restoredEntity.getAnalytic_id());
        assertEquals(originalEntity.getOrder(), restoredEntity.getOrder());
    }

    @Test
    public void testCompleteCycle_DTOToEntityToDTO() {
        CompositeStructureDTO originalDTO = new CompositeStructureDTO(400L, 500L, 600L, 10);

        CompositeStructure entity = originalDTO.toEntity();
        CompositeStructureDTO restoredDTO = CompositeStructureDTO.fromEntity(entity);

        assertEquals(originalDTO.getId(), restoredDTO.getId());
        assertEquals(originalDTO.getCompositeId(), restoredDTO.getCompositeId());
        assertEquals(originalDTO.getAnalyticId(), restoredDTO.getAnalyticId());
        assertEquals(originalDTO.getExecutionOrder(), restoredDTO.getExecutionOrder());
    }

    @Test
    public void testNegativeOrder() {
        CompositeStructureDTO dto = new CompositeStructureDTO(1L, 2L, 3L, -1);
        assertEquals(-1, dto.getExecutionOrder());

        CompositeStructure entity = dto.toEntity();
        assertEquals(-1, entity.getOrder());
    }

    @Test
    public void testZeroOrder() {
        CompositeStructureDTO dto = new CompositeStructureDTO(1L, 2L, 3L, 0);
        assertEquals(0, dto.getExecutionOrder());
    }
}