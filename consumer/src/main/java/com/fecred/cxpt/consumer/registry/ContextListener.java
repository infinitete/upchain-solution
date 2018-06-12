package com.fecred.cxpt.consumer.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.HashMap;
import java.util.Map;

import com.fecred.cxpt.consumer.utils.netUtil;

//
// 系统启动以后
// 注册自己
//
@Configuration
public class ContextListener implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${server.port}")
    private String port = "8080";

    @Value("${node}")
    private String nodeName;

    @Value("${service.name}")
    private String serviceName = "generator";

    @Autowired
    private CuratorFramework client;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        //
        // 等待 client 成功注入
        //
        if (client == null) {
            return;
        }

        try {
            ServiceInstanceBuilder<Map> service = ServiceInstance.builder();

            service.address(netUtil.getLocalIp());
            service.port(Integer.parseInt(this.port));
            service.name(serviceName);

            Map payload = new HashMap();
            payload.put("start", "/node/start");
            payload.put("status", "/node/status");
            service.payload(payload);

            ServiceInstance<Map> instance = service.build();

            ServiceDiscovery<Map> serviceDiscovery = ServiceDiscoveryBuilder.builder(Map.class)
                    .client(this.client)
                    .serializer(new JsonInstanceSerializer<Map>(Map.class))
                    .basePath("/service")
                    .build();

            // 注册服务
            serviceDiscovery.registerService(instance);
            serviceDiscovery.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
