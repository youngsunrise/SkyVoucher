package com.hellobike.skyvoucher.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.IOException;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String hostIP;

    @Bean
    public RedissonClient getClient() throws IOException {
        Config config = new Config();
//        可以读yaml配置，也可以直接写地址，见官方文档：https://redisson.org/docs/getting-started/
        config.useSingleServer()
                .setAddress("redis://" + hostIP + ":6379")
                .setPassword("123456");
        return Redisson.create(config);
    }
}
