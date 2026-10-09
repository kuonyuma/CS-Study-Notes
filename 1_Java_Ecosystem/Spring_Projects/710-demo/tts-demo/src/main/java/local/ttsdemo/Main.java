package local.ttsdemo;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public final class Main {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8787;
        if (port < 1 || port > 65535) throw new IllegalArgumentException("Port must be between 1 and 65535");
        String key = System.getenv("GEMINI_API_KEY");
        if (key == null || key.isBlank()) key = System.getenv("GOOGLE_GENERATIVE_AI_API_KEY");
        boolean configured = key != null && !key.isBlank();
        var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
        var server = new TtsServer(port, Map.of(
                "gemini", new GeminiTts(client, URI.create("https://generativelanguage.googleapis.com/v1beta/interactions"), key),
                "edge", new EdgeTts(client)), configured);
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
        try { server.start(); }
        catch (Exception e) { server.close(); throw e; }
        System.out.println("TTS Demo: http://localhost:" + server.port());
        System.out.println("Gemini 3.8: " + (configured ? "API key configured" : "API key missing") + "; Edge: no API key required");
        System.out.println("Press Ctrl+C to stop.");
        new CountDownLatch(1).await();
    }
    private Main() {}
}
