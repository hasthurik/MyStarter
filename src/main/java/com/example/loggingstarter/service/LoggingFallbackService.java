//package com.example.loggingstarter.service;
//
//import com.example.loggingstarter.entity.ErrorLog;
//import com.example.loggingstarter.repository.ErrorLogRepo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//
//@Service
//@Slf4j
//public class LoggingFallbackService {
//
//    private final ErrorLogRepo repo;
//
//    public LoggingFallbackService(ErrorLogRepo repo) {
//        this.repo = repo;
//    }
//
//    public void saveError(String method, Throwable ex, Object[] args) {
//        ErrorLog logEntity = ErrorLog.builder()
//                .timestamp(LocalDateTime.now())
//                .methodSignature(method)
//                .exceptionMessage(ex.getMessage())
//                .stacktrace(Arrays.toString(ex.getStackTrace()))
//                .argsJson(Arrays.toString(args))
//                .build();
//
//        repo.save(logEntity);
//        log.warn("Saved error locally in DB (Kafka unavailable)");
//    }
//}
//
