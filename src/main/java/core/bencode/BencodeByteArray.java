package core.bencode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BencodeByteArray implements BencodeElement<byte[]> {

    private final byte[] value;

    public BencodeByteArray(byte[] value) {
        this.value = value;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String lengthPrefix = value.length + ":";

        outputStream.write(lengthPrefix.getBytes(StandardCharsets.UTF_8));
        outputStream.write(value);

        return outputStream.toByteArray();
    }

    public String asString() {
        return new String(value, StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return asString();
    }
}
