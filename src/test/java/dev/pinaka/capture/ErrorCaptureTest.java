package dev.pinaka.capture;

import dev.pinaka.ErrorOptions;
import dev.pinaka.PinakaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ErrorCaptureTest {

    private PinakaConfig config;

    @BeforeEach
    void setup() {
        LogCapture.init(100);
        config = PinakaConfig.builder("pk_test", "java-test")
            .environment("test")
            .release("1.0.0")
            .build();
    }

    @Test
    void payloadContainsApiKey() {
        Map<String, Object> payload = ErrorCapture.buildPayload(
            config, new RuntimeException("boom"), new ErrorOptions(true)
        );
        assertEquals("pk_test", payload.get("apiKey"));
    }

    @Test
    void payloadLanguageIsJava() {
        Map<String, Object> payload = ErrorCapture.buildPayload(
            config, new RuntimeException("boom"), new ErrorOptions(true)
        );
        assertEquals("java", payload.get("language"));
    }

    @Test
    void errorBlockContainsMessageAndType() {
        RuntimeException err = new RuntimeException("something failed");
        Map<String, Object> payload = ErrorCapture.buildPayload(config, err, new ErrorOptions(false));

        @SuppressWarnings("unchecked")
        Map<String, Object> errorBlock = (Map<String, Object>) payload.get("error");
        assertEquals("something failed", errorBlock.get("message"));
        assertEquals("java.lang.RuntimeException", errorBlock.get("type"));
        assertFalse(errorBlock.get("stackTrace").toString().isEmpty());
        assertEquals(false, errorBlock.get("handled"));
    }

    @Test
    void requestContextIncludedWhenProvided() {
        ErrorOptions opts = new ErrorOptions(true)
            .request(new ErrorOptions.RequestContext("POST", "/api/pay", 500, 123L));
        Map<String, Object> payload = ErrorCapture.buildPayload(config, new RuntimeException("e"), opts);

        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) payload.get("context");
        @SuppressWarnings("unchecked")
        Map<String, Object> request = (Map<String, Object>) context.get("request");
        assertNotNull(request);
        assertEquals("POST", request.get("method"));
        assertEquals("/api/pay", request.get("path"));
    }

    @Test
    void contextContainsHostnameAndTimestamp() {
        Map<String, Object> payload = ErrorCapture.buildPayload(
            config, new RuntimeException("ctx"), new ErrorOptions(true)
        );
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) payload.get("context");
        assertNotNull(context.get("hostname"));
        assertNotNull(context.get("timestamp"));
        assertTrue(context.get("timestamp").toString().startsWith("20"));
    }
}
