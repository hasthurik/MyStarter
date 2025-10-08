package com.example.loggingstarter.Aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class HttpOutcomeRequestLogAspect {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @AfterReturning(pointcut = "@annotation(com.example.loggingstarter.annotation.HttpOutcomeRequestLog)", returning = "result")
    public void logHttpRequests(JoinPoint joinPoint, Object result) {
        String methodSignature = joinPoint.getSignature().toShortString();

        Map<String, Object> logMsg = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "method", methodSignature,
                "args", Arrays.toString(joinPoint.getArgs()),
                "result", result != null ? result.toString() : "null"
        );

        kafkaTemplate.send("service_logs",
                MessageBuilder.withPayload(logMsg)
                        .setHeader("type", "INFO")
                        .setHeader("service", "account-processing")
                        .build()
        );

        log.info("HTTP OUT request logged: {}", logMsg);
    }
}
