package ru.ssau.tk.swc.labs.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateAnalFunDTOTest {

    @Test
    public void testDefaultConstructor() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO();
        assertNull(dto.getName());
        assertNull(dto.getType());
    }

    @Test
    public void testParameterizedConstructor() {
        String expectedName = "Test Function";
        Integer expectedType = 5;

        CreateAnalFunDTO dto = new CreateAnalFunDTO(expectedName, expectedType);

        assertEquals(expectedName, dto.getName());
        assertEquals(expectedType, dto.getType());
    }

    @Test
    public void testSettersAndGetters() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO();
        String expectedName = "Setter Test";
        Integer expectedType = 3;

        dto.setName(expectedName);
        dto.setType(expectedType);

        assertEquals(expectedName, dto.getName());
        assertEquals(expectedType, dto.getType());
    }

    @Test
    public void testNameBoundaryValues() {
        CreateAnalFunDTO dto1 = new CreateAnalFunDTO("A", 1);
        CreateAnalFunDTO dto2 = new CreateAnalFunDTO("A".repeat(100), 10);

        assertEquals("A", dto1.getName());
        assertEquals(1, dto1.getType());
        assertEquals(100, dto2.getName().length());
        assertEquals(10, dto2.getType());
    }

    @Test
    public void testWhitespaceName() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO("Test", 5);
        assertEquals("Test", dto.getName());
        assertEquals(5, dto.getType());
    }

    @Test
    public void testNameWithSpaces() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO("Valid Name With Spaces", 5);
        assertEquals("Valid Name With Spaces", dto.getName());
        assertEquals(5, dto.getType());
    }

    @Test
    public void testTypeExactlyOne() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO("Test", 1);
        assertEquals("Test", dto.getName());
        assertEquals(1, dto.getType());
    }

    @Test
    public void testTypeExactlyTen() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO("Test", 10);
        assertEquals("Test", dto.getName());
        assertEquals(10, dto.getType());
    }

    @Test
    public void testValidSpecialCharactersInName() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO("Function-Name_123", 5);
        assertEquals("Function-Name_123", dto.getName());
        assertEquals(5, dto.getType());
    }

    @Test
    public void testNullValues() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO();
        dto.setName(null);
        dto.setType(null);

        assertNull(dto.getName());
        assertNull(dto.getType());
    }

    @Test
    public void testEmptyStringName() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO("", 5);
        assertEquals("", dto.getName());
        assertEquals(5, dto.getType());
    }

    @Test
    public void testNegativeType() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO("Test", -5);
        assertEquals("Test", dto.getName());
        assertEquals(-5, dto.getType());
    }

    @Test
    public void testLargeType() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO("Test", 100);
        assertEquals("Test", dto.getName());
        assertEquals(100, dto.getType());
    }

    @Test
    public void testZeroType() {
        CreateAnalFunDTO dto = new CreateAnalFunDTO("Test", 0);
        assertEquals("Test", dto.getName());
        assertEquals(0, dto.getType());
    }
}