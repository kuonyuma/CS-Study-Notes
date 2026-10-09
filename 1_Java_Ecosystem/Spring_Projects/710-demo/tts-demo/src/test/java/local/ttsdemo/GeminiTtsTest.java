package local.ttsdemo;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class GeminiTtsTest {
    @Test void postsVerbatimTranscriptWithSeparateStyleAndDecodesRestAudio() throws Exception {
        var captured = new AtomicReference<String>();
        var key = new AtomicReference<String>();
        var upstream = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        upstream.createContext("/interactions", exchange -> {
            captured.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            key.set(exchange.getRequestHeaders().getFirst("x-goog-api-key"));
            var response = "{\"status\":\"completed\",\"steps\":[{\"type\":\"model_output\",\"content\":[{\"type\":\"audio\",\"mime_type\":\"audio/wav\",\"data\":\"UklGRg==\"}]}]}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        upstream.start();
        try {
            var engine = new GeminiTts(HttpClient.newHttpClient(), URI.create("http://127.0.0.1:" + upstream.getAddress().getPort() + "/interactions"), "test-key");
            var audio = engine.synthesize(new TtsRequest("gemini", "你好", "Kore", "轻声朗读", 0));
            assertArrayEquals(new byte[]{82,73,70,70}, audio.bytes());
            assertEquals("audio/wav", audio.contentType());
            var body = Json.MAPPER.readTree(captured.get());
            assertEquals("gemini-3.8-flash-tts", body.path("model").asText());
            assertEquals("你好", body.at("/input/0/content/0/text").asText());
            assertEquals("轻声朗读", body.at("/input/0/content/0/annotations/0/style").asText());
            assertEquals("Kore", body.at("/generation_config/speech_config/0/voice").asText());
            assertFalse(body.path("store").asBoolean(true));
            assertEquals("test-key", key.get());
        } finally { upstream.stop(0); }
    }
    @Test void quotaErrorsRemainErrorsRatherThanFakeAudio() throws Exception {
        var upstream = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        upstream.createContext("/", exchange -> {
            exchange.sendResponseHeaders(429, -1);
            exchange.close();
        });
        upstream.start();
        try {
            var engine = new GeminiTts(HttpClient.newHttpClient(), URI.create("http://127.0.0.1:" + upstream.getAddress().getPort()), "test-key");
            var error = assertThrows(ApiException.class, () -> engine.synthesize(new TtsRequest("gemini", "Hello", "Kore", "", 0)));
            assertEquals(429, error.status());
        } finally { upstream.stop(0); }
    }
}
