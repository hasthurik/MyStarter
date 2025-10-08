package com.example.loggingstarter.Aspect;

import com.example.loggingstarter.service.LoggingFallbackService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class MetricAspect {

    @Value("${metric.limit}")
    private long limitMs;

    @Value("${spring.application.name:-service}")
    private String serviceName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MetricAspect(KafkaTemplate<String, Object> kafkaTemplate, LoggingFallbackService fallbackService) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Around("@annotation(com.example.loggingstarter.annotation.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;

        if (duration > limitMs) {
            String methodSignature = joinPoint.getSignature().toShortString();
            Object[] args = joinPoint.getArgs();

            Map<String, Object> logData = new HashMap<>();
            logData.put("timestamp", LocalDateTime.now());
            logData.put("method", methodSignature);
            logData.put("duration_ms", duration);
            logData.put("args", Arrays.toString(args));

            log.warn("Method {} took {} ms (> {} ms)", methodSignature, duration, limitMs);

            kafkaTemplate.send("service_logs", serviceName, logData);
        }

        return result;
    }
}

