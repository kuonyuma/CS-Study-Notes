package local.ttsdemo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TtsRequestTest {
    @Test void rejectsBlankTextBeforeContactingAnyProvider() {
        var error = assertThrows(ApiException.class, () -> TtsRequest.parse("{\"provider\":\"edge\",\"text\":\"  \"}"));
        assertEquals(400, error.status());
    }
    @Test void defaultsVoiceAndTrimsText() {
        var request = TtsRequest.parse("{\"provider\":\"edge\",\"text\":\"  你好  \"}");
        assertEquals("你好", request.text());
        assertEquals("zh-CN-XiaoxiaoNeural", request.voice());
        assertEquals(0, request.rate());
    }
    @Test void rejectsCrossProviderVoiceAndFractionalRate() {
        assertThrows(ApiException.class, () -> TtsRequest.parse("{\"provider\":\"gemini\",\"text\":\"hello\",\"voice\":\"zh-CN-XiaoxiaoNeural\"}"));
        assertThrows(ApiException.class, () -> TtsRequest.parse("{\"provider\":\"edge\",\"text\":\"hello\",\"rate\":1.5}"));
    }
    @Test void rejectsExcessiveTextAndWrongFieldTypes() {
        assertThrows(ApiException.class, () -> TtsRequest.parse("{\"provider\":\"edge\",\"text\":\"" + "a".repeat(3001) + "\"}"));
        assertThrows(ApiException.class, () -> TtsRequest.parse("{\"provider\":\"edge\",\"text\":123}"));
        assertThrows(ApiException.class, () -> TtsRequest.parse("[]"));
        assertThrows(ApiException.class, () -> TtsRequest.parse("{\"provider\":\"edge\",\"text\":\"hello\",\"rate\":101}"));
        assertThrows(ApiException.class, () -> TtsRequest.parse("{\"provider\":\"other\",\"text\":\"hello\"}"));
    }
}
