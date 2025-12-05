package core.bencode;

import java.util.List;

public class BencodeList implements BencodeElement<List<BencodeElement<?>>> {

    private final List<BencodeElement<?>> value;

    public BencodeList(List<BencodeElement<?>> value) {
        this.value = value;
    }

    @Override
    public List<BencodeElement<?>> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
