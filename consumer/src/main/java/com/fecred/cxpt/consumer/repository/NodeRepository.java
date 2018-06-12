package com.fecred.cxpt.consumer.repository;

import com.fecred.cxpt.consumer.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface NodeRepository extends JpaRepository<Node, Integer> {
}
