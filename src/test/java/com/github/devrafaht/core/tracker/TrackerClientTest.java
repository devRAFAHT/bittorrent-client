package com.github.devrafaht.core.tracker;

import com.github.devrafaht.core.metainfo.TorrentMetainfoFactory;
import core.metainfo.TorrentMetainfo;
import core.tracker.ByteUrlEncoder;
import core.tracker.TrackerClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrackerClient Tests")
class TrackerClientTest {

    @Mock
    private TorrentMetainfo metainfo;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<byte[]> httpResponse;

    @Test
    @DisplayName("Should build announce URL consistent with Factory defaults")
    void shouldBuildCorrectAnnounceUrl() {
        TrackerClient trackerClient = new TrackerClient();

        byte[] peerId = "-BC0001-123456789012".getBytes(StandardCharsets.UTF_8);
        int port = 6881;
        byte[] hash = new byte[]{ 0x12, (byte) 0xFF, 'A' };

        when(metainfo.getAnnounce()).thenReturn(TorrentMetainfoFactory.DEFAULT_ANNOUNCE);
        when(metainfo.getLength()).thenReturn(TorrentMetainfoFactory.DEFAULT_LENGTH);
        when(metainfo.getInfoHash()).thenReturn(hash);

        String resultUrl = trackerClient.buildAnnounceUrl(metainfo, peerId, port);

        String expectedHashEncoded = ByteUrlEncoder.encode(hash);
        String expectedPeerIdEncoded = ByteUrlEncoder.encode(peerId);

        String expectedUrl = TorrentMetainfoFactory.DEFAULT_ANNOUNCE +
                "?info_hash=" + expectedHashEncoded +
                "&peer_id=" + expectedPeerIdEncoded +
                "&port=" + port +
                "&uploaded=0" +
                "&downloaded=0" +
                "&left=" + TorrentMetainfoFactory.DEFAULT_LENGTH +
                "&compact=1";

        assertEquals(expectedUrl, resultUrl);
    }

    @Test
    @DisplayName("Should return response body when server returns HTTP 200")
    void shouldReturnBodyWhenStatusIs200() throws IOException, InterruptedException {
        TrackerClient trackerClient = new TrackerClient(httpClient);
        byte[] peerId = "-BC0001-123456789012".getBytes(StandardCharsets.UTF_8);
        byte[] expect = "d8:intervali900e5:peers6:......e".getBytes(StandardCharsets.UTF_8); // Dummy response

        when(metainfo.getAnnounce()).thenReturn(TorrentMetainfoFactory.DEFAULT_ANNOUNCE);
        when(metainfo.getInfoHash()).thenReturn(new byte[20]);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expect);

        byte[] result = trackerClient.request(metainfo, peerId, 6881);

        assertArrayEquals(expect, result);
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    @DisplayName("Should throw IOException when server returns non-200 status")
    void shouldThrowExceptionWhenStatusIsNot200() throws IOException, InterruptedException {
        TrackerClient trackerClient = new TrackerClient(httpClient);
        byte[] peerId = "-BC0001-123456789012".getBytes(StandardCharsets.UTF_8);

        when(metainfo.getAnnounce()).thenReturn(TorrentMetainfoFactory.DEFAULT_ANNOUNCE);
        when(metainfo.getInfoHash()).thenReturn(new byte[20]);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        when(httpResponse.statusCode()).thenReturn(404);

        IOException exception = assertThrows(IOException.class, () -> {
            trackerClient.request(metainfo, peerId, 6881);
        });

        assertEquals("Tracker failed. HTTP Status: 404", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IOException when HttpClient throws exception (Network Error)")
    void shouldThrowExceptionWhenNetworkFails() throws IOException, InterruptedException {
        TrackerClient trackerClient = new TrackerClient(httpClient);
        byte[] peerId = "-BC0001-123456789012".getBytes(StandardCharsets.UTF_8);

        when(metainfo.getAnnounce()).thenReturn(TorrentMetainfoFactory.DEFAULT_ANNOUNCE);
        when(metainfo.getInfoHash()).thenReturn(new byte[20]);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network Unreachable"));

        assertThrows(IOException.class, () -> {
            trackerClient.request(metainfo, peerId, 6881);
        });
    }
}