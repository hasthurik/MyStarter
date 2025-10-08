package com.example.loggingstarter.Aspect;

import com.example.loggingstarter.entity.ErrorLog;
import com.example.loggingstarter.repository.ErrorLogRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogDatasourceErrorAspect {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ErrorLogRepo errorLogRepo;

    @Around("@annotation(com.example.loggingstarter.annotation.LogDatasourceError)")
    public Object logErrors(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            String methodSignature = joinPoint.getSignature().toShortString();
            String argsJson = Arrays.toString(joinPoint.getArgs());

            Map<String, Object> errorMsg = Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "method", methodSignature,
                    "exception", ex.getMessage(),
                    "stacktrace", Arrays.toString(ex.getStackTrace()),
                    "args", argsJson
            );

            try {
                kafkaTemplate.send("service_logs", "account-processing", errorMsg);
                kafkaTemplate.send("service_logs", "account-processing",
                        MessageBuilder.withPayload(errorMsg)
                                .setHeader("type", "ERROR")
                                .build()
                );
            } catch (Exception kafkaEx) {

                ErrorLog errorLog = ErrorLog.builder()
                        .timestamp(LocalDateTime.now())
                        .methodSignature(methodSignature)
                        .exceptionMessage(ex.getMessage())
                        .stacktrace(Arrays.toString(ex.getStackTrace()))
                        .argsJson(argsJson)
                        .build();
                errorLogRepo.save(errorLog);
            }

            log.error("Error in {}: {}", methodSignature, ex.getMessage(), ex);
            throw ex;
        }
    }
}
