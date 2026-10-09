package local.ttsdemo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LocalOriginTest {
    @Test void browserDefaultPortAuthoritiesAreAcceptedWithoutWeakeningHostChecks() {
        assertTrue(TtsServer.isAllowedAuthority("localhost", 80));
        assertTrue(TtsServer.isAllowedAuthority("127.0.0.1", 80));
        assertTrue(TtsServer.isAllowedAuthority("localhost:80", 80));
        assertTrue(TtsServer.isAllowedAuthority("localhost:8787", 8787));
        assertFalse(TtsServer.isAllowedAuthority("localhost", 8787));
        assertFalse(TtsServer.isAllowedAuthority("evil.example:80", 80));
        assertFalse(TtsServer.isAllowedAuthority("localhost.evil.example", 80));
    }
}
