package com.example.loggingstarter.Aspect;

import com.example.loggingstarter.annotation.Cached;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class CachedAspect {

    private final Map<String, Map<Object, CacheEntry>> caches = new ConcurrentHashMap<>();

    @Value("${cache.time}")
    private long cacheTime;

    @Around("@annotation(cached)")
    public Object handleCache(ProceedingJoinPoint joinPoint, Cached cached) throws Throwable {
        String cacheName = cached.cacheName();
        caches.putIfAbsent(cacheName, new ConcurrentHashMap<>());
        Map<Object, CacheEntry> cache = caches.get(cacheName);

        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            return joinPoint.proceed();
        }

        Object key = args[0];

        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired(cacheTime)) {
            log.info("CACHE HIT {}({})", joinPoint.getSignature().getName(), key);
            return entry.value;
        }

        log.info("CACHE MISS {}({})", joinPoint.getSignature().getName(), key);
        Object result = joinPoint.proceed();

        cache.put(key, new CacheEntry(result, LocalDateTime.now()));

        return result;
    }

    @Scheduled(fixedDelay = 60000)
    public void cleanUp() {
        caches.values().forEach(cache ->
                cache.entrySet().removeIf(e -> e.getValue().isExpired(cacheTime))
        );
    }

    private record CacheEntry(Object value, LocalDateTime createdAt) {
        boolean isExpired(long ttlSeconds) {
            return Duration.between(createdAt, LocalDateTime.now()).getSeconds() > ttlSeconds;
        }
    }
}

