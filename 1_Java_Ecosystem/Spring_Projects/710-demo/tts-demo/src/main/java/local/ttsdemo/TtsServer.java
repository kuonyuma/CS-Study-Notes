package local.ttsdemo;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

final class TtsServer implements AutoCloseable {
    private final HttpServer server;
    private final ExecutorService executor = Executors.newFixedThreadPool(8);
    private final Semaphore synthesisSlots = new Semaphore(2);
    private final Map<String, TtsEngine> engines;
    private final boolean geminiConfigured;
    TtsServer(int port, Map<String, TtsEngine> engines, boolean geminiConfigured) throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        this.engines = Map.copyOf(engines);
        this.geminiConfigured = geminiConfigured;
        server.setExecutor(executor);
        server.createContext("/", this::handle);
    }
    void start() { server.start(); }
    int port() { return server.getAddress().getPort(); }
    static boolean isAllowedAuthority(String authority, int port) {
        if (authority == null) return false;
        String normalized = authority.toLowerCase(java.util.Locale.ROOT);
        return Set.of("localhost:" + port, "127.0.0.1:" + port).contains(normalized)
                || (port == 80 && Set.of("localhost", "127.0.0.1").contains(normalized));
    }
    private void handle(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
            String host = exchange.getRequestHeaders().getFirst("Host");
            if (!isAllowedAuthority(host, port()))
                throw new ApiException(403, "FORBIDDEN_HOST", "请通过 localhost 或 127.0.0.1 访问服务。");
            var path = exchange.getRequestURI().getPath();
            if (path.equals("/api/tts")) {
                method(exchange, "POST");
                synthesize(exchange);
            } else {
                method(exchange, "GET");
                switch (path) {
                    case "/api/health" -> json(exchange, 200, Map.of("status", "ok"));
                    case "/api/config" -> json(exchange, 200, Catalog.config(geminiConfigured));
                    case "/", "/index.html" -> resource(exchange, "index.html", "text/html; charset=utf-8");
                    case "/app.js" -> resource(exchange, "app.js", "text/javascript; charset=utf-8");
                    case "/style.css" -> resource(exchange, "style.css", "text/css; charset=utf-8");
                    case "/favicon.ico" -> exchange.sendResponseHeaders(204, -1);
                    default -> throw new ApiException(404, "NOT_FOUND", "接口或文件不存在。");
                }
            }
        } catch (Exception e) {
            Throwable cause = e;
            while ((cause instanceof java.util.concurrent.ExecutionException || cause instanceof java.util.concurrent.CompletionException)
                    && cause.getCause() != null) cause = cause.getCause();
            ApiException error;
            if (cause instanceof ApiException api) error = api;
            else if (cause instanceof java.util.concurrent.TimeoutException || cause instanceof java.net.http.HttpTimeoutException)
                error = new ApiException(504, "UPSTREAM_TIMEOUT", "语音合成超时，请缩短文本或稍后重试。");
            else {
                // Only the exception type is logged; requests, keys and upstream response bodies are never logged.
                System.err.println("TTS request failed: " + cause.getClass().getSimpleName());
                error = new ApiException(502, "UPSTREAM_ERROR", "语音服务连接失败，请检查网络后重试。");
            }
            if (cause instanceof InterruptedException) Thread.currentThread().interrupt();
            if (exchange.getResponseCode() == -1)
                json(exchange, error.status(), Map.of("error", Map.of("code", error.code(), "message", error.getMessage())));
        } finally { exchange.close(); }
    }
    private void synthesize(HttpExchange exchange) throws Exception {
        String origin = exchange.getRequestHeaders().getFirst("Origin");
        if (origin != null && (!origin.startsWith("http://") || !isAllowedAuthority(origin.substring(7), port())))
            throw new ApiException(403, "FORBIDDEN_ORIGIN", "此接口只允许本地页面调用。");
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.split(";", 2)[0].trim().equalsIgnoreCase("application/json"))
            throw new ApiException(415, "UNSUPPORTED_MEDIA_TYPE", "请提交 application/json 请求。");
        byte[] body = exchange.getRequestBody().readNBytes(20_001);
        if (body.length > 20_000) throw new ApiException(413, "REQUEST_TOO_LARGE", "请求体过大，请缩短文本。");
        var request = TtsRequest.parse(new String(body, StandardCharsets.UTF_8));
        var engine = engines.get(request.provider());
        if (engine == null) throw new ApiException(503, "PROVIDER_UNAVAILABLE", "语音服务尚未配置。");
        if (!synthesisSlots.tryAcquire()) throw new ApiException(429, "BUSY", "当前有两条合成任务正在处理，请稍后重试。");
        try {
            var audio = engine.synthesize(request);
            exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"tts-" + request.provider() + "." + audio.extension() + "\"");
            exchange.getResponseHeaders().set("X-TTS-Provider", request.provider());
            exchange.getResponseHeaders().set("X-TTS-Model", Catalog.provider(request.provider()).model());
            bytes(exchange, 200, audio.contentType(), audio.bytes());
        } finally { synthesisSlots.release(); }
    }
    private static void method(HttpExchange exchange, String expected) {
        if (!exchange.getRequestMethod().equals(expected)) {
            exchange.getResponseHeaders().set("Allow", expected);
            throw new ApiException(405, "METHOD_NOT_ALLOWED", "此接口需要 " + expected + " 请求。");
        }
    }
    private static void resource(HttpExchange exchange, String name, String contentType) throws IOException {
        try (var input = TtsServer.class.getResourceAsStream("/static/" + name)) {
            if (input == null) throw new ApiException(404, "NOT_FOUND", "页面文件不存在，请重新构建服务。");
            bytes(exchange, 200, contentType, input.readAllBytes());
        }
    }
    private static void json(HttpExchange exchange, int status, Object value) throws IOException {
        bytes(exchange, status, "application/json; charset=utf-8", Json.MAPPER.writeValueAsBytes(value));
    }
    private static void bytes(HttpExchange exchange, int status, String contentType, byte[] bytes) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }
    @Override public void close() {
        server.stop(0);
        executor.shutdownNow();
    }
}
