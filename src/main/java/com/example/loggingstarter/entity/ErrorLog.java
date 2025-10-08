package com.example.loggingstarter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String methodSignature;
    private String exceptionMessage;

    @Column(columnDefinition = "TEXT")
    private String stacktrace;

    @Column(columnDefinition = "TEXT")
    private String argsJson;


}
