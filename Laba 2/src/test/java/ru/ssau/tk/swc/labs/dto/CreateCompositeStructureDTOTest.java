package ru.ssau.tk.swc.labs.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateCompositeStructureDTOTest {

    @Test
    public void testDefaultConstructor() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO();
        assertNull(dto.getCompositeId());
        assertNull(dto.getAnalyticId());
        assertNull(dto.getExecutionOrder());
    }

    @Test
    public void testParameterizedConstructor() {
        Long expectedCompositeId = 2L;
        Long expectedAnalyticId = 3L;
        Integer expectedOrder = 1;

        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(expectedCompositeId, expectedAnalyticId, expectedOrder);

        assertEquals(expectedCompositeId, dto.getCompositeId());
        assertEquals(expectedAnalyticId, dto.getAnalyticId());
        assertEquals(expectedOrder, dto.getExecutionOrder());
    }

    @Test
    public void testSettersAndGetters() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO();
        Long expectedCompositeId = 20L;
        Long expectedAnalyticId = 30L;
        Integer expectedOrder = 5;

        dto.setCompositeId(expectedCompositeId);
        dto.setAnalyticId(expectedAnalyticId);
        dto.setExecutionOrder(expectedOrder);

        assertEquals(expectedCompositeId, dto.getCompositeId());
        assertEquals(expectedAnalyticId, dto.getAnalyticId());
        assertEquals(expectedOrder, dto.getExecutionOrder());
    }

    @Test
    public void testPositiveValues() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(10L, 15L, 3);
        assertEquals(10L, dto.getCompositeId());
        assertEquals(15L, dto.getAnalyticId());
        assertEquals(3, dto.getExecutionOrder());
    }

    @Test
    public void testZeroValues() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(0L, 0L, 0);
        assertEquals(0L, dto.getCompositeId());
        assertEquals(0L, dto.getAnalyticId());
        assertEquals(0, dto.getExecutionOrder());
    }

    @Test
    public void testNegativeValues() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(-5L, -10L, -1);
        assertEquals(-5L, dto.getCompositeId());
        assertEquals(-10L, dto.getAnalyticId());
        assertEquals(-1, dto.getExecutionOrder());
    }

    @Test
    public void testLargeValues() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(Long.MAX_VALUE, Long.MAX_VALUE, Integer.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, dto.getCompositeId());
        assertEquals(Long.MAX_VALUE, dto.getAnalyticId());
        assertEquals(Integer.MAX_VALUE, dto.getExecutionOrder());
    }

    @Test
    public void testExecutionOrderMinimumValue() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(1L, 2L, 1);
        assertEquals(1, dto.getExecutionOrder());
    }

    @Test
    public void testExecutionOrderLargeValue() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(1L, 2L, 1000);
        assertEquals(1000, dto.getExecutionOrder());
    }

    @Test
    public void testSetNullValues() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(1L, 2L, 3);

        dto.setCompositeId(null);
        dto.setAnalyticId(null);
        dto.setExecutionOrder(null);

        assertNull(dto.getCompositeId());
        assertNull(dto.getAnalyticId());
        assertNull(dto.getExecutionOrder());
    }

    @Test
    public void testConstructorWithNullValues() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(null, null, null);
        assertNull(dto.getCompositeId());
        assertNull(dto.getAnalyticId());
        assertNull(dto.getExecutionOrder());
    }

    @Test
    public void testUpdateValues() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(5L, 10L, 2);

        dto.setCompositeId(15L);
        dto.setAnalyticId(20L);
        dto.setExecutionOrder(5);

        assertEquals(15L, dto.getCompositeId());
        assertEquals(20L, dto.getAnalyticId());
        assertEquals(5, dto.getExecutionOrder());
    }

    @Test
    public void testSameValuesMultipleTimes() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO();

        dto.setCompositeId(100L);
        dto.setAnalyticId(200L);
        dto.setExecutionOrder(10);

        assertEquals(100L, dto.getCompositeId());
        assertEquals(200L, dto.getAnalyticId());
        assertEquals(10, dto.getExecutionOrder());

        dto.setCompositeId(100L);
        dto.setAnalyticId(200L);
        dto.setExecutionOrder(10);

        assertEquals(100L, dto.getCompositeId());
        assertEquals(200L, dto.getAnalyticId());
        assertEquals(10, dto.getExecutionOrder());
    }

    @Test
    public void testEqualIds() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(5L, 5L, 1);
        assertEquals(5L, dto.getCompositeId());
        assertEquals(5L, dto.getAnalyticId());
        assertEquals(1, dto.getExecutionOrder());
    }

    @Test
    public void testNegativeExecutionOrder() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(1L, 2L, -5);
        assertEquals(-5, dto.getExecutionOrder());
    }

    @Test
    public void testZeroExecutionOrder() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(1L, 2L, 0);
        assertEquals(0, dto.getExecutionOrder());
    }

    @Test
    public void testLargeLongValues() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(1000000L, 2000000L, 50);
        assertEquals(1000000L, dto.getCompositeId());
        assertEquals(2000000L, dto.getAnalyticId());
        assertEquals(50, dto.getExecutionOrder());
    }

    @Test
    public void testSetValuesInDifferentOrder() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO();

        dto.setExecutionOrder(3);
        dto.setAnalyticId(2L);
        dto.setCompositeId(1L);

        assertEquals(1L, dto.getCompositeId());
        assertEquals(2L, dto.getAnalyticId());
        assertEquals(3, dto.getExecutionOrder());
    }

    @Test
    public void testMixedPositiveNegative() {
        CreateCompositeStructureDTO dto = new CreateCompositeStructureDTO(-5L, 10L, -2);
        assertEquals(-5L, dto.getCompositeId());
        assertEquals(10L, dto.getAnalyticId());
        assertEquals(-2, dto.getExecutionOrder());
    }
}