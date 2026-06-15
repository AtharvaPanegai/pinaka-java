package dev.pinaka;

public class PinakaConfig {
    private final String apiKey;
    private final String service;
    private final String environment;
    private final String release;
    private final int maxLogLines;
    private final boolean debug;
    private final boolean enabled;

    private PinakaConfig(Builder builder) {
        this.apiKey = builder.apiKey;
        this.service = builder.service;
        this.environment = builder.environment != null ? builder.environment
                : System.getenv().getOrDefault("APP_ENV", "production");
        this.release = builder.release != null ? builder.release
                : System.getenv().getOrDefault("APP_VERSION", "unknown");
        this.maxLogLines = builder.maxLogLines > 0 ? builder.maxLogLines : 100;
        this.debug = builder.debug;
        this.enabled = builder.enabled;
    }

    public String getApiKey() { return apiKey; }
    public String getService() { return service; }
    public String getEnvironment() { return environment; }
    public String getRelease() { return release; }
    public int getMaxLogLines() { return maxLogLines; }
    public boolean isDebug() { return debug; }
    public boolean isEnabled() { return enabled; }

    public static Builder builder(String apiKey, String service) {
        return new Builder(apiKey, service);
    }

    public static class Builder {
        private final String apiKey;
        private final String service;
        private String environment;
        private String release;
        private int maxLogLines = 100;
        private boolean debug = false;
        private boolean enabled = true;

        private Builder(String apiKey, String service) {
            if (apiKey == null || apiKey.isBlank()) throw new IllegalArgumentException("apiKey is required");
            if (service == null || service.isBlank()) throw new IllegalArgumentException("service is required");
            this.apiKey = apiKey;
            this.service = service;
        }

        public Builder environment(String env) { this.environment = env; return this; }
        public Builder release(String release) { this.release = release; return this; }
        public Builder maxLogLines(int max) { this.maxLogLines = max; return this; }
        public Builder debug(boolean debug) { this.debug = debug; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }

        public PinakaConfig build() { return new PinakaConfig(this); }
    }
}
