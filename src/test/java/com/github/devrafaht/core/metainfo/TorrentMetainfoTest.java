package com.github.devrafaht.core.metainfo;

import core.bencode.BencodeDictionary;
import core.bencode.BencodeElement;
import core.bencode.BencodeNumber;
import core.metainfo.TorrentMetainfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("TorrentMetainfo Tests")
@ExtendWith(MockitoExtension.class)
class TorrentMetainfoTest {

    @Mock
    private BencodeDictionary mockRoot;

    @Test
    @DisplayName("Should create Metainfo from valid Bencode structure")
    void shouldCreateValidMetainfo() throws IOException {
        BencodeDictionary root = TorrentMetainfoFactory.createValidRootDictionary();

        TorrentMetainfo metainfo = TorrentMetainfo.createFrom(root);

        assertEquals(TorrentMetainfoFactory.DEFAULT_ANNOUNCE, metainfo.getAnnounce());
        assertEquals(TorrentMetainfoFactory.DEFAULT_NAME, metainfo.getName());
        assertEquals(TorrentMetainfoFactory.DEFAULT_LENGTH, metainfo.getLength());
        assertEquals(TorrentMetainfoFactory.DEFAULT_PIECE_LENGTH, metainfo.getPieceLength());
    }

    @Test
    @DisplayName("Should create Metainfo using Mock for Root")
    void shouldCreateValidMetainfoWithMock() throws IOException {
        BencodeDictionary realRoot = TorrentMetainfoFactory.createValidRootDictionary();
        when(mockRoot.getValue()).thenReturn(realRoot.getValue());

        TorrentMetainfo metainfo = TorrentMetainfo.createFrom(mockRoot);

        assertEquals(TorrentMetainfoFactory.DEFAULT_NAME, metainfo.getName());
        verify(mockRoot).getValue();
    }

    @Test
    @DisplayName("Should throw exception when 'announce' is missing")
    void shouldThrowExceptionWhenMissingAnnounce() {
        Map<String, BencodeElement<?>> infoMap = TorrentMetainfoFactory.createValidInfoMap();
        Map<String, BencodeElement<?>> rootDataWithoutAnnounce = new LinkedHashMap<>();
        rootDataWithoutAnnounce.put("info", new BencodeDictionary(infoMap));
        when(mockRoot.getValue()).thenReturn(rootDataWithoutAnnounce);

        Exception e = assertThrows(IOException.class, () -> TorrentMetainfo.createFrom(mockRoot));
        assertEquals("Missing 'announce'", e.getMessage());
    }

    @Test
    @DisplayName("Should calculate Info Hash correctly")
    void shouldCalculateInfoHash() throws IOException {
        BencodeDictionary root = TorrentMetainfoFactory.createValidRootDictionary();

        TorrentMetainfo metainfo = TorrentMetainfo.createFrom(root);

        assertNotNull(metainfo.getInfoHash());
        assertEquals(20, metainfo.getInfoHash().length);
        assertFalse(metainfo.getHexInfoHash().isEmpty());
    }

    @Test
    @DisplayName("Should split pieces blob into list of hashes")
    void shouldSplitPiecesCorrectly() throws IOException {
        Map<String, BencodeElement<?>> infoMap = TorrentMetainfoFactory.createValidInfoMap();
        infoMap.put("pieces", TorrentMetainfoFactory.createPiecesBlob(2));
        BencodeDictionary root = TorrentMetainfoFactory.createValidRootDictionary(new BencodeDictionary(infoMap));

        TorrentMetainfo metainfo = TorrentMetainfo.createFrom(root);

        assertEquals(2, metainfo.getPieceHashes().size());
        assertEquals((byte)'a', metainfo.getPieceHashes().get(0)[0]);
        assertEquals((byte)'b', metainfo.getPieceHashes().get(1)[0]);
    }

    @Test
    @DisplayName("Should throw exception when root element is not a dictionary")
    void shouldThrowExceptionWhenRootNotDictionary() {
        BencodeNumber invalidRoot = new BencodeNumber(123L);

        Exception e = assertThrows(IOException.class, () -> TorrentMetainfo.createFrom(invalidRoot));
        assertEquals("Invalid file.", e.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when missing metadata inside info dictionary")
    void shouldThrowExceptionWhenMissingMetadataInInfo() {
        Map<String, BencodeElement<?>> infoMap = new LinkedHashMap<>();
        infoMap.put("length", new BencodeNumber(1024L));
        BencodeDictionary root = TorrentMetainfoFactory.createValidRootDictionary(new BencodeDictionary(infoMap));

        Exception e = assertThrows(IOException.class, () -> TorrentMetainfo.createFrom(root));
        assertTrue(e.getMessage().contains("Missing required metadata fields"));
    }

    @Test
    @DisplayName("Should throw exception when pieces length is not multiple of 20")
    void shouldThrowExceptionWhenPiecesLengthInvalid() {
        Map<String, BencodeElement<?>> infoMap = TorrentMetainfoFactory.createValidInfoMap();
        infoMap.put("pieces", TorrentMetainfoFactory.createInvalidPiecesBlob());
        BencodeDictionary root = TorrentMetainfoFactory.createValidRootDictionary(new BencodeDictionary(infoMap));

        Exception e = assertThrows(IOException.class, () -> TorrentMetainfo.createFrom(root));
        assertEquals("Invalid 'pieces' field size.", e.getMessage());
    }
}