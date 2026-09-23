package org.safa.maintenanceserviceapigateaway.rateLimiter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.safa.maintenanceserviceapigateaway.ApiResponse;
import org.safa.maintenanceserviceapigateaway.jwt.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RateLimiterInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final RateLimiterService rateLimiterService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {

        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();

        String sessionKey = resolveSessionKey(request);
        Bucket bucket = resolveBucket(uri, httpMethod, sessionKey);

        if (bucket.tryConsume(1)) {
            return true;
        }

        handleRateLimitExceeded(response);
        return false;
    }

    private String resolveSessionKey(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();

        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            String token = authorization.substring(BEARER_PREFIX.length());
            long userId = jwtService.extractUserId(token);

            return "rate::limit::user:"
                    + userId
                    + ":"
                    + httpMethod
                    + ":"
                    + uri;
        }

        String remoteAddress = request.getRemoteAddr();

        return "rate::limit::ip:"
                + remoteAddress
                + ":"
                + httpMethod
                + ":"
                + uri;
    }

    private Bucket resolveBucket(
            String uri,
            String httpMethod,
            String sessionKey
    ) {
        if (isDocumentationRequest(uri)) {
            return rateLimiterService.resolveRegularBucket(sessionKey);
        }

        if (isStrictRequest(uri, httpMethod)) {
            return rateLimiterService.resolveStrictBucket(sessionKey);
        }

        if (isSearchRequest(uri, httpMethod)) {
            return rateLimiterService.resolveScrollBucket(sessionKey);
        }

        return rateLimiterService.resolveRegularBucket(sessionKey);
    }

    private boolean isDocumentationRequest(String uri) {
        return uri.contains("/swagger-ui")
                || uri.contains("/api-docs")
                || uri.contains("/v3/api-docs");
    }

    private boolean isStrictRequest(String uri, String httpMethod) {
        return uri.contains("/auth")
                && Set.of("POST", "DELETE", "PATCH").contains(httpMethod);
    }

    private boolean isSearchRequest(String uri, String httpMethod) {
        return "GET".equals(httpMethod)
                && (uri.contains("/search") || uri.contains("/scroll"));
    }

    private void handleRateLimitExceeded(
            HttpServletResponse response
    ) throws Exception {

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", "60");

        ApiResponse<Void> responseBody = ApiResponse.<Void>builder()
                .code(HttpStatus.TOO_MANY_REQUESTS.value())
                .message("Too Many Requests, please wait!")
                .build();

        response.getWriter().write(
                objectMapper.writeValueAsString(responseBody)
        );
    }
}