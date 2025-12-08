package ru.ssau.tk.swc.labs.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateCompFunDTOTest {

    @Test
    public void testDefaultConstructor() {
        CreateCompFunDTO dto = new CreateCompFunDTO();
        assertNull(dto.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        String expectedName = "Composite Function";

        CreateCompFunDTO dto = new CreateCompFunDTO(expectedName);

        assertEquals(expectedName, dto.getName());
    }

    @Test
    public void testSettersAndGetters() {
        CreateCompFunDTO dto = new CreateCompFunDTO();
        String expectedName = "Setter Test";

        dto.setName(expectedName);

        assertEquals(expectedName, dto.getName());
    }

    @Test
    public void testNameBoundaryValues() {
        CreateCompFunDTO dto1 = new CreateCompFunDTO("A");
        CreateCompFunDTO dto2 = new CreateCompFunDTO("A".repeat(50));

        assertEquals("A", dto1.getName());
        assertEquals(50, dto2.getName().length());
    }

    @Test
    public void testEmptyStringName() {
        CreateCompFunDTO dto = new CreateCompFunDTO("");
        assertEquals("", dto.getName());
    }

    @Test
    public void testWhitespaceName() {
        CreateCompFunDTO dto = new CreateCompFunDTO("   ");
        assertEquals("   ", dto.getName());
    }

    @Test
    public void testNullName() {
        CreateCompFunDTO dto = new CreateCompFunDTO();
        dto.setName(null);
        assertNull(dto.getName());
    }

    @Test
    public void testNameWithSpaces() {
        CreateCompFunDTO dto = new CreateCompFunDTO("Valid Composite Name");
        assertEquals("Valid Composite Name", dto.getName());
    }

    @Test
    public void testNameWithSpecialCharacters() {
        CreateCompFunDTO dto = new CreateCompFunDTO("Function-Name_123");
        assertEquals("Function-Name_123", dto.getName());
    }

    @Test
    public void testNameExactly50Chars() {
        String name50 = "A".repeat(50);
        CreateCompFunDTO dto = new CreateCompFunDTO(name50);
        assertEquals(50, dto.getName().length());
    }

    @Test
    public void testName51Chars() {
        String name51 = "A".repeat(51);
        CreateCompFunDTO dto = new CreateCompFunDTO(name51);
        assertEquals(51, dto.getName().length());
    }

    @Test
    public void testUpdateName() {
        CreateCompFunDTO dto = new CreateCompFunDTO("Initial Name");

        dto.setName("Updated Name");

        assertEquals("Updated Name", dto.getName());
    }

    @Test
    public void testSameNameMultipleTimes() {
        CreateCompFunDTO dto = new CreateCompFunDTO();

        dto.setName("Test Name");
        assertEquals("Test Name", dto.getName());

        dto.setName("Test Name");
        assertEquals("Test Name", dto.getName());
    }

    @Test
    public void testConstructorWithNull() {
        CreateCompFunDTO dto = new CreateCompFunDTO(null);
        assertNull(dto.getName());
    }

    @Test
    public void testNameWithNumbers() {
        CreateCompFunDTO dto = new CreateCompFunDTO("Function123");
        assertEquals("Function123", dto.getName());
    }

    @Test
    public void testNameWithMixedCase() {
        CreateCompFunDTO dto = new CreateCompFunDTO("CoMpOsItE_FuNcTiOn");
        assertEquals("CoMpOsItE_FuNcTiOn", dto.getName());
    }

    @Test
    public void testNameWithPunctuation() {
        CreateCompFunDTO dto = new CreateCompFunDTO("Composite.Function.Test");
        assertEquals("Composite.Function.Test", dto.getName());
    }

    @Test
    public void testNameOneCharacter() {
        CreateCompFunDTO dto = new CreateCompFunDTO("F");
        assertEquals("F", dto.getName());
    }

    @Test
    public void testNameWithUnicode() {
        CreateCompFunDTO dto = new CreateCompFunDTO("Функция");
        assertEquals("Функция", dto.getName());
    }

    @Test
    public void testSetEmptyString() {
        CreateCompFunDTO dto = new CreateCompFunDTO("Initial");
        dto.setName("");
        assertEquals("", dto.getName());
    }
}