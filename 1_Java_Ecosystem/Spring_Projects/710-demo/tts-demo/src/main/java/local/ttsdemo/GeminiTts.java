package local.ttsdemo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

final class GeminiTts implements TtsEngine {
    private final HttpClient client;
    private final URI endpoint;
    private final String key;
    GeminiTts(HttpClient client, URI endpoint, String key) {
        this.client = client;
        this.endpoint = endpoint;
        this.key = key;
    }
    @Override public Audio synthesize(TtsRequest request) throws Exception {
        if (key == null || key.isBlank())
            throw new ApiException(503, "MISSING_API_KEY", "未找到 GEMINI_API_KEY 或 GOOGLE_GENERATIVE_AI_API_KEY 系统变量，请设置后重启服务。");
        var content = new java.util.LinkedHashMap<String, Object>();
        content.put("type", "text");
        content.put("text", request.text());
        if (!request.style().isEmpty())
            content.put("annotations", List.of(Map.of("type", "speech_metadata", "style", request.style())));
        var payload = Map.of(
                "model", Catalog.GEMINI_MODEL,
                "input", List.of(Map.of("type", "user_input", "content", List.of(content))),
                "response_format", Map.of("type", "audio", "mime_type", "audio/wav"),
                "generation_config", Map.of("speech_config", List.of(Map.of("voice", request.voice()))),
                "store", false);
        var httpRequest = HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(120))
                .header("x-goog-api-key", key).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(Json.MAPPER.writeValueAsBytes(payload))).build();
        var response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            int status = response.statusCode();
            if (status == 429) throw new ApiException(429, "UPSTREAM_QUOTA", "Gemini 请求频率或额度已达上限，请稍后重试或切换 Edge 免费语音。");
            if (status == 401 || status == 403) throw new ApiException(502, "UPSTREAM_AUTH", "Gemini 密钥或模型访问权限被拒绝，请检查该密钥对应的项目权限。");
            throw new ApiException(502, "UPSTREAM_ERROR", "Gemini 合成失败（HTTP " + status + "）。请稍后重试。");
        }
        if (response.body().length > 64 * 1024 * 1024)
            throw new ApiException(502, "INVALID_AUDIO", "Gemini 音频响应过大，请缩短文本。");
        var root = Json.MAPPER.readTree(response.body());
        com.fasterxml.jackson.databind.JsonNode audio = null;
        for (var step : root.path("steps")) {
            if (step.path("type").asText().equals("model_output")) {
                for (var part : step.path("content")) {
                    if (part.path("type").asText().equals("audio") && part.hasNonNull("data")) audio = part;
                }
            }
        }
        if (audio == null || audio.path("data").asText().isEmpty())
            throw new ApiException(502, "NO_AUDIO", "Gemini 未返回音频，文本可能被内容过滤，请修改文本后重试。");
        try {
            byte[] bytes = Base64.getDecoder().decode(audio.path("data").asText());
            if (bytes.length < 4 || bytes[0] != 'R' || bytes[1] != 'I' || bytes[2] != 'F' || bytes[3] != 'F')
                throw new ApiException(502, "INVALID_AUDIO", "Gemini 返回了无效的 WAV 音频。");
            return new Audio(bytes, "audio/wav", "wav");
        } catch (IllegalArgumentException e) {
            throw new ApiException(502, "INVALID_AUDIO", "Gemini 音频编码无效。");
        }
    }
}
