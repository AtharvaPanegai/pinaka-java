package dev.pinaka.demo;

import dev.pinaka.ErrorOptions;
import dev.pinaka.Pinaka;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CrashController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "java-demo"));
    }

    @GetMapping("/crash-exception")
    public void crashException() {
        throw new RuntimeException("simulated exception in java service");
    }

    @GetMapping("/crash-npe")
    public void crashNpe() {
        String s = null;
        // noinspection ConstantConditions
        s.length(); // NullPointerException
    }

    @GetMapping("/crash-manual")
    public ResponseEntity<Map<String, String>> crashManual() {
        IllegalStateException err = new IllegalStateException("manual capture from java");
        Pinaka.captureError(err, new ErrorOptions(true));
        return ResponseEntity.ok(Map.of("status", "error captured", "error", err.getMessage()));
    }

    @GetMapping("/crash-assertion")
    public void crashAssertion() {
        throw new AssertionError("assertion failed: balance cannot be negative");
    }
}
