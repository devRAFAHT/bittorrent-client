package com.github.devrafaht.core.metainfo;

import core.bencode.BencodeByteArray;
import core.bencode.BencodeDictionary;
import core.bencode.BencodeElement;
import core.bencode.BencodeNumber;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class TorrentMetainfoFactory {

    public static final String DEFAULT_ANNOUNCE = "http://tracker.com";
    public static final String DEFAULT_NAME = "ubuntu.iso";
    public static final long DEFAULT_LENGTH = 1024L;
    public static final long DEFAULT_PIECE_LENGTH = 512L;

    public static Map<String, BencodeElement<?>> createValidInfoMap() {
        Map<String, BencodeElement<?>> map = new LinkedHashMap<>();

        map.put("name", new BencodeByteArray(DEFAULT_NAME.getBytes(StandardCharsets.UTF_8)));
        map.put("length", new BencodeNumber(DEFAULT_LENGTH));
        map.put("piece length", new BencodeNumber(DEFAULT_PIECE_LENGTH));
        map.put("pieces", createPiecesBlob(1));

        return map;
    }

    public static BencodeDictionary createValidRootDictionary() {
        Map<String, BencodeElement<?>> infoMap = createValidInfoMap();
        return createValidRootDictionary(new BencodeDictionary(infoMap));
    }

    public static BencodeDictionary createValidRootDictionary(BencodeDictionary infoDictionary) {
        Map<String, BencodeElement<?>> rootMap = new LinkedHashMap<>();
        rootMap.put("announce", new BencodeByteArray(DEFAULT_ANNOUNCE.getBytes(StandardCharsets.UTF_8)));
        rootMap.put("info", infoDictionary);
        return new BencodeDictionary(rootMap);
    }

    public static BencodeByteArray createPiecesBlob(int numPieces) {
        byte[] blob = new byte[20 * numPieces];
        for (int i = 0; i < blob.length; i++) {
            blob[i] = (byte) ('a' + (i / 20));
        }
        return new BencodeByteArray(blob);
    }

    public static BencodeByteArray createInvalidPiecesBlob() {
        return new BencodeByteArray(new byte[21]);
    }
}