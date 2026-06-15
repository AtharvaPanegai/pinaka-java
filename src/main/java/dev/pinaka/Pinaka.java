package dev.pinaka;

import dev.pinaka.capture.ErrorCapture;
import dev.pinaka.capture.LogCapture;
import dev.pinaka.transport.HttpTransport;

public class Pinaka {
    private static volatile PinakaConfig config;

    public static void init(PinakaConfig cfg) {
        config = cfg;
        if (!cfg.isEnabled()) return;

        LogCapture.init(cfg.getMaxLogLines());

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
            captureError(throwable, new ErrorOptions(false))
        );

        if (cfg.isDebug()) {
            System.out.printf("[Pinaka] Initialized — service: %s, env: %s%n",
                cfg.getService(), cfg.getEnvironment());
        }
    }

    public static void captureError(Throwable error) {
        captureError(error, null);
    }

    public static void captureError(Throwable error, ErrorOptions options) {
        try {
            if (config == null || !config.isEnabled()) return;
            ErrorOptions opts = options != null ? options : new ErrorOptions(true);
            HttpTransport.send(ErrorCapture.buildPayload(config, error, opts));
        } catch (Exception ignored) {
            // SDK must never crash the host application
        }
    }

    public static void captureMessage(String message) {
        captureError(new RuntimeException(message), new ErrorOptions(true));
    }

    public static PinakaConfig getConfig() {
        return config;
    }
}
