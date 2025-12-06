package core.tracker;

import java.util.HexFormat;

public class ByteUrlEncoder {

    public static String encode(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for(byte b : bytes){
            char c = (char) b;

            boolean isSafe = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '_' || c == '~';

            if(isSafe){
                stringBuilder.append(c);
            } else{
                stringBuilder.append('%');
                String hex = HexFormat.of().toHexDigits(b);
                stringBuilder.append(hex);
            }
        }

        return stringBuilder.toString();
    }

}
