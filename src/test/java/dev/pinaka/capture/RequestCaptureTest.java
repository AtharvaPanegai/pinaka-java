package dev.pinaka.capture;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequestCaptureTest {

    @Test
    void stripsQueryString() {
        String result = RequestCapture.sanitizePath("/users/profile?id=123&token=secret");
        assertEquals("/users/profile", result);
    }

    @Test
    void leavesCleanPathUnchanged() {
        String result = RequestCapture.sanitizePath("/api/v1/orders");
        assertEquals("/api/v1/orders", result);
    }

    @Test
    void handlesNullInput() {
        assertEquals("/", RequestCapture.sanitizePath(null));
    }

    @Test
    void handlesRootWithQuery() {
        assertEquals("/", RequestCapture.sanitizePath("/?debug=true"));
    }
}
