package local.ttsdemo;

import java.util.List;
import java.util.Map;

final class Catalog {
    static final String GEMINI_MODEL = "gemini-3.8-flash-tts";
    static final int MAX_TEXT_LENGTH = 3000;
    record Voice(String id, String name, String language) {}
    record Provider(String id, String name, String model, boolean configured,
                    boolean supportsStyle, boolean supportsRate, String defaultVoice, List<Voice> voices) {}

    private static final String[][] GEMINI_VOICES = {
            {"Zephyr", "Bright"}, {"Puck", "Upbeat"}, {"Charon", "Informative"}, {"Kore", "Firm"},
            {"Fenrir", "Excitable"}, {"Leda", "Youthful"}, {"Orus", "Firm"}, {"Aoede", "Breezy"},
            {"Callirrhoe", "Easy-going"}, {"Autonoe", "Bright"}, {"Enceladus", "Breathy"}, {"Iapetus", "Clear"},
            {"Umbriel", "Easy-going"}, {"Algieba", "Smooth"}, {"Despina", "Smooth"}, {"Erinome", "Clear"},
            {"Algenib", "Gravelly"}, {"Rasalgethi", "Informative"}, {"Laomedeia", "Upbeat"}, {"Achernar", "Soft"},
            {"Alnilam", "Firm"}, {"Schedar", "Even"}, {"Gacrux", "Mature"}, {"Pulcherrima", "Forward"},
            {"Achird", "Friendly"}, {"Zubenelgenubi", "Casual"}, {"Vindemiatrix", "Gentle"},
            {"Sadachbia", "Lively"}, {"Sadaltager", "Knowledgeable"}, {"Sulafat", "Warm"}
    };
    private static final List<Voice> GEMINI = java.util.Arrays.stream(GEMINI_VOICES)
            .map(v -> new Voice(v[0], v[0] + " · " + v[1], "multilingual")).toList();
    private static final List<Voice> EDGE = List.of(
            new Voice("zh-CN-XiaoxiaoNeural", "晓晓 · 中文女声", "zh-CN"),
            new Voice("zh-CN-YunxiNeural", "云希 · 中文男声", "zh-CN"),
            new Voice("zh-HK-HiuMaanNeural", "晓曼 · 粤语女声", "zh-HK"),
            new Voice("ja-JP-NanamiNeural", "七海 · 日语女声", "ja-JP"),
            new Voice("ja-JP-KeitaNeural", "圭太 · 日语男声", "ja-JP"),
            new Voice("en-US-JennyNeural", "Jenny · 美式英语女声", "en-US"),
            new Voice("en-US-GuyNeural", "Guy · 美式英语男声", "en-US"),
            new Voice("en-GB-SoniaNeural", "Sonia · 英式英语女声", "en-GB"));

    static List<Provider> providers(boolean keyPresent) {
        return List.of(new Provider("gemini", "Gemini 3.8 Flash TTS", GEMINI_MODEL, keyPresent,
                        true, false, "Kore", GEMINI),
                new Provider("edge", "Microsoft Edge 免费语音", "edge-read-aloud", true,
                        false, true, "zh-CN-XiaoxiaoNeural", EDGE));
    }
    static Provider provider(String id) {
        return providers(true).stream().filter(p -> p.id().equals(id)).findFirst()
                .orElseThrow(() -> new ApiException(400, "VALIDATION_ERROR", "请选择 Gemini 或 Microsoft Edge。"));
    }
    static Map<String, Object> config(boolean keyPresent) {
        return Map.of("maxTextLength", MAX_TEXT_LENGTH, "providers", providers(keyPresent));
    }
    private Catalog() {}
}
