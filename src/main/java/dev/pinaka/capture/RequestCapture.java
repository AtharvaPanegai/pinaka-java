package dev.pinaka.capture;

public class RequestCapture {

    // Strip query parameters — never capture query values.
    public static String sanitizePath(String url) {
        if (url == null) return "/";
        int idx = url.indexOf('?');
        return idx == -1 ? url : url.substring(0, idx);
    }
}
