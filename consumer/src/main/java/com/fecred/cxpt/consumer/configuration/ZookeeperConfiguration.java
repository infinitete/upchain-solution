package com.fecred.cxpt.consumer.configuration;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfiguration {

    @Value("${zk.url}")
    private String zkUrl;

    @Bean
    public CuratorFramework getCuratorFramework() {

        //
        // 重试机制
        // 初次1000ms，时间会随着失败次数增加而增加
        // 重试次数为3次，超过3次视为失败
        //
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkUrl, retryPolicy);

        client.start();

        return  client;
    }
}
