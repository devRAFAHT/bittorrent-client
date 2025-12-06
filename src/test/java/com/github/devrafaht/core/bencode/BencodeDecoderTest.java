package com.github.devrafaht.core.bencode;

import core.bencode.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Bencode Decoder & Encoder Tests")
@ExtendWith(MockitoExtension.class)
public class BencodeDecoderTest {

    @Nested
    @DisplayName("Integers Tests")
    class Integers {
        @Test
        @DisplayName("Should decode positive integer")
        void shouldDecodePositiveInteger() throws IOException {
            byte[] data = "i42e".getBytes();
            BencodeElement<?> result = BencodeDecoder.decode(data);

            assertInstanceOf(BencodeNumber.class, result);
            assertEquals(42L, ((BencodeNumber) result).getValue());
        }

        @Test
        @DisplayName("Should decode negative integer")
        void shouldDecodeNegativeInteger() throws IOException {
            byte[] data = "i-500e".getBytes();
            BencodeElement<?> result = BencodeDecoder.decode(data);

            assertInstanceOf(BencodeNumber.class, result);
            assertEquals(-500L, ((BencodeNumber) result).getValue());
        }

        @Test
        @DisplayName("Should decode zero")
        void shouldDecodeZero() throws IOException {
            byte[] data = "i0e".getBytes();
            BencodeElement<?> result = BencodeDecoder.decode(data);

            assertEquals(0L, ((BencodeNumber) result).getValue());
        }

        @Test
        @DisplayName("Should throw exception when missing 'e' at the end")
        void shouldThrowExceptionWhenMissingEndCharacter() {
            byte[] data = "i42".getBytes();
            assertThrows(IOException.class, () -> BencodeDecoder.decode(data));
        }

        @Test
        @DisplayName("Should encode integer back to bytes")
        void shouldEncodeInteger() throws IOException {
            BencodeNumber bencodeNumber = new BencodeNumber(42L);
            assertArrayEquals("i42e".getBytes(), bencodeNumber.encode());
        }
    }

    @Nested
    @DisplayName("Byte Array Tests")
    class Strings {

        @Test
        @DisplayName("Should decode normal string")
        void shouldDecodeString() throws IOException {
            byte[] data = "5:hello".getBytes();
            BencodeElement<?> result = BencodeDecoder.decode(data);

            assertInstanceOf(BencodeByteArray.class, result);
            BencodeByteArray stringResult = (BencodeByteArray) result;

            assertEquals("hello", stringResult.asString());
            assertArrayEquals("hello".getBytes(StandardCharsets.UTF_8), stringResult.getValue());
        }

        @Test
        @DisplayName("Should decode empty string")
        void shouldDecodeEmptyString() throws IOException {
            byte[] data = "0:".getBytes();
            BencodeElement<?> result = BencodeDecoder.decode(data);

            assertEquals("", ((BencodeByteArray) result).asString());
            assertEquals(0, ((BencodeByteArray) result).getValue().length);
        }

        @Test
        @DisplayName("Should throw exception when string length does not match data")
        void shouldThrowExceptionWhenStringLengthMismatch() {
            byte[] data = "10:abc".getBytes();
            assertThrows(IOException.class, () -> BencodeDecoder.decode(data));
        }

        @Test
        @DisplayName("Should encode string back to bytes")
        void shouldEncodeString() throws IOException {
            byte[] bytes = "hello".getBytes(StandardCharsets.UTF_8);
            BencodeByteArray bencodeByteArray = new BencodeByteArray(bytes);
            assertArrayEquals("5:hello".getBytes(), bencodeByteArray.encode());
        }
    }

    @Nested
    @DisplayName("Lists Tests")
    class Lists {

        @Test
        @DisplayName("Should decode mixed list")
        void shouldDecodeList() throws IOException {
            byte[] data = "li42e3:fooe".getBytes();
            BencodeElement<?> result = BencodeDecoder.decode(data);

            assertInstanceOf(BencodeList.class, result);
            List<BencodeElement<?>> list = ((BencodeList) result).getValue();
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("Should decode empty list")
        void shouldDecodeEmptyList() throws IOException {
            byte[] data = "le".getBytes();
            BencodeElement<?> result = BencodeDecoder.decode(data);
            List<BencodeElement<?>> list = ((BencodeList) result).getValue();
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("Should encode list back to bytes using Mocks")
        void shouldEncodeList(@Mock BencodeElement<Object> mockElement) throws IOException {
            when(mockElement.encode()).thenReturn("ITEM".getBytes());

            List<BencodeElement<?>> list = new ArrayList<>();
            list.add(mockElement);

            BencodeList bencodeList = new BencodeList(list);

            byte[] result = bencodeList.encode();

            assertArrayEquals("lITEMe".getBytes(), result);
            verify(mockElement, times(1)).encode();
        }
    }

    @Nested
    @DisplayName("Dictionaries Tests")
    class Dictionaries {

        @Test
        @DisplayName("Should decode dictionary")
        void shouldDecodeDictionary() throws IOException {
            byte[] data = "d3:bar4:spam3:fooi42ee".getBytes();
            BencodeElement<?> result = BencodeDecoder.decode(data);
            assertInstanceOf(BencodeDictionary.class, result);
        }

        @Test
        @DisplayName("Should throw exception when dictionary key is not a string")
        void shouldThrowExceptionWhenDictionaryKeyIsNotString() {
            byte[] data = "di42e5:valuee".getBytes();
            Exception exception = assertThrows(IOException.class, () -> BencodeDecoder.decode(data));
            assertTrue(exception.getMessage().contains("Dictionary key is not a String"));
        }

        @Test
        @DisplayName("Should encode dictionary back to bytes using Mocks")
        void shouldEncodeDictionary(@Mock BencodeElement<Object> mockValue) throws IOException {
            when(mockValue.encode()).thenReturn("VAL".getBytes());

            Map<String, BencodeElement<?>> map = new LinkedHashMap<>();
            map.put("key", mockValue);

            BencodeDictionary bencodeDictionary = new BencodeDictionary(map);

            byte[] result = bencodeDictionary.encode();

            assertArrayEquals("d3:keyVALe".getBytes(), result);
            verify(mockValue).encode();
        }
    }

    @Nested
    @DisplayName("General Error Handling")
    class GeneralErrors {

        @Test
        @DisplayName("Should throw exception when data is garbage/invalid")
        void shouldThrowExceptionWhenDataIsGarbage() {
            byte[] data = "xyz".getBytes();
            assertThrows(IOException.class, () -> BencodeDecoder.decode(data));
        }
    }
}