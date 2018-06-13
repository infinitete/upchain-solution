package com.fecred.qzcxpt.controller;

import com.alibaba.fastjson.JSON;
import com.fecred.qzcxpt.common.Response;
import com.fecred.qzcxpt.mapper.IPersonal;
import com.fecred.qzcxpt.model.MPersonal;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;

@RestController
@EnableAutoConfiguration
public class HelloController {

    String key;

    HelloController() {
        Double temp = Math.random();

        key = temp.toString();
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private IPersonal iPersonal;

    @RequestMapping("/")
    @ResponseBody
    public Response World() {
        MPersonal personal = iPersonal.getUnONTIDPerson();

        Response response = new Response();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(personal);

        String value = JSONArray.toJSONString(personal);

        personal.setRunning(1);

        iPersonal.Update(personal);

        template.send("test", key, value);

        return response;
    }

    @KafkaListener(id = "ontid", topicPattern = "cnode.*+")
    public void sender(ConsumerRecord<String, String> cr) throws  Exception {

        String key = cr.key();
        String value = cr.value();

        MPersonal personal = JSON.parseObject(value, MPersonal.class);

        // 更新到数据库
        if (personal != null) {
            iPersonal.Update(personal);
        }

        // 取出下一条并发送给对应的generator
        personal = iPersonal.getUnONTIDPerson();

        System.out.println("Topic: " + cr.topic().replaceAll("cnode", "node"));

        template.send(cr.topic().replaceAll("cnode", "node"), key, JSON.toJSONString(personal));
    }
}
