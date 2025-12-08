package ru.ssau.tk.swc.labs.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateTabFunDTOTest {

    @Test
    public void testDefaultConstructor() {
        CreateTabFunDTO dto = new CreateTabFunDTO();
        assertNull(dto.getType());
    }

    @Test
    public void testParameterizedConstructor() {
        String expectedType = "Linear";

        CreateTabFunDTO dto = new CreateTabFunDTO(expectedType);

        assertEquals(expectedType, dto.getType());
    }

    @Test
    public void testSettersAndGetters() {
        CreateTabFunDTO dto = new CreateTabFunDTO();
        String expectedType = "Quadratic";

        dto.setType(expectedType);

        assertEquals(expectedType, dto.getType());
    }

    @Test
    public void testTypeBoundaryValues() {
        CreateTabFunDTO dto1 = new CreateTabFunDTO("T");
        CreateTabFunDTO dto2 = new CreateTabFunDTO("T".repeat(50));

        assertEquals("T", dto1.getType());
        assertEquals(50, dto2.getType().length());
    }

    @Test
    public void testEmptyStringType() {
        CreateTabFunDTO dto = new CreateTabFunDTO("");
        assertEquals("", dto.getType());
    }

    @Test
    public void testWhitespaceType() {
        CreateTabFunDTO dto = new CreateTabFunDTO("   ");
        assertEquals("   ", dto.getType());
    }

    @Test
    public void testNullType() {
        CreateTabFunDTO dto = new CreateTabFunDTO();
        dto.setType(null);
        assertNull(dto.getType());
    }

    @Test
    public void testTypeWithSpaces() {
        CreateTabFunDTO dto = new CreateTabFunDTO("Linear Function");
        assertEquals("Linear Function", dto.getType());
    }

    @Test
    public void testTypeWithSpecialCharacters() {
        CreateTabFunDTO dto = new CreateTabFunDTO("Type_1-2.3");
        assertEquals("Type_1-2.3", dto.getType());
    }

    @Test
    public void testTypeExactly50Chars() {
        String type50 = "T".repeat(50);
        CreateTabFunDTO dto = new CreateTabFunDTO(type50);
        assertEquals(50, dto.getType().length());
    }

    @Test
    public void testType51Chars() {
        String type51 = "T".repeat(51);
        CreateTabFunDTO dto = new CreateTabFunDTO(type51);
        assertEquals(51, dto.getType().length());
    }

    @Test
    public void testUpdateType() {
        CreateTabFunDTO dto = new CreateTabFunDTO("Initial Type");

        dto.setType("Updated Type");

        assertEquals("Updated Type", dto.getType());
    }

    @Test
    public void testSameTypeMultipleTimes() {
        CreateTabFunDTO dto = new CreateTabFunDTO();

        dto.setType("Test Type");
        assertEquals("Test Type", dto.getType());

        dto.setType("Test Type");
        assertEquals("Test Type", dto.getType());
    }

    @Test
    public void testConstructorWithNull() {
        CreateTabFunDTO dto = new CreateTabFunDTO(null);
        assertNull(dto.getType());
    }

    @Test
    public void testTypeWithNumbers() {
        CreateTabFunDTO dto = new CreateTabFunDTO("Type123");
        assertEquals("Type123", dto.getType());
    }

    @Test
    public void testTypeWithMixedCase() {
        CreateTabFunDTO dto = new CreateTabFunDTO("TaBuLaTeD_FuNcTiOn");
        assertEquals("TaBuLaTeD_FuNcTiOn", dto.getType());
    }

    @Test
    public void testTypeWithPunctuation() {
        CreateTabFunDTO dto = new CreateTabFunDTO("Type.A.B.C");
        assertEquals("Type.A.B.C", dto.getType());
    }

    @Test
    public void testTypeOneCharacter() {
        CreateTabFunDTO dto = new CreateTabFunDTO("T");
        assertEquals("T", dto.getType());
    }

    @Test
    public void testTypeWithUnicode() {
        CreateTabFunDTO dto = new CreateTabFunDTO("ТипФункции");
        assertEquals("ТипФункции", dto.getType());
    }

    @Test
    public void testSetEmptyString() {
        CreateTabFunDTO dto = new CreateTabFunDTO("Initial");
        dto.setType("");
        assertEquals("", dto.getType());
    }

    @Test
    public void testCommonFunctionTypes() {
        CreateTabFunDTO dto1 = new CreateTabFunDTO("Linear");
        CreateTabFunDTO dto2 = new CreateTabFunDTO("Quadratic");
        CreateTabFunDTO dto3 = new CreateTabFunDTO("Exponential");

        assertEquals("Linear", dto1.getType());
        assertEquals("Quadratic", dto2.getType());
        assertEquals("Exponential", dto3.getType());
    }

    @Test
    public void testTypeWithHyphens() {
        CreateTabFunDTO dto = new CreateTabFunDTO("Step-Function");
        assertEquals("Step-Function", dto.getType());
    }

    @Test
    public void testTypeWithUnderscores() {
        CreateTabFunDTO dto = new CreateTabFunDTO("table_function_type");
        assertEquals("table_function_type", dto.getType());
    }

    @Test
    public void testTypeWithMultipleWords() {
        CreateTabFunDTO dto = new CreateTabFunDTO("Piecewise Linear Function");
        assertEquals("Piecewise Linear Function", dto.getType());
    }
}