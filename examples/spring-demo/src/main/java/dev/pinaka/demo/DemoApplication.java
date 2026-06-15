package dev.pinaka.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println();
        System.out.println("java-demo running on :4002");
        System.out.println("  GET /                  → health check");
        System.out.println("  GET /crash-exception   → RuntimeException (unhandled)");
        System.out.println("  GET /crash-npe         → NullPointerException (unhandled)");
        System.out.println("  GET /crash-manual      → manual captureError, returns 200");
        System.out.println("  GET /crash-assertion   → AssertionError (unhandled)");
        System.out.println();
    }
}
