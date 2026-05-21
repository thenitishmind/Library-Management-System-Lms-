package com.library.lms.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration using simple in-memory caching (no Redis required).
 * Switch to RedisCacheManager when Redis is available in production.
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "books", "authors", "categories", "members", "loans", "fines"
        );
    }
}
