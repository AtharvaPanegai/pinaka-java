package dev.pinaka.integrations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pinaka")
public class PinakaProperties {
    private String apiKey;
    private String service;
    private String environment;
    private String release;
    private int maxLogLines = 100;
    private boolean debug = false;
    private boolean enabled = true;

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getRelease() { return release; }
    public void setRelease(String release) { this.release = release; }

    public int getMaxLogLines() { return maxLogLines; }
    public void setMaxLogLines(int maxLogLines) { this.maxLogLines = maxLogLines; }

    public boolean isDebug() { return debug; }
    public void setDebug(boolean debug) { this.debug = debug; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
