package local.ttsdemo;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;

final class EdgeProtocol {
    static List<String> escapedChunks(String text) {
        var chunks = new ArrayList<String>();
        var chunk = new StringBuilder();
        int bytes = 0;
        for (int cp : text.codePoints().toArray()) {
            String escaped = switch (cp) {
                case '&' -> "&amp;";
                case '<' -> "&lt;";
                case '>' -> "&gt;";
                case '\"' -> "&quot;";
                case '\'' -> "&apos;";
                default -> new String(Character.toChars(cp));
            };
            int size = escaped.getBytes(StandardCharsets.UTF_8).length;
            if (bytes + size > 3500) {
                chunks.add(chunk.toString());
                chunk.setLength(0);
                bytes = 0;
            }
            chunk.append(escaped);
            bytes += size;
        }
        if (!chunk.isEmpty()) chunks.add(chunk.toString());
        return chunks;
    }
    static byte[] audioPayload(byte[] frame) {
        if (frame.length < 2) throw invalidFrame();
        int length = ((frame[0] & 255) << 8) | (frame[1] & 255);
        if (length + 2 > frame.length) throw invalidFrame();
        var header = new String(frame, 2, length, StandardCharsets.UTF_8);
        if (!header.contains("Path:audio") || !header.contains("Content-Type:audio/mpeg")) return new byte[0];
        return Arrays.copyOfRange(frame, length + 2, frame.length);
    }
    private static ApiException invalidFrame() {
        return new ApiException(502, "INVALID_AUDIO", "Edge 返回了损坏的音频帧。");
    }
}
