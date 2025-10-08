package com.example.loggingstarter.repository;

import com.example.loggingstarter.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogRepo extends JpaRepository<ErrorLog, Long> {
}
