package core.tracker;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class PeerIdGenerator {

    private static final String PREFIX = "-BC0001-";

    public static byte[] generate(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] prefixBytes = PREFIX.getBytes(StandardCharsets.US_ASCII);
        byte[] sufixBytes = new byte[12];
        secureRandom.nextBytes(sufixBytes);

        byte[] peerId = new byte[20];

        System.arraycopy(prefixBytes, 0, peerId, 0, prefixBytes.length);
        System.arraycopy(sufixBytes, 0, peerId, prefixBytes.length, sufixBytes.length);

        return peerId;
    }

}
