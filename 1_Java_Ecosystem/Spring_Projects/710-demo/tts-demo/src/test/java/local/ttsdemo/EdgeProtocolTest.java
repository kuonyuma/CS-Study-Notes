package local.ttsdemo;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class EdgeProtocolTest {
    @Test void escapesXmlWithoutSplittingEntitiesOrUnicodeCharacters() {
        assertEquals("你好 &amp; &lt;world&gt;", String.join("", EdgeProtocol.escapedChunks("你好 & <world>")));
        var chunks = EdgeProtocol.escapedChunks("你好🙂&<>".repeat(500));
        assertTrue(chunks.size() > 1);
        assertEquals("你好🙂&amp;&lt;&gt;".repeat(500), String.join("", chunks));
        for (var chunk : chunks) assertTrue(chunk.getBytes(StandardCharsets.UTF_8).length <= 3500);
    }
    @Test void stripsTwoByteLengthAndHeadersFromBinaryAudioFrame() {
        var headers = "Path:audio\r\nContent-Type:audio/mpeg\r\n".getBytes(StandardCharsets.UTF_8);
        var frame = ByteBuffer.allocate(2 + headers.length + 3).putShort((short)headers.length).put(headers).put(new byte[]{1,2,3}).array();
        assertArrayEquals(new byte[]{1,2,3}, EdgeProtocol.audioPayload(frame));
    }
    @Test void rejectsTruncatedFramesRatherThanReturningCorruptAudio() {
        assertThrows(ApiException.class, () -> EdgeProtocol.audioPayload(new byte[]{0,50,1}));
    }
}
