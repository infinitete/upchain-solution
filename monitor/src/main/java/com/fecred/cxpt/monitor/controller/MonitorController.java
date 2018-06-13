package com.fecred.cxpt.monitor.controller;

import com.fecred.cxpt.monitor.discovery.Nodes;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class MonitorController {

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private Nodes nodeService;

    @KafkaListener(id = "test", topics = "status")
    public void listener(ConsumerRecord<String, String> cr) throws Exception {

        System.out.println(cr.key() + " - " + cr.value());
    }

    @RequestMapping("/api/v1/services")
    @ResponseBody
    public  ArrayList<ServiceInstance<Map>> services() {
        Collection<ServiceInstance<Map>> services = nodeService.getServices();

        ArrayList<ServiceInstance<Map>> response = null;

        if (!services.isEmpty()) {
            response = new ArrayList<ServiceInstance<Map>>(services);
        }

        return  response;
    }
}
