package core.bencode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BencodeDictionary implements BencodeElement<Map<String, BencodeElement<?>>> {

    private final Map<String, BencodeElement<?>> value;

    public BencodeDictionary(Map<String, BencodeElement<?>> value) {
        this.value = value;
    }

    @Override
    public Map<String, BencodeElement<?>> getValue() {
        return value;
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write('d');

        for (Map.Entry<String, BencodeElement<?>> entry : value.entrySet()) {
            String key = entry.getKey();
            String bencodedKey = key.length() + ":" + key;

            outputStream.write(bencodedKey.getBytes(StandardCharsets.UTF_8));
            outputStream.write(entry.getValue().encode());
        }

        outputStream.write('e');

        return outputStream.toByteArray();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
