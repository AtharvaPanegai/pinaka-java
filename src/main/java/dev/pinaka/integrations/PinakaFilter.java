package dev.pinaka.integrations;

import dev.pinaka.ErrorOptions;
import dev.pinaka.Pinaka;
import dev.pinaka.capture.RequestCapture;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PinakaFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpRes = (HttpServletResponse) res;
        long start = System.currentTimeMillis();

        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            Pinaka.captureError(e, new ErrorOptions(false)
                    .request(new ErrorOptions.RequestContext(
                            httpReq.getMethod(),
                            RequestCapture.sanitizePath(httpReq.getRequestURI()),
                            500,
                            duration
                    )));
            throw new RuntimeException(e);
        }
    }
}
