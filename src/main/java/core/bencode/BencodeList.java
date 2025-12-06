package core.bencode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    public byte[] encode() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write('l');

        for(BencodeElement<?> element : value){
            outputStream.write(element.encode());
        }

        outputStream.write('e');

        return outputStream.toByteArray();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
