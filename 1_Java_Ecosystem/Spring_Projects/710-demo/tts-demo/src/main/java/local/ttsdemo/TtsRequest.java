package local.ttsdemo;

record TtsRequest(String provider, String text, String voice, String style, int rate) {
    static TtsRequest parse(String json) {
        try {
            var node = Json.MAPPER.readTree(json);
            if (node == null || !node.isObject()) throw invalid("请求必须是 JSON 对象。");
            var provider = Catalog.provider(string(node, "provider", ""));
            var text = string(node, "text", "").strip();
            if (text.isEmpty() || text.length() > Catalog.MAX_TEXT_LENGTH)
                throw invalid("请输入 1–3000 个字符的文本。");
            // Reject XML-incompatible control characters before building Edge SSML.
            if (text.codePoints().anyMatch(cp -> (cp < 32 && cp != 9 && cp != 10 && cp != 13)
                    || cp == 0xFFFE || cp == 0xFFFF || (cp >= 0xD800 && cp <= 0xDFFF)))
                throw invalid("文本包含不支持的控制字符。");
            var voice = string(node, "voice", provider.defaultVoice());
            if (provider.voices().stream().noneMatch(v -> v.id().equals(voice)))
                throw invalid("所选音色不属于当前语音服务。");
            var style = string(node, "style", "").strip();
            if (style.length() > 500) throw invalid("朗读风格最多 500 个字符。");
            var rateNode = node.get("rate");
            int rate = 0;
            if (rateNode != null && !rateNode.isNull()) {
                if (!rateNode.isIntegralNumber() || !rateNode.canConvertToInt())
                    throw invalid("语速必须是整数。");
                rate = rateNode.intValue();
                if (rate < -50 || rate > 100) throw invalid("语速范围为 -50% 至 +100%。");
            }
            if (!provider.supportsStyle() && !style.isEmpty()) throw invalid("Edge 不支持朗读风格参数。");
            if (!provider.supportsRate() && rate != 0) throw invalid("Gemini 请通过朗读风格描述语速。");
            return new TtsRequest(provider.id(), text, voice, style, rate);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw invalid("JSON 格式不正确。");
        }
    }
    private static String string(com.fasterxml.jackson.databind.JsonNode node, String name, String fallback) {
        var value = node.get(name);
        if (value == null || value.isNull()) return fallback;
        if (!value.isTextual()) throw invalid(name + " 必须是字符串。");
        return value.textValue();
    }
    private static ApiException invalid(String message) { return new ApiException(400, "VALIDATION_ERROR", message); }
}
