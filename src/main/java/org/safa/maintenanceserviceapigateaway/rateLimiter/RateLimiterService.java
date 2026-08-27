package org.safa.maintenanceserviceapigateaway.rateLimiter;

import io.github.bucket4j.Bucket;

public interface RateLimiterService {
    Bucket resolveStrictBucket(String sessionKey);
    Bucket resolveRegularBucket(String sessionKey);
    Bucket resolveScrollBucket(String sessionKey);
}
