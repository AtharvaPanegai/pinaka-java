package dev.pinaka.capture;

import dev.pinaka.ErrorOptions;
import dev.pinaka.PinakaConfig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ErrorCapture {

    public static Map<String, Object> buildPayload(PinakaConfig config, Throwable error, ErrorOptions opts) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("apiKey", config.getApiKey());
        payload.put("service", config.getService());
        if (config.getRepoId() != null && !config.getRepoId().isBlank()) {
            payload.put("repoId", config.getRepoId());
        }
        payload.put("environment", config.getEnvironment());
        payload.put("language", "java");
        payload.put("sdkVersion", "0.1.0");

        Map<String, Object> errorBlock = new HashMap<>();
        errorBlock.put("message", error.getMessage() != null ? error.getMessage() : error.toString());
        errorBlock.put("type", error.getClass().getName());
        errorBlock.put("stackTrace", stackTraceToString(error));
        errorBlock.put("handled", opts.isHandled());
        payload.put("error", errorBlock);

        Map<String, Object> context = new HashMap<>();
        context.put("recentLogs", LogCapture.getRecentLogs());
        context.put("serviceName", config.getService());
        context.put("serviceVersion", config.getRelease());
        context.put("hostname", getHostname());
        context.put("timestamp", Instant.now().toString());

        String commitSha = System.getenv("GIT_COMMIT");
        if (commitSha == null) commitSha = System.getenv("COMMIT_SHA");
        if (commitSha != null) context.put("commitSha", commitSha);

        if (opts.getRequest() != null) {
            Map<String, Object> req = new HashMap<>();
            req.put("method", opts.getRequest().method);
            req.put("path", opts.getRequest().path);
            req.put("statusCode", opts.getRequest().statusCode);
            req.put("durationMs", opts.getRequest().durationMs);
            context.put("request", req);
        }

        payload.put("context", context);
        return payload;
    }

    private static String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
