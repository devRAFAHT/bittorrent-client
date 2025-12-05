package core.bencode;

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
    public String toString() {
        return String.valueOf(value);
    }
}
