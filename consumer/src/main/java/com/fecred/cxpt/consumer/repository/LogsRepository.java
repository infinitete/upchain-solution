package com.fecred.cxpt.consumer.repository;

import com.fecred.cxpt.consumer.model.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface LogsRepository extends JpaRepository<Logs, Integer> {

}
