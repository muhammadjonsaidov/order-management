package com.intern.order.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisConfig {

    /**
     * Bu standart ObjectMapper. @RestController'lar shu bean'dan foydalanadi.
     * @Primary annotatsiyasi Spring'ga bir nechta ObjectMapper bo'lganda,
     * standart sifatida aynan shuni ishlatishni aytadi.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    /**
     * Bu faqat Redis uchun ishlatiladigan maxsus ObjectMapper.
     * U standart ObjectMapper'ni klonlaydi va qo'shimcha "default typing" sozlamasini qo'shadi.
     */
    private ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = objectMapper().copy(); // Standart sozlamalarni klonlaymiz
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return objectMapper;
    }

    /**
     * Bu Bean Redis kesh sozlamalarini belgilaydi va faqat Redis uchun
     * mo'ljallangan maxsus ObjectMapper'dan foydalanadi.
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(redisObjectMapper())
                ));
    }
}