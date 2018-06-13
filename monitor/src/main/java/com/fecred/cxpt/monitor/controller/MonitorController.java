package com.fecred.cxpt.monitor.controller;

import com.fecred.cxpt.monitor.common.Response;
import com.fecred.cxpt.monitor.discovery.Nodes;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@EnableAutoConfiguration
public class MonitorController {

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private Nodes nodeService;

    private String[] AllowOrders = {"start", "pause", "unpause"};

    @KafkaListener(id = "monitor", topics = "status")
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

    @RequestMapping("/api/v1/{node}/{action}")
    @ResponseBody
    public Response control(@PathVariable String node, @PathVariable String action) {

        Response response = new Response();
        Map data = new HashMap<String, String>();

        boolean allowed = false;

        for (int i = 0; i < AllowOrders.length; i ++) {
            if (AllowOrders[i].toLowerCase().equals(action.toLowerCase())) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            response.setCode(403);
            data.put("Message", "Action Not Allowed");
            response.setData(data);

            return response;
        }

        Collection<ServiceInstance<Map>> services = nodeService.getServices();
        ServiceInstance<Map> service = null;

        for(Iterator iter = services.iterator(); iter.hasNext();){
            ServiceInstance<Map> temp = (ServiceInstance<Map>) iter.next();

            if (temp.getPayload().get("nodeName").equals(node)) {
                service = temp;
                break;
            }
        }

        if (service == null) {
            response.setCode(404);
            data.put("Message", "Node " + node + " does not exist or is down");
            response.setData(data);

            return response;
        }

        template.send(node+"-control", "control", action);

        response.setCode(200);
        data.put("Message", "Order has been sent");
        response.setData(data);

        return response;
    }
}
