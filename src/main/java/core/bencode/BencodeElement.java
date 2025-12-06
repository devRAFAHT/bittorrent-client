package core.bencode;

import java.io.IOException;

public interface BencodeElement<T> {

    T getValue();
    byte[] encode() throws IOException;

}
