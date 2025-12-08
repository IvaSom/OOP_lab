package ru.ssau.tk.swc.labs.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateAnalPointDTOTest {

    @Test
    public void testDefaultConstructor() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO();
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
        assertNull(dto.getFunID());
    }

    @Test
    public void testParameterizedConstructor() {
        double expectedX = 10.5;
        double expectedY = 20.3;
        Long expectedFunID = 7L;

        CreateAnalPointDTO dto = new CreateAnalPointDTO(expectedX, expectedY, expectedFunID);

        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testSettersAndGetters() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO();
        double expectedX = 100.1;
        double expectedY = 200.2;
        Long expectedFunID = 50L;

        dto.setX(expectedX);
        dto.setY(expectedY);
        dto.setFunID(expectedFunID);

        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testPositiveCoordinates() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(15.7, 25.9, 12L);
        assertEquals(15.7, dto.getX());
        assertEquals(25.9, dto.getY());
        assertEquals(12L, dto.getFunID());
    }

    @Test
    public void testNegativeCoordinates() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(-10.5, -20.5, 3L);
        assertEquals(-10.5, dto.getX());
        assertEquals(-20.5, dto.getY());
        assertEquals(3L, dto.getFunID());
    }

    @Test
    public void testZeroCoordinates() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(0.0, 0.0, 5L);
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
        assertEquals(5L, dto.getFunID());
    }

    @Test
    public void testLargeCoordinates() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(1000.0, 2000.0, 8L);
        assertEquals(1000.0, dto.getX());
        assertEquals(2000.0, dto.getY());
        assertEquals(8L, dto.getFunID());
    }

    @Test
    public void testDecimalPrecision() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(1.23456789, 9.87654321, 15L);
        assertEquals(1.23456789, dto.getX());
        assertEquals(9.87654321, dto.getY());
        assertEquals(15L, dto.getFunID());
    }

    @Test
    public void testSetNullFunID() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(1.0, 2.0, 3L);
        dto.setFunID(null);
        assertNull(dto.getFunID());
    }

    @Test
    public void testZeroFunID() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(1.0, 2.0, 0L);
        assertEquals(0L, dto.getFunID());
    }

    @Test
    public void testNegativeFunID() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(1.0, 2.0, -5L);
        assertEquals(-5L, dto.getFunID());
    }

    @Test
    public void testLargeFunID() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(1.0, 2.0, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, dto.getFunID());
    }

    @Test
    public void testUpdateValues() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(5.0, 10.0, 1L);

        dto.setX(15.0);
        dto.setY(20.0);
        dto.setFunID(2L);

        assertEquals(15.0, dto.getX());
        assertEquals(20.0, dto.getY());
        assertEquals(2L, dto.getFunID());
    }

    @Test
    public void testSameValuesMultipleTimes() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO();

        dto.setX(3.14);
        dto.setY(2.71);
        dto.setFunID(42L);

        assertEquals(3.14, dto.getX());
        assertEquals(2.71, dto.getY());
        assertEquals(42L, dto.getFunID());

        dto.setX(3.14);
        dto.setY(2.71);
        dto.setFunID(42L);

        assertEquals(3.14, dto.getX());
        assertEquals(2.71, dto.getY());
        assertEquals(42L, dto.getFunID());
    }

    @Test
    public void testConstructorWithNullFunID() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(1.0, 2.0, null);
        assertEquals(1.0, dto.getX());
        assertEquals(2.0, dto.getY());
        assertNull(dto.getFunID());
    }

    @Test
    public void testVerySmallCoordinates() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(0.000001, 0.000002, 7L);
        assertEquals(0.000001, dto.getX());
        assertEquals(0.000002, dto.getY());
        assertEquals(7L, dto.getFunID());
    }

    @Test
    public void testMixedSignCoordinates() {
        CreateAnalPointDTO dto = new CreateAnalPointDTO(-5.5, 10.5, 9L);
        assertEquals(-5.5, dto.getX());
        assertEquals(10.5, dto.getY());
        assertEquals(9L, dto.getFunID());
    }
}