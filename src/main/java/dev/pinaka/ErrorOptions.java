package dev.pinaka;

import java.util.Map;

public class ErrorOptions {
    private final boolean handled;
    private Map<String, String> tags;
    private RequestContext request;

    public ErrorOptions(boolean handled) {
        this.handled = handled;
    }

    public boolean isHandled() { return handled; }
    public Map<String, String> getTags() { return tags; }
    public RequestContext getRequest() { return request; }

    public ErrorOptions tags(Map<String, String> tags) { this.tags = tags; return this; }
    public ErrorOptions request(RequestContext request) { this.request = request; return this; }

    public static class RequestContext {
        public final String method;
        public final String path;
        public final int statusCode;
        public final long durationMs;

        public RequestContext(String method, String path, int statusCode, long durationMs) {
            this.method = method;
            this.path = path;
            this.statusCode = statusCode;
            this.durationMs = durationMs;
        }
    }
}
