package dev.pinaka.capture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

class LogCaptureTest {

    @BeforeEach
    void resetBuffer() {
        LogCapture.init(100);
    }

    @Test
    void addStoresEntryWithTimestamp() {
        LogCapture.add("INFO", "test message");
        List<String> logs = LogCapture.getRecentLogs();
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("test message"));
        // ISO timestamp prefix
        assertTrue(logs.get(0).matches("\\d{4}-.*"));
    }

    @Test
    void circularEvictionDropsOldestWhenFull() {
        LogCapture.init(3);
        for (int i = 0; i < 5; i++) {
            LogCapture.add("INFO", "line " + i);
        }
        List<String> logs = LogCapture.getRecentLogs();
        assertEquals(3, logs.size());
        assertFalse(logs.get(0).contains("line 0"), "oldest entry should have been evicted");
        assertTrue(logs.get(2).contains("line 4"), "newest entry should be present");
    }

    @Test
    void getRecentLogsReturnsCopy() {
        LogCapture.add("INFO", "original");
        List<String> logs = LogCapture.getRecentLogs();
        logs.add("injected");
        assertEquals(1, LogCapture.getRecentLogs().size(), "external mutation should not affect buffer");
    }

    @Test
    void initClearsExistingBuffer() {
        LogCapture.add("INFO", "will be cleared");
        LogCapture.init(100);
        assertTrue(LogCapture.getRecentLogs().isEmpty());
    }

    @Test
    void concurrentWritesAreSafe() throws InterruptedException {
        LogCapture.init(1000);
        int threads = 20;
        CountDownLatch latch = new CountDownLatch(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                LogCapture.add("INFO", "concurrent");
                latch.countDown();
            });
        }
        latch.await();
        pool.shutdown();
        assertFalse(LogCapture.getRecentLogs().isEmpty());
    }
}
