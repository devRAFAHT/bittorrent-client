package com.github.devrafaht.core.tracker;

import core.tracker.PeerIdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("PeerIdGenerator Tests")
class PeerIdGeneratorTest {

    @Test
    @DisplayName("Should generate PeerId with correct structure (20 bytes and specific prefix)")
    void shouldGeneratePeerIdWithCorrectStructure() {
        byte[] peerId = PeerIdGenerator.generate();

        assertEquals(20, peerId.length);

        String prefix = "-BC0001-";
        byte[] prefixBytes = prefix.getBytes(StandardCharsets.US_ASCII);

        for (int i = 0; i < prefixBytes.length; i++) {
            assertEquals(prefixBytes[i], peerId[i], "Prefix mismatch at index " + i);
        }
    }

    @Test
    @DisplayName("Should generate unique PeerIds on consecutive calls")
    void shouldGenerateUniquePeerIds() {
        byte[] id1 = PeerIdGenerator.generate();
        byte[] id2 = PeerIdGenerator.generate();

        assertNotEquals(id1, id2);
    }
}
