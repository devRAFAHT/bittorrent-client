package core.bencode;

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

    public String asString() {
        return new String(value, StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return asString();
    }
}
