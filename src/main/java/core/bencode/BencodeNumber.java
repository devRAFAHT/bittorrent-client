package core.bencode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BencodeNumber implements BencodeElement<Long>{

    private final Long value;

    public BencodeNumber(Long value) {
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public byte[] encode() throws IOException {
        String encodedValue = "i" + value +"e";
        return encodedValue.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
