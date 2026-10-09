package local.ttsdemo;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class TtsServerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String BODY = "{\"provider\":\"edge\",\"text\":\"hello\"}";
    private HttpRequest.Builder request(TtsServer server, String path) {
        return HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.port() + path));
    }
    private HttpRequest post(TtsServer server, String body) {
        return request(server, "/api/tts").header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
    }
    @Test void returnsAudioBytesWithDownloadHeadersAndDefaultVoice() throws Exception {
        var captured = new AtomicReference<TtsRequest>();
        TtsEngine engine = req -> { captured.set(req); return new Audio(new byte[]{1,2,3}, "audio/mpeg", "mp3"); };
        try (var server = new TtsServer(0, Map.of("edge", engine), false)) {
            server.start();
            var response = client.send(post(server, BODY), HttpResponse.BodyHandlers.ofByteArray());
            assertEquals(200, response.statusCode());
            assertArrayEquals(new byte[]{1,2,3}, response.body());
            assertEquals("audio/mpeg", response.headers().firstValue("Content-Type").orElseThrow());
            assertTrue(response.headers().firstValue("Content-Disposition").orElseThrow().contains("tts-edge.mp3"));
            assertEquals("no-store", response.headers().firstValue("Cache-Control").orElseThrow());
            assertEquals("zh-CN-XiaoxiaoNeural", captured.get().voice());
        }
    }
    @Test void reportsConfigurationWithoutLeakingCredentials() throws Exception {
        try (var server = new TtsServer(0, Map.of(), false)) {
            server.start();
            var health = client.send(request(server, "/api/health").GET().build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(200, health.statusCode());
            assertEquals("ok", Json.MAPPER.readTree(health.body()).path("status").asText());
            var config = client.send(request(server, "/api/config").GET().build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(200, config.statusCode());
            var json = Json.MAPPER.readTree(config.body());
            assertFalse(json.at("/providers/0/configured").asBoolean(true));
            assertTrue(json.at("/providers/1/configured").asBoolean());
            assertEquals("gemini-3.8-flash-tts", json.at("/providers/0/model").asText());
        }
    }
    @Test void rejectsInvalidJsonOversizedBodyAndCrossSiteCallsBeforeSynthesis() throws Exception {
        try (var server = new TtsServer(0, Map.of(), false)) {
            server.start();
            assertEquals(400, client.send(post(server, "not-json"), HttpResponse.BodyHandlers.ofString()).statusCode());
            assertEquals(413, client.send(post(server, "a".repeat(20001)), HttpResponse.BodyHandlers.ofString()).statusCode());
            var crossSite = request(server, "/api/tts").header("Origin", "https://untrusted.example").header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(BODY)).build();
            assertEquals(403, client.send(crossSite, HttpResponse.BodyHandlers.ofString()).statusCode());
            assertEquals(405, client.send(request(server, "/api/tts").GET().build(), HttpResponse.BodyHandlers.ofString()).statusCode());
        }
    }
    @Test void doesNotExposeInternalErrorsToBrowser() throws Exception {
        TtsEngine engine = req -> { throw new IllegalStateException("private-secret"); };
        try (var server = new TtsServer(0, Map.of("edge", engine), false)) {
            server.start();
            var response = client.send(post(server, BODY), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            assertEquals(502, response.statusCode());
            assertFalse(response.body().contains("private-secret"));
            assertFalse(Json.MAPPER.readTree(response.body()).at("/error/message").asText().isBlank());
        }
    }
    @Test void limitsConcurrentSynthesisAndReleasesSlotsAfterCompletion() throws Exception {
        var entered = new CountDownLatch(2);
        var release = new CountDownLatch(1);
        TtsEngine engine = req -> {
            entered.countDown();
            assertTrue(release.await(5, TimeUnit.SECONDS));
            return new Audio(new byte[]{1}, "audio/mpeg", "mp3");
        };
        try (var server = new TtsServer(0, Map.of("edge", engine), false)) {
            server.start();
            var first = client.sendAsync(post(server, BODY), HttpResponse.BodyHandlers.ofByteArray());
            var second = client.sendAsync(post(server, BODY), HttpResponse.BodyHandlers.ofByteArray());
            try {
                assertTrue(entered.await(3, TimeUnit.SECONDS));
                assertEquals(429, client.send(post(server, BODY), HttpResponse.BodyHandlers.ofByteArray()).statusCode());
            } finally { release.countDown(); }
            assertEquals(200, first.get(5, TimeUnit.SECONDS).statusCode());
            assertEquals(200, second.get(5, TimeUnit.SECONDS).statusCode());
            assertEquals(200, client.send(post(server, BODY), HttpResponse.BodyHandlers.ofByteArray()).statusCode());
        }
    }
}
