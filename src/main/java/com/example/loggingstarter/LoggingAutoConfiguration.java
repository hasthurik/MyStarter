package com.example.loggingstarter;

import com.example.loggingstarter.Aspect.HttpOutcomeRequestLogAspect;
import com.example.loggingstarter.Aspect.LogDatasourceErrorAspect;
import com.example.loggingstarter.Aspect.MetricAspect;
import com.example.loggingstarter.repository.ErrorLogRepo;
import com.example.loggingstarter.service.LoggingFallbackService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import com.example.loggingstarter.Aspect.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@AutoConfiguration
@ComponentScan(basePackages = "com.example.loggingstarter")
@EnableJpaRepositories(basePackages = "com.example.loggingstarter.repository")
@EntityScan(basePackages = "com.example.loggingstarter.entity")
@Slf4j
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LoggingFallbackService loggingFallbackService(ErrorLogRepo errorLogRepo) {
        log.info("LoggingFallbackService initialized");
        return new LoggingFallbackService(errorLogRepo);
    }

    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    @ConditionalOnMissingBean
    public MetricAspect metricAspect(KafkaTemplate<String, Object> kafkaTemplate,
                                     LoggingFallbackService fallbackService) {
        log.info("MetricAspect initialized");
        return new MetricAspect(kafkaTemplate, fallbackService);
    }

    @Bean
    @ConditionalOnMissingBean
    public CachedAspect cachedAspect() {
        log.info("CachedAspect initialized");
        return new CachedAspect();
    }

    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    @ConditionalOnMissingBean
    public HttpIncomeRequestLogAspect httpIncomeRequestLogAspect(KafkaTemplate<String, Object> kafkaTemplate) {
        log.info("HttpIncomeRequestLogAspect initialized");
        return new HttpIncomeRequestLogAspect(kafkaTemplate);
    }

    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    @ConditionalOnMissingBean
    public HttpOutcomeRequestLogAspect httpOutcomeRequestLogAspect(KafkaTemplate<String, Object> kafkaTemplate) {
        log.info("HttpOutcomeRequestLogAspect initialized");
        return new HttpOutcomeRequestLogAspect(kafkaTemplate);
    }

    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    @ConditionalOnMissingBean
    public LogDatasourceErrorAspect logDatasourceErrorAspect(KafkaTemplate<String, Object> kafkaTemplate,
                                                             ErrorLogRepo errorLogRepo) {
        log.info("LogDatasourceErrorAspect initialized");
        return new LogDatasourceErrorAspect(kafkaTemplate, errorLogRepo);
    }
}
