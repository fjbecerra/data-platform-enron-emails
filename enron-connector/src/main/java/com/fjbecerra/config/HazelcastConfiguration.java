package com.fjbecerra.config;


import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.JoinConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Collections.singletonList;


@Configuration
public class HazelcastConfiguration {

    @Value("${hazelcast.members}")
    private String members;

    @Value("${hazelcast.multicast}")
    private boolean multicast;

    @Value("${hazelcast.executor.pool}")
    private Integer executorPool;

    @Value("${hazelcast.executor.name}")
    private String executorName;

    @Bean
    public Config config() {

        Config config = new Config();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(multicast);
        joinConfig.getTcpIpConfig().setEnabled(true).setMembers(singletonList(members));
        ExecutorConfig executorConfig = new ExecutorConfig().setName(executorName).setPoolSize(executorPool);
        config.addExecutorConfig(executorConfig);

        return config;
    }


}
