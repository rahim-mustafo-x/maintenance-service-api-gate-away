package org.safa.maintenanceserviceapigateaway.rateLimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {
    private final Map<String, Bucket> strictBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> regularBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> scrollBuckets = new ConcurrentHashMap<>();

    @Override
    public Bucket resolveStrictBucket(String sessionKey) {
        return strictBuckets.computeIfAbsent(sessionKey, _->Bucket.builder().addLimit(
                Bandwidth.builder().capacity(10).refillIntervally(5, Duration.ofMinutes(3)).build()
        ).build());
    }

    @Override
    public Bucket resolveRegularBucket(String sessionKey) {
        return regularBuckets.computeIfAbsent(sessionKey, _->Bucket.builder().addLimit(
                Bandwidth.builder().capacity(20).refillIntervally(20, Duration.ofMinutes(2)).build()
        ).build());
    }

    @Override
    public Bucket resolveScrollBucket(String sessionKey) {
        return scrollBuckets.computeIfAbsent(sessionKey, _->Bucket.builder().addLimit(
                Bandwidth.builder().capacity(100).refillIntervally(50, Duration.ofMinutes(2)).build()
        ).build());
    }
}
