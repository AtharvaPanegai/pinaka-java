package dev.pinaka;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PinakaConfigTest {

    @Test
    void rejectsNullApiKey() {
        assertThrows(IllegalArgumentException.class, () ->
            PinakaConfig.builder(null, "svc").build()
        );
    }

    @Test
    void rejectsBlankService() {
        assertThrows(IllegalArgumentException.class, () ->
            PinakaConfig.builder("pk_test", "  ").build()
        );
    }

    @Test
    void defaultsMaxLogLinesToOneHundred() {
        PinakaConfig config = PinakaConfig.builder("pk_test", "svc").build();
        assertEquals(100, config.getMaxLogLines());
    }

    @Test
    void defaultsEnabledToTrue() {
        PinakaConfig config = PinakaConfig.builder("pk_test", "svc").build();
        assertTrue(config.isEnabled());
    }

    @Test
    void acceptsCustomEnvironment() {
        PinakaConfig config = PinakaConfig.builder("pk_test", "svc")
            .environment("staging")
            .build();
        assertEquals("staging", config.getEnvironment());
    }

    @Test
    void disabledFlagIsRespected() {
        PinakaConfig config = PinakaConfig.builder("pk_test", "svc")
            .enabled(false)
            .build();
        assertFalse(config.isEnabled());
    }
}
