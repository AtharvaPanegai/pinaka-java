package dev.pinaka.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pinaka.Pinaka;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HttpTransport {
    public static String PINAKA_API = "https://api.getpinaka.com/v1/ingest";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void send(Map<String, Object> payload) {
        // Capture endpoint at call time so retries always go to the same destination,
        // even if PINAKA_API is later changed (e.g. in tests).
        final String endpoint = PINAKA_API;
        CompletableFuture.runAsync(() -> sendWithRetry(payload, endpoint, 1))
                .exceptionally(e -> null);
    }

    private static void sendWithRetry(Map<String, Object> payload, String endpoint, int attempt) {
        if (attempt > 3) return;
        try {
            String body = mapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() == 401) {
                if (Pinaka.getConfig() != null && Pinaka.getConfig().isDebug()) {
                    System.err.println("[Pinaka] Invalid API key — check your dashboard at getpinaka.com");
                }
                return;
            }
            if (response.statusCode() == 429 || response.statusCode() >= 500) {
                Thread.sleep(attempt * 1000L);
                sendWithRetry(payload, endpoint, attempt + 1);
            }
        } catch (Exception e) {
            try { Thread.sleep(attempt * 1000L); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            sendWithRetry(payload, endpoint, attempt + 1);
        }
    }
}
