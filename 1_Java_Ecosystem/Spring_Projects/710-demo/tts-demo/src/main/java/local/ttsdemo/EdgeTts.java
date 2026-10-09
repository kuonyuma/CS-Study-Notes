package local.ttsdemo;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/** Java implementation of Edge's read-aloud wire protocol. No Python or Node backend. */
final class EdgeTts implements TtsEngine {
    // This is Edge's public client identifier, not a user's API credential.
    private static final String PUBLIC_CLIENT_TOKEN = "6A5AA1D4EAFF4E9FB37E23D68491D6F4";
    private static final String VERSION = "143.0.3650.75";
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter
            .ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT+0000 (Coordinated Universal Time)'", Locale.US).withZone(ZoneOffset.UTC);
    private final HttpClient client;
    EdgeTts(HttpClient client) { this.client = client; }

    @Override public Audio synthesize(TtsRequest request) throws Exception {
        var output = new ByteArrayOutputStream();
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(120);
        for (String text : EdgeProtocol.escapedChunks(request.text())) {
            var listener = new AudioListener();
            var uri = URI.create("wss://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1"
                    + "?TrustedClientToken=" + PUBLIC_CLIENT_TOKEN + "&ConnectionId=" + id()
                    + "&Sec-MS-GEC=" + gec() + "&Sec-MS-GEC-Version=1-" + VERSION);
            WebSocket socket = client.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(15))
                    .header("Origin", "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Pragma", "no-cache").header("Cache-Control", "no-cache")
                    .header("Cookie", "muid=" + id().toUpperCase(Locale.ROOT) + ";")
                    .buildAsync(uri, listener).get(15, TimeUnit.SECONDS);
            try {
                String timestamp = TIMESTAMP.format(Instant.now());
                socket.sendText("X-Timestamp:" + timestamp + "\r\nContent-Type:application/json; charset=utf-8\r\nPath:speech.config\r\n\r\n"
                        + "{\"context\":{\"synthesis\":{\"audio\":{\"metadataoptions\":{\"sentenceBoundaryEnabled\":\"false\",\"wordBoundaryEnabled\":\"false\"},"
                        + "\"outputFormat\":\"audio-24khz-48kbitrate-mono-mp3\"}}}}\r\n", true).get(10, TimeUnit.SECONDS);
                String rate = (request.rate() >= 0 ? "+" : "") + request.rate() + "%";
                String ssml = "<speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='en-US'><voice name='"
                        + request.voice() + "'><prosody pitch='+0Hz' rate='" + rate + "' volume='+0%'>"
                        + text + "</prosody></voice></speak>";
                socket.sendText("X-RequestId:" + id() + "\r\nContent-Type:application/ssml+xml\r\nX-Timestamp:"
                        + timestamp + "Z\r\nPath:ssml\r\n\r\n" + ssml, true).get(10, TimeUnit.SECONDS);
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) throw new java.util.concurrent.TimeoutException();
                output.write(listener.result.get(remaining, TimeUnit.NANOSECONDS));
            } finally { socket.abort(); }
        }
        if (output.size() == 0) throw new ApiException(502, "NO_AUDIO", "Edge 未返回音频，请重试。");
        return new Audio(output.toByteArray(), "audio/mpeg", "mp3");
    }

    private static String id() { return UUID.randomUUID().toString().replace("-", ""); }
    private static String gec() throws Exception {
        long seconds = Instant.now().getEpochSecond() + 11_644_473_600L;
        long ticks = (seconds - seconds % 300) * 10_000_000L;
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest((ticks + PUBLIC_CLIENT_TOKEN).getBytes(StandardCharsets.US_ASCII));
        return HexFormat.of().withUpperCase().formatHex(digest);
    }

    private static final class AudioListener implements WebSocket.Listener {
        final CompletableFuture<byte[]> result = new CompletableFuture<>();
        private final ByteArrayOutputStream output = new ByteArrayOutputStream();
        private final ByteArrayOutputStream binary = new ByteArrayOutputStream();
        private final StringBuilder text = new StringBuilder();
        @Override public CompletionStage<?> onBinary(WebSocket socket, ByteBuffer data, boolean last) {
            try {
                byte[] bytes = new byte[data.remaining()];
                data.get(bytes);
                binary.write(bytes);
                if (binary.size() > 1024 * 1024) throw new ApiException(502, "INVALID_AUDIO", "Edge 音频帧过大。");
                if (last) {
                    output.write(EdgeProtocol.audioPayload(binary.toByteArray()));
                    binary.reset();
                    if (output.size() > 32 * 1024 * 1024) throw new ApiException(502, "INVALID_AUDIO", "Edge 音频过大。");
                }
            } catch (Exception e) { result.completeExceptionally(e); }
            socket.request(1);
            return null;
        }
        @Override public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
            text.append(data);
            if (text.length() > 1024 * 1024) {
                result.completeExceptionally(new ApiException(502, "UPSTREAM_ERROR", "Edge 元数据响应过大。"));
            } else if (last) {
                if (text.toString().contains("Path:turn.end")) result.complete(output.toByteArray());
                text.setLength(0);
            }
            socket.request(1);
            return null;
        }
        @Override public CompletionStage<?> onClose(WebSocket socket, int status, String reason) {
            result.completeExceptionally(new ApiException(502, "UPSTREAM_CLOSED", "Edge 在完成合成前断开连接，请重试。"));
            return null;
        }
        @Override public void onError(WebSocket socket, Throwable error) { result.completeExceptionally(error); }
    }
}
