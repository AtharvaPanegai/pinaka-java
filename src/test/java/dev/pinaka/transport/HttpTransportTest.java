package dev.pinaka.transport;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class HttpTransportTest {

    private HttpServer server;
    private int port;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicReference<String> lastBody = new AtomicReference<>("");

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();
        requestCount.set(0);
        lastBody.set("");
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
        // restore default endpoint so other tests are not affected
        HttpTransport.PINAKA_API = "http://localhost:8080/v1/ingest";
    }

    private void bindEndpoint(int statusCode) {
        server.createContext("/v1/ingest", exchange -> {
            requestCount.incrementAndGet();
            lastBody.set(new String(exchange.getRequestBody().readAllBytes()));
            exchange.sendResponseHeaders(statusCode, -1);
            exchange.close();
        });
        server.start();
        HttpTransport.PINAKA_API = "http://localhost:" + port + "/v1/ingest";
    }

    @Test
    void sendIsFireAndForget() throws InterruptedException {
        bindEndpoint(202);
        long start = System.currentTimeMillis();
        HttpTransport.send(Map.of("apiKey", "pk_test"));
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 100, "send() should return immediately, took " + elapsed + "ms");
        // drain the async request before @AfterEach stops the server, preventing retry
        // leakage into subsequent tests
        Thread.sleep(200);
    }

    @Test
    void sendPostsPayloadToEndpoint() throws InterruptedException {
        bindEndpoint(202);
        HttpTransport.send(Map.of("apiKey", "pk_test", "service", "java-test"));
        Thread.sleep(200);
        assertEquals(1, requestCount.get());
        assertTrue(lastBody.get().contains("java-test"));
    }

    @Test
    void noRetryOn401() throws InterruptedException {
        bindEndpoint(401);
        HttpTransport.send(Map.of("apiKey", "bad-key"));
        Thread.sleep(200);
        assertEquals(1, requestCount.get(), "should not retry on 401");
    }

    @Test
    void doesNotThrowOnNetworkFailure() {
        HttpTransport.PINAKA_API = "http://127.0.0.1:1"; // nothing listening
        assertDoesNotThrow(() -> HttpTransport.send(Map.of("apiKey", "pk_test")));
    }
}
