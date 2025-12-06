package core.metainfo;

import core.bencode.BencodeByteArray;
import core.bencode.BencodeDictionary;
import core.bencode.BencodeElement;
import core.bencode.BencodeNumber;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TorrentMetainfo {

    private final String announce;
    private final String name;
    private final long length;
    private final long pieceLength;
    private final List<byte[]> pieceHashes;
    private final byte[] infoHash;

    private TorrentMetainfo(String announce, String name, long length, long pieceLength, List<byte[]> pieceHashes, byte[] infoHash) {
        this.announce = announce;
        this.name = name;
        this.length = length;
        this.pieceLength = pieceLength;
        this.pieceHashes = pieceHashes;
        this.infoHash = infoHash;
    }

    public static TorrentMetainfo createFrom(BencodeElement<?> rootElement) throws IOException {

        if(!(rootElement instanceof BencodeDictionary)){
            throw new IOException("Invalid file.");
        }

        Map<String, BencodeElement<?>> rootData = ((BencodeDictionary) rootElement).getValue();

        if(!(rootData.containsKey("announce"))){
            throw new IOException("Missing 'announce'");
        }

        String announce = ((BencodeByteArray) rootData.get("announce")).asString();

        BencodeElement<?> infoElement = rootData.get("info");

        byte[] infoByte = infoElement.encode();
        byte[] infoHash = calculateSha1(infoByte);

        Map<String, BencodeElement<?>> infoData = ((BencodeDictionary) infoElement).getValue();

        if (!infoData.containsKey("name") || !infoData.containsKey("length") || !infoData.containsKey("piece length") || !infoData.containsKey("pieces")) {
            throw new IOException("Missing required metadata fields in 'info' dictionary.");
        }

        String name = ((BencodeByteArray) infoData.get("name")).asString();
        long length = ((BencodeNumber) infoData.get("length")).getValue();
        long pieceLength = ((BencodeNumber) infoData.get("piece length")).getValue();

        byte[] piecesBlob = ((BencodeByteArray) infoData.get("pieces")).getValue();

        if (piecesBlob.length % 20 != 0) {
            throw new IOException("Invalid 'pieces' field size.");
        }

        List<byte[]> pieces = new ArrayList<>();

        for (int i = 0; i < piecesBlob.length; i += 20) {
            byte[] sha1Hash = new byte[20];
            System.arraycopy(piecesBlob, i, sha1Hash, 0, 20);

            pieces.add(sha1Hash);
        }

        return new TorrentMetainfo(announce, name, length, pieceLength, pieces, infoHash);
    }

    private static byte[] calculateSha1(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            return messageDigest.digest(data);
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }

    public String getAnnounce() {
        return announce;
    }

    public String getName() {
        return name;
    }

    public long getLength() {
        return length;
    }

    public long getPieceLength() {
        return pieceLength;
    }

    public List<byte[]> getPieceHashes() {
        return pieceHashes;
    }

    public byte[] getInfoHash() {
        return infoHash;
    }

    public String getHexInfoHash() {
        StringBuilder hex = new StringBuilder();
        for (byte b : infoHash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
