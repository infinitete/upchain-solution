package com.fecred.cxpt.monitor.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class Nodes {

    @Autowired
    private CuratorFramework client;

    public Collection<ServiceInstance<Map>> getServices () {
        ServiceDiscovery<Map> serviceDiscovery = ServiceDiscoveryBuilder.builder(Map.class)
                .client(client)
                .serializer(new JsonInstanceSerializer<Map>(Map.class))
                .basePath("/service")
                .build();

        Collection<ServiceInstance<Map>> services = null;

        try {
            serviceDiscovery.start();
            services = serviceDiscovery.queryForInstances("generator");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return services;
    }
}
