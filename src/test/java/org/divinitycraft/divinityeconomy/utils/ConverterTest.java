package org.divinitycraft.divinityeconomy.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConverterTest {

    @Test
    void getTicks_multipliesSecondsByTwenty() {
        assertEquals(200, Converter.getTicks(10));
        assertEquals(0, Converter.getTicks(0));
    }

    @Test
    void getDouble_parsesValidNumber() {
        assertEquals(3.14, Converter.getDouble("3.14"));
        assertEquals(-5.0, Converter.getDouble("-5"));
    }

    @Test
    void getDouble_returnsZeroOnInvalidInput() {
        assertEquals(0, Converter.getDouble("not-a-number"));
        assertEquals(0, Converter.getDouble(""));
    }

    @Test
    void getInt_parsesValidNumber() {
        assertEquals(42, Converter.getInt("42"));
        assertEquals(-7, Converter.getInt("-7"));
    }

    @Test
    void getInt_returnsZeroOnInvalidInput() {
        assertEquals(0, Converter.getInt("abc"));
        assertEquals(0, Converter.getInt("3.5"));
    }

    @Test
    void getLong_parsesValidNumber() {
        assertEquals(9999999999L, Converter.getLong("9999999999"));
    }

    @Test
    void getLong_returnsZeroOnInvalidInput() {
        assertEquals(0L, Converter.getLong("nope"));
    }

    @Test
    void getBoolean_isCaseInsensitiveForTrue() {
        assertTrue(Converter.getBoolean("true"));
        assertTrue(Converter.getBoolean("TRUE"));
        assertTrue(Converter.getBoolean("True"));
    }

    @Test
    void getBoolean_returnsFalseForAnythingElse() {
        assertFalse(Converter.getBoolean("false"));
        assertFalse(Converter.getBoolean("yes"));
        assertFalse(Converter.getBoolean(""));
    }

    @ParameterizedTest
    @CsvSource({
            "5, 0, 10, 5",
            "-5, 0, 10, 0",
            "15, 0, 10, 10",
            "0, 0, 10, 0",
            "10, 0, 10, 10"
    })
    void constrainInt_clampsToBounds(int value, int min, int max, int expected) {
        assertEquals(expected, Converter.constrainInt(value, min, max));
    }

    @ParameterizedTest
    @CsvSource({
            "5, 0, 10, 5",
            "-5, 0, 10, 0",
            "15, 0, 10, 10"
    })
    void constrainLong_clampsToBounds(long value, long min, long max, long expected) {
        assertEquals(expected, Converter.constrainLong(value, min, max));
    }

    @ParameterizedTest
    @CsvSource({
            "5.5, 0.0, 10.0, 5.5",
            "-5.5, 0.0, 10.0, 0.0",
            "15.5, 0.0, 10.0, 10.0"
    })
    void constrainDouble_clampsToBounds(double value, double min, double max, double expected) {
        assertEquals(expected, Converter.constrainDouble(value, min, max));
    }
}
