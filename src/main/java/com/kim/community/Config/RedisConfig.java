package com.kim.community.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 設置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // 設置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        // value本身是hash 因此要設置hash的序列化方式
        // 設置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 設置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();
        return template;
    }
}
