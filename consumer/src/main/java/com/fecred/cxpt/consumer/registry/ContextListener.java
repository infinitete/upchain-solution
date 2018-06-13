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
// 系统成功启动以后
//
// 通过服务注册注册自己
// 主要作用是监控节点可以实时获取在线节点基本信息
// 比如IP、端口、可用的服务
//
@Configuration
public class ContextListener implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${server.port}")
    private String port = "8080";

    ///
    /// 务必保证节点名称的惟一性
    ///
    @Value("${node}")
    private String nodeName;

    ///
    /// 所有节点的服务名称务必要一致
    /// 同时监控节点也要通过服务名称来获取这个这个服务下的所有节点
    ///
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
            payload.put("nodeName", this.nodeName);
            payload.put("start", "/api/v1/node/start");
            payload.put("status", "/api/v1/node/status");
            payload.put("pause", "/api/v1/node/pause");
            payload.put("unpause", "/api/v1/node/unpause");
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
