package ru.ssau.tk.swc.labs.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateCompPointDTOTest {

    @Test
    public void testDefaultConstructor() {
        CreateCompPointDTO dto = new CreateCompPointDTO();
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
        assertNull(dto.getFunID());
    }

    @Test
    public void testParameterizedConstructor() {
        double expectedX = 12.5;
        double expectedY = 18.3;
        Long expectedFunID = 9L;

        CreateCompPointDTO dto = new CreateCompPointDTO(expectedX, expectedY, expectedFunID);

        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testSettersAndGetters() {
        CreateCompPointDTO dto = new CreateCompPointDTO();
        double expectedX = 45.6;
        double expectedY = 78.9;
        Long expectedFunID = 25L;

        dto.setX(expectedX);
        dto.setY(expectedY);
        dto.setFunID(expectedFunID);

        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testPositiveCoordinates() {
        CreateCompPointDTO dto = new CreateCompPointDTO(7.3, 11.5, 14L);
        assertEquals(7.3, dto.getX());
        assertEquals(11.5, dto.getY());
        assertEquals(14L, dto.getFunID());
    }

    @Test
    public void testNegativeCoordinates() {
        CreateCompPointDTO dto = new CreateCompPointDTO(-15.2, -25.7, 8L);
        assertEquals(-15.2, dto.getX());
        assertEquals(-25.7, dto.getY());
        assertEquals(8L, dto.getFunID());
    }

    @Test
    public void testZeroCoordinates() {
        CreateCompPointDTO dto = new CreateCompPointDTO(0.0, 0.0, 5L);
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
        assertEquals(5L, dto.getFunID());
    }

    @Test
    public void testLargeCoordinates() {
        CreateCompPointDTO dto = new CreateCompPointDTO(1500.0, 2500.0, 12L);
        assertEquals(1500.0, dto.getX());
        assertEquals(2500.0, dto.getY());
        assertEquals(12L, dto.getFunID());
    }

    @Test
    public void testDecimalPrecision() {
        CreateCompPointDTO dto = new CreateCompPointDTO(3.14159265, 2.71828182, 22L);
        assertEquals(3.14159265, dto.getX());
        assertEquals(2.71828182, dto.getY());
        assertEquals(22L, dto.getFunID());
    }

    @Test
    public void testSetNullFunID() {
        CreateCompPointDTO dto = new CreateCompPointDTO(2.0, 3.0, 4L);
        dto.setFunID(null);
        assertNull(dto.getFunID());
    }

    @Test
    public void testZeroFunID() {
        CreateCompPointDTO dto = new CreateCompPointDTO(1.0, 2.0, 0L);
        assertEquals(0L, dto.getFunID());
    }

    @Test
    public void testNegativeFunID() {
        CreateCompPointDTO dto = new CreateCompPointDTO(1.0, 2.0, -7L);
        assertEquals(-7L, dto.getFunID());
    }

    @Test
    public void testLargeFunID() {
        CreateCompPointDTO dto = new CreateCompPointDTO(1.0, 2.0, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, dto.getFunID());
    }

    @Test
    public void testUpdateValues() {
        CreateCompPointDTO dto = new CreateCompPointDTO(8.0, 16.0, 3L);

        dto.setX(32.0);
        dto.setY(64.0);
        dto.setFunID(6L);

        assertEquals(32.0, dto.getX());
        assertEquals(64.0, dto.getY());
        assertEquals(6L, dto.getFunID());
    }

    @Test
    public void testSameValuesMultipleTimes() {
        CreateCompPointDTO dto = new CreateCompPointDTO();

        dto.setX(1.5);
        dto.setY(2.5);
        dto.setFunID(33L);

        assertEquals(1.5, dto.getX());
        assertEquals(2.5, dto.getY());
        assertEquals(33L, dto.getFunID());

        dto.setX(1.5);
        dto.setY(2.5);
        dto.setFunID(33L);

        assertEquals(1.5, dto.getX());
        assertEquals(2.5, dto.getY());
        assertEquals(33L, dto.getFunID());
    }

    @Test
    public void testConstructorWithNullFunID() {
        CreateCompPointDTO dto = new CreateCompPointDTO(3.0, 4.0, null);
        assertEquals(3.0, dto.getX());
        assertEquals(4.0, dto.getY());
        assertNull(dto.getFunID());
    }

    @Test
    public void testVerySmallCoordinates() {
        CreateCompPointDTO dto = new CreateCompPointDTO(0.000003, 0.000004, 11L);
        assertEquals(0.000003, dto.getX());
        assertEquals(0.000004, dto.getY());
        assertEquals(11L, dto.getFunID());
    }

    @Test
    public void testMixedSignCoordinates() {
        CreateCompPointDTO dto = new CreateCompPointDTO(-8.5, 12.5, 19L);
        assertEquals(-8.5, dto.getX());
        assertEquals(12.5, dto.getY());
        assertEquals(19L, dto.getFunID());
    }

    @Test
    public void testExactIntegerCoordinates() {
        CreateCompPointDTO dto = new CreateCompPointDTO(10.0, 20.0, 30L);
        assertEquals(10.0, dto.getX());
        assertEquals(20.0, dto.getY());
        assertEquals(30L, dto.getFunID());
    }

    @Test
    public void testVeryLargeCoordinates() {
        CreateCompPointDTO dto = new CreateCompPointDTO(999999.999, 888888.888, 777L);
        assertEquals(999999.999, dto.getX());
        assertEquals(888888.888, dto.getY());
        assertEquals(777L, dto.getFunID());
    }

    @Test
    public void testSetValuesInDifferentOrder() {
        CreateCompPointDTO dto = new CreateCompPointDTO();

        dto.setFunID(55L);
        dto.setY(100.0);
        dto.setX(50.0);

        assertEquals(50.0, dto.getX());
        assertEquals(100.0, dto.getY());
        assertEquals(55L, dto.getFunID());
    }

    @Test
    public void testCoordinatesWithManyDecimals() {
        CreateCompPointDTO dto = new CreateCompPointDTO(123.456789012, 987.654321098, 1234L);
        assertEquals(123.456789012, dto.getX());
        assertEquals(987.654321098, dto.getY());
        assertEquals(1234L, dto.getFunID());
    }
}