package dev.pinaka.capture;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class LogCapture {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final List<String> buffer = new ArrayList<>();
    private static int maxLines = 100;

    public static void init(int max) {
        lock.lock();
        try {
            maxLines = max;
            buffer.clear();
        } finally {
            lock.unlock();
        }
    }

    public static void add(String level, String message) {
        String entry = Instant.now() + " " + level + "  " + message;
        lock.lock();
        try {
            buffer.add(entry);
            if (buffer.size() > maxLines) {
                buffer.remove(0);
            }
        } finally {
            lock.unlock();
        }
    }

    public static List<String> getRecentLogs() {
        lock.lock();
        try {
            return new ArrayList<>(buffer);
        } finally {
            lock.unlock();
        }
    }
}
