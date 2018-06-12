package com.fecred.cxpt.monitor.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class MonitorController {

    private KafkaTemplate<String, String> template;


    @KafkaListener(id = "test", topics = "status")
    public void listener(ConsumerRecord<String, String> cr) throws Exception {

        System.out.println(cr.key() + " - " + cr.value());
    }
}
