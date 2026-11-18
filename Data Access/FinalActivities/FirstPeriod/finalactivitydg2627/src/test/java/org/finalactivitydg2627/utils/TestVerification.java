package org.finalactivitydg2627.utils;

import org.finalactivitydg2627.Utils.Verification;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class TestVerification {

    private String label;
    private String input;
    private Integer minIntOrLength;
    private Integer maxIntOrLength;
    private LocalDate minDate;
    private LocalDate maxDate;
    private Pattern pattern;


    @Test
    void verifyGeneralValue_fail_whiteSpaceOrEmpty() {
        label = "test";
        input = " ";
        var result = Verification.basicStringVerification(label, input, minIntOrLength, maxIntOrLength, pattern);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + Verification.VALUE_IS_EMPTY, result.message());
    }

    @Test
    void verifyGeneralValue_fail_null() {
        label = "test";
        var result = Verification.basicStringVerification(label, input, minIntOrLength, maxIntOrLength, pattern);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + Verification.VALUE_IS_NULL, result.message());
    }

    @Test
    void verifyString_success() {
        label = "test";
        input = "Y1234567";

        var result = Verification.basicStringVerification(label, input, minIntOrLength, maxIntOrLength, pattern);
        assertNotNull(result);
        assertNull(result.message());
        assertEquals(input, result.result());
    }


    @Test
    void verifyString_fail_minLength() {
        label = "test";
        input = "12";
        minIntOrLength = 3;
        var result = Verification.basicStringVerification(label, input, minIntOrLength, maxIntOrLength, pattern);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + " " + input + Verification.STR_TOO_SHORT, result.message());
    }

    @Test
    void verifyString_fail_maxLength() {
        label = "test";
        input = "1234";
        maxIntOrLength = 3;
        var result = Verification.basicStringVerification(label, input, minIntOrLength, maxIntOrLength, pattern);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + " " + input + Verification.STR_TOO_LONG, result.message());
    }

    @Test
    void verifyString_fail_pattern() {
        label = "test";
        input = "1234";
        pattern = Pattern.compile("[A-Z]\\d{7}");
        var result = Verification.basicStringVerification(label, input, minIntOrLength, maxIntOrLength, pattern);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + " " + input + Verification.STR_INVALID_PATTERN, result.message());
    }


    @Test
    void verifyInt_success() {
        label = "test";
        input = "1234";
        int resultTest = 1234;
        var result = Verification.basicIntegerVerification(label, input, minIntOrLength, maxIntOrLength);
        assertNotNull(result);
        assertNull(result.message());
        assertNotNull(result.result());
        assertEquals(resultTest, result.result());
    }

    @Test
    void verifyInt_fail_whiteSpaceOrEmpty() {
        label = "test";
        input = " ";
        var result = Verification.basicIntegerVerification(label, input, minIntOrLength, maxIntOrLength);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + Verification.VALUE_IS_EMPTY, result.message());
    }

    @Test
    void verifyInt_fail_minInt() {
        label = "test";
        input = "9";
        minIntOrLength = 10;
        var result = Verification.basicIntegerVerification(label, input, minIntOrLength, maxIntOrLength);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + " " + input + Verification.INT_VALUE_TOO_LOW, result.message());
    }

    @Test
    void verifyInt_fail_maxInt() {
        label = "test";
        input = "11";
        maxIntOrLength = 10;
        var result = Verification.basicIntegerVerification(label, input, minIntOrLength, maxIntOrLength);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + " " + input + Verification.INT_VALUE_TOO_HIGH, result.message());
    }

    @Test
    void verifyDate_success() {
        label = "test";
        input = "2015-11-13";
        var testResult = LocalDate.parse("2015-11-13");
        var result = Verification.basicDateVerification(label, input, minDate, maxDate);
        assertNotNull(result);
        assertNull(result.message());
        assertNotNull(result.result());
        assertEquals(testResult, result.result());
    }

    @Test
    void verifyDate_fail_minDate() {
        label = "test";
        input = "2015-11-13";
        minDate = LocalDate.parse("2015-11-14");
        var result = Verification.basicDateVerification(label, input, minDate, maxDate);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + " " + input + String.format(Verification.DATE_BELOW_MIN, minDate), result.message());
    }

    @Test
    void verifyDate_fail_maxDate() {
        label = "test";
        input = "2015-11-13";
        maxDate = LocalDate.parse("2015-11-12");
        var result = Verification.basicDateVerification(label, input, minDate, maxDate);
        assertNotNull(result);
        assertNull(result.result());
        assertNotNull(result.message());
        assertEquals(label + " " + input + String.format(Verification.DATE_ABOVE_MAX, maxDate), result.message());
    }
}
