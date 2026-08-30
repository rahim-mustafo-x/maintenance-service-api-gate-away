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
    private final RateLimiterService rateLimiterService;
    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        var uri = request.getRequestURI();
        var auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        var httpMethod = request.getMethod();
        String sessionKey;
        if (auth != null && auth.startsWith("Bearer ")) {
            var userId = jwtService.extractUserId(auth.substring(7));
            sessionKey = "rate::limit::"+userId+"::"+uri;
        }else {
            var localAddr = request.getLocalAddr();
            var localName = request.getLocalName();
            var localPort = request.getLocalPort();
            sessionKey = "rate::limit::"+localAddr+"::"+localName+"::"+localPort;

        }
        Bucket bucket;
        if (uri.contains("/auth") && Set.of("POST", "DELETE", "PATCH").contains(httpMethod)) {
            bucket = rateLimiterService.resolveStrictBucket(sessionKey);
        } else if (uri.contains("/scroll") && httpMethod.contains("GET")) {
            bucket = rateLimiterService.resolveScrollBucket(sessionKey);
        }else {
            bucket = rateLimiterService.resolveRegularBucket(sessionKey);
        }
        if (bucket.tryConsume(1)){
            return true;
        }else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            var responseBody = ApiResponse.builder()
                    .code(HttpStatus.TOO_MANY_REQUESTS.value())
                    .message("Too Many Requests, please wait!")
                    .build();
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(responseBody));
            return false;
        }
    }
}
