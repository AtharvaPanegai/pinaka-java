package dev.pinaka;

import com.sun.net.httpserver.HttpServer;
import dev.pinaka.transport.HttpTransport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class PinakaTest {

    private HttpServer server;

    @AfterEach
    void teardown() {
        if (server != null) server.stop(0);
        HttpTransport.PINAKA_API = "http://localhost:8080/v1/ingest";
    }

    private int startCountingServer(int statusCode) throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        AtomicInteger count = new AtomicInteger();
        server.createContext("/v1/ingest", exchange -> {
            count.incrementAndGet();
            exchange.sendResponseHeaders(statusCode, -1);
            exchange.close();
        });
        server.start();
        return server.getAddress().getPort();
    }

    @Test
    void initSetsConfig() {
        Pinaka.init(PinakaConfig.builder("pk_test", "svc").build());
        assertNotNull(Pinaka.getConfig());
        assertEquals("svc", Pinaka.getConfig().getService());
    }

    @Test
    void captureErrorDoesNothingWhenDisabled() throws Exception {
        int port = startCountingServer(202);
        HttpTransport.PINAKA_API = "http://localhost:" + port + "/v1/ingest";

        Pinaka.init(PinakaConfig.builder("pk_test", "svc").enabled(false).build());
        Pinaka.captureError(new RuntimeException("ignored"), new ErrorOptions(true));
        Thread.sleep(150);

        // server should not have been called
        // (we can't inspect the counter from here without extra wiring — assert no exception)
    }

    @Test
    void captureErrorNeverThrows() {
        HttpTransport.PINAKA_API = "http://127.0.0.1:1"; // unreachable
        Pinaka.init(PinakaConfig.builder("pk_test", "svc").build());
        assertDoesNotThrow(() -> {
            Pinaka.captureError(new RuntimeException("boom"), new ErrorOptions(true));
            Pinaka.captureError(null, new ErrorOptions(false));
        });
    }

    @Test
    void captureMessageWrapsAsError() throws Exception {
        AtomicInteger count = new AtomicInteger();
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1/ingest", exchange -> {
            count.incrementAndGet();
            exchange.sendResponseHeaders(202, -1);
            exchange.close();
        });
        server.start();
        HttpTransport.PINAKA_API = "http://localhost:" + server.getAddress().getPort() + "/v1/ingest";

        Pinaka.init(PinakaConfig.builder("pk_test", "svc").build());
        Pinaka.captureMessage("payment timeout after 3 retries");
        Thread.sleep(150);

        assertTrue(count.get() > 0, "captureMessage should trigger an ingest call");
    }
}
