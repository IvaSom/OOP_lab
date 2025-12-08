package ru.ssau.tk.swc.labs.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateTabPointDTOTest {

    @Test
    public void testDefaultConstructor() {
        CreateTabPointDTO dto = new CreateTabPointDTO();
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
        assertEquals(0.0, dto.getDerive());
        assertNull(dto.getFunID());
    }

    @Test
    public void testParameterizedConstructor() {
        double expectedX = 5.5;
        double expectedY = 10.5;
        double expectedDerive = 2.0;
        Long expectedFunID = 3L;

        CreateTabPointDTO dto = new CreateTabPointDTO(expectedX, expectedY, expectedDerive, expectedFunID);

        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedDerive, dto.getDerive());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testSettersAndGetters() {
        CreateTabPointDTO dto = new CreateTabPointDTO();
        double expectedX = 15.7;
        double expectedY = 25.9;
        double expectedDerive = 3.5;
        Long expectedFunID = 12L;

        dto.setX(expectedX);
        dto.setY(expectedY);
        dto.setDerive(expectedDerive);
        dto.setFunID(expectedFunID);

        assertEquals(expectedX, dto.getX());
        assertEquals(expectedY, dto.getY());
        assertEquals(expectedDerive, dto.getDerive());
        assertEquals(expectedFunID, dto.getFunID());
    }

    @Test
    public void testPositiveValues() {
        CreateTabPointDTO dto = new CreateTabPointDTO(2.0, 4.0, 1.0, 5L);
        assertEquals(2.0, dto.getX());
        assertEquals(4.0, dto.getY());
        assertEquals(1.0, dto.getDerive());
        assertEquals(5L, dto.getFunID());
    }

    @Test
    public void testNegativeValues() {
        CreateTabPointDTO dto = new CreateTabPointDTO(-3.0, -6.0, -2.0, -7L);
        assertEquals(-3.0, dto.getX());
        assertEquals(-6.0, dto.getY());
        assertEquals(-2.0, dto.getDerive());
        assertEquals(-7L, dto.getFunID());
    }

    @Test
    public void testZeroValues() {
        CreateTabPointDTO dto = new CreateTabPointDTO(0.0, 0.0, 0.0, 0L);
        assertEquals(0.0, dto.getX());
        assertEquals(0.0, dto.getY());
        assertEquals(0.0, dto.getDerive());
        assertEquals(0L, dto.getFunID());
    }

    @Test
    public void testLargeValues() {
        CreateTabPointDTO dto = new CreateTabPointDTO(1000.0, 2000.0, 500.0, 10000L);
        assertEquals(1000.0, dto.getX());
        assertEquals(2000.0, dto.getY());
        assertEquals(500.0, dto.getDerive());
        assertEquals(10000L, dto.getFunID());
    }

    @Test
    public void testDecimalPrecision() {
        CreateTabPointDTO dto = new CreateTabPointDTO(1.234567, 2.345678, 3.456789, 4L);
        assertEquals(1.234567, dto.getX());
        assertEquals(2.345678, dto.getY());
        assertEquals(3.456789, dto.getDerive());
        assertEquals(4L, dto.getFunID());
    }

    @Test
    public void testSetNullFunID() {
        CreateTabPointDTO dto = new CreateTabPointDTO(1.0, 2.0, 3.0, 4L);
        dto.setFunID(null);
        assertNull(dto.getFunID());
    }

    @Test
    public void testZeroDerive() {
        CreateTabPointDTO dto = new CreateTabPointDTO(1.0, 2.0, 0.0, 3L);
        assertEquals(0.0, dto.getDerive());
    }

    @Test
    public void testLargeDerive() {
        CreateTabPointDTO dto = new CreateTabPointDTO(1.0, 2.0, 100.5, 4L);
        assertEquals(100.5, dto.getDerive());
    }

    @Test
    public void testUpdateValues() {
        CreateTabPointDTO dto = new CreateTabPointDTO(5.0, 10.0, 2.0, 1L);

        dto.setX(15.0);
        dto.setY(20.0);
        dto.setDerive(4.0);
        dto.setFunID(2L);

        assertEquals(15.0, dto.getX());
        assertEquals(20.0, dto.getY());
        assertEquals(4.0, dto.getDerive());
        assertEquals(2L, dto.getFunID());
    }

    @Test
    public void testSameValuesMultipleTimes() {
        CreateTabPointDTO dto = new CreateTabPointDTO();

        dto.setX(3.14);
        dto.setY(2.71);
        dto.setDerive(1.61);
        dto.setFunID(42L);

        assertEquals(3.14, dto.getX());
        assertEquals(2.71, dto.getY());
        assertEquals(1.61, dto.getDerive());
        assertEquals(42L, dto.getFunID());

        dto.setX(3.14);
        dto.setY(2.71);
        dto.setDerive(1.61);
        dto.setFunID(42L);

        assertEquals(3.14, dto.getX());
        assertEquals(2.71, dto.getY());
        assertEquals(1.61, dto.getDerive());
        assertEquals(42L, dto.getFunID());
    }

    @Test
    public void testConstructorWithNullFunID() {
        CreateTabPointDTO dto = new CreateTabPointDTO(1.0, 2.0, 3.0, null);
        assertNull(dto.getFunID());
    }

    @Test
    public void testVerySmallValues() {
        CreateTabPointDTO dto = new CreateTabPointDTO(0.000001, 0.000002, 0.000003, 7L);
        assertEquals(0.000001, dto.getX());
        assertEquals(0.000002, dto.getY());
        assertEquals(0.000003, dto.getDerive());
        assertEquals(7L, dto.getFunID());
    }

    @Test
    public void testMixedSignValues() {
        CreateTabPointDTO dto = new CreateTabPointDTO(-5.5, 10.5, -2.5, -9L);
        assertEquals(-5.5, dto.getX());
        assertEquals(10.5, dto.getY());
        assertEquals(-2.5, dto.getDerive());
        assertEquals(-9L, dto.getFunID());
    }

    @Test
    public void testExactIntegerValues() {
        CreateTabPointDTO dto = new CreateTabPointDTO(10.0, 20.0, 5.0, 30L);
        assertEquals(10.0, dto.getX());
        assertEquals(20.0, dto.getY());
        assertEquals(5.0, dto.getDerive());
        assertEquals(30L, dto.getFunID());
    }

    @Test
    public void testVeryLargeCoordinates() {
        CreateTabPointDTO dto = new CreateTabPointDTO(999999.999, 888888.888, 777777.777, 5555L);
        assertEquals(999999.999, dto.getX());
        assertEquals(888888.888, dto.getY());
        assertEquals(777777.777, dto.getDerive());
        assertEquals(5555L, dto.getFunID());
    }

    @Test
    public void testSetValuesInDifferentOrder() {
        CreateTabPointDTO dto = new CreateTabPointDTO();

        dto.setFunID(99L);
        dto.setDerive(2.5);
        dto.setY(10.0);
        dto.setX(5.0);

        assertEquals(5.0, dto.getX());
        assertEquals(10.0, dto.getY());
        assertEquals(2.5, dto.getDerive());
        assertEquals(99L, dto.getFunID());
    }

    @Test
    public void testCoordinatesWithManyDecimals() {
        CreateTabPointDTO dto = new CreateTabPointDTO(123.456789, 987.654321, 555.666777, 1234L);
        assertEquals(123.456789, dto.getX());
        assertEquals(987.654321, dto.getY());
        assertEquals(555.666777, dto.getDerive());
        assertEquals(1234L, dto.getFunID());
    }

    @Test
    public void testDeriveEqualToCoordinates() {
        CreateTabPointDTO dto = new CreateTabPointDTO(2.0, 4.0, 2.0, 1L);
        assertEquals(2.0, dto.getX());
        assertEquals(4.0, dto.getY());
        assertEquals(2.0, dto.getDerive());
        assertEquals(1L, dto.getFunID());
    }

    @Test
    public void testNegativeDerivePositiveCoordinates() {
        CreateTabPointDTO dto = new CreateTabPointDTO(3.0, 9.0, -1.5, 2L);
        assertEquals(3.0, dto.getX());
        assertEquals(9.0, dto.getY());
        assertEquals(-1.5, dto.getDerive());
        assertEquals(2L, dto.getFunID());
    }
}