package com.github.devrafaht.core.tracker;

import core.tracker.ByteUrlEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ByteUrlEncoder Tests")
class ByteUrlEncoderTest {

    @Test
    @DisplayName("Should return the same string for safe characters (a-z, A-Z, 0-9, .-_~)")
    void shouldReturnSameStringForSafeCharacters() {
        String input = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-_~";
        byte[] data = input.getBytes(StandardCharsets.US_ASCII);

        String result = ByteUrlEncoder.encode(data);

        assertEquals(input, result);
    }

    @Test
    @DisplayName("Should percent-encode unsafe characters")
    void shouldPercentEncodeUnsafeCharacters() {
        byte[] data = new byte[] { ' ', '\n', 0x00 };

        String result = ByteUrlEncoder.encode(data);

        assertEquals("%20%0a%00", result);
    }

    @Test
    @DisplayName("Should encode mixed binary data correctly")
    void shouldEncodeMixedBinaryData() {
        byte[] data = new byte[] { 0x12, (byte) 0xFF, 0x41 };

        String result = ByteUrlEncoder.encode(data);

        assertEquals("%12%ffA", result);
    }
}