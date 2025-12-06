package core.bencode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BencodeDecoder {

    private final PushbackInputStream stream;

    public static BencodeElement<?> decode(byte[] data) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        BencodeDecoder decoder = new BencodeDecoder(byteStream);
        return decoder.decodeInternal();
    }

    private BencodeDecoder(InputStream inputStream) {
        this.stream = new PushbackInputStream(inputStream, 1);
    }

    private BencodeElement<?> decodeInternal() throws IOException {
        int readByte = stream.read();

        if (readByte == -1) {
            return null;
        }

        stream.unread(readByte);

        char currentChar = (char) readByte;

        if (currentChar == 'i') {
            return decodeNumber();
        } else if (currentChar == 'l') {
            return decodeList();
        } else if (currentChar == 'd') {
            return decodeDictionary();
        } else if (Character.isDigit(currentChar)) {
            return decodeByteArray();
        } else {
            throw new IOException("Invalid Bencode format: unexpected character '" + currentChar + "'");
        }
    }

    private BencodeNumber decodeNumber() throws IOException {
        int readByte = stream.read();

        if ((char) readByte != 'i') {
            throw new IOException("Internal error: expected 'i'");
        }

        StringBuilder buffer = new StringBuilder();
        boolean foundEnd = false;

        while ((readByte = stream.read()) != -1) {
            char currentChar = (char) readByte;

            if (currentChar == 'e') {
                foundEnd = true;
                break;
            }

            buffer.append(currentChar);
        }

        if (!foundEnd) {
            throw new IOException("Invalid Bencode number: missing 'e' at the end");
        }

        try {
            return new BencodeNumber(Long.parseLong(buffer.toString()));
        } catch (NumberFormatException e) {
            throw new IOException("Invalid Bencode number: " + buffer.toString(), e);
        }
    }

    private BencodeByteArray decodeByteArray() throws IOException {
        StringBuilder lengthBuffer = new StringBuilder();
        int readByte;
        boolean foundColon = false;

        while ((readByte = stream.read()) != -1) {
            char currentChar = (char) readByte;

            if (currentChar == ':') {
                foundColon = true;
                break;
            }

            if (Character.isDigit(currentChar)) {
                lengthBuffer.append(currentChar);
            } else {
                throw new IOException("String length contains non-numeric character: " + currentChar);
            }
        }

        if (!foundColon) {
            throw new IOException("Invalid Bencode string: missing ':' separator");
        }

        if (lengthBuffer.isEmpty()) {
            throw new IOException("String has no defined length");
        }

        int length = Integer.parseInt(lengthBuffer.toString());
        byte[] data = new byte[length];

        int readBytesCount = stream.readNBytes(data, 0, length);

        if (readBytesCount != length) {
            throw new IOException("Unexpected EOF while reading string of length " + length);
        }

        return new BencodeByteArray(data);
    }

    private BencodeList decodeList() throws IOException {
        int readByte = stream.read();

        if ((char) readByte != 'l') {
            throw new IOException("Internal error: expected 'l'");
        }

        List<BencodeElement<?>> list = new ArrayList<>();

        while (true) {
            int peekByte = stream.read();

            if (peekByte == -1) {
                throw new IOException("Unclosed list (EOF)");
            }

            stream.unread(peekByte);

            char currentChar = (char) peekByte;

            if (currentChar == 'e') {
                stream.read();
                break;
            }

            BencodeElement<?> element = decodeInternal();
            list.add(element);
        }

        return new BencodeList(list);
    }

    private BencodeDictionary decodeDictionary() throws IOException {
        int readByte = stream.read();

        if ((char) readByte != 'd') {
            throw new IOException("Internal error: expected 'd'");
        }

        Map<String, BencodeElement<?>> map = new LinkedHashMap<>();

        while (true) {
            int peekByte = stream.read();

            if (peekByte == -1) {
                throw new IOException("Unclosed dictionary (EOF)");
            }

            stream.unread(peekByte);

            char currentChar = (char) peekByte;

            if (currentChar == 'e') {
                stream.read();
                break;
            }

            BencodeElement<?> keyElement = decodeInternal();

            if (!(keyElement instanceof BencodeByteArray)) {
                throw new IOException("Dictionary key is not a String! Found: " + keyElement.getClass().getSimpleName());
            }

            String key = ((BencodeByteArray) keyElement).asString();

            BencodeElement<?> valueElement = decodeInternal();

            map.put(key, valueElement);
        }

        return new BencodeDictionary(map);
    }
}