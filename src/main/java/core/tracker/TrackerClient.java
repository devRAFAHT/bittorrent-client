package core.tracker;

import core.metainfo.TorrentMetainfo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TrackerClient {

    private final HttpClient client;

    public TrackerClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public TrackerClient(HttpClient client) {
        this.client = client;
    }

    public String buildAnnounceUrl(TorrentMetainfo metainfo, byte[] peerId, int port) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(metainfo.getAnnounce());
        stringBuilder.append("?info_hash=").append(ByteUrlEncoder.encode(metainfo.getInfoHash()));
        stringBuilder.append("&peer_id=").append(ByteUrlEncoder.encode(peerId));
        stringBuilder.append("&port=").append(port);
        stringBuilder.append("&uploaded=0");
        stringBuilder.append("&downloaded=0");
        stringBuilder.append("&left=").append(metainfo.getLength());
        stringBuilder.append("&compact=1");

        return stringBuilder.toString();
    }

    public byte[] request(TorrentMetainfo metainfo, byte[] peerId, int port) throws IOException, InterruptedException {
        String uri = buildAnnounceUrl(metainfo, peerId, port);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if(response.statusCode() != 200){
            throw new IOException("Tracker failed. HTTP Status: " + response.statusCode());
        }

        return response.body();
    }

}
