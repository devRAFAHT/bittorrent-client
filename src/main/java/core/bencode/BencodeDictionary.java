package core.bencode;

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
    public String toString() {
        return value.toString();
    }
}
