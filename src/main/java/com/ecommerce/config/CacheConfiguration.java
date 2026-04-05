package com.ecommerce.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {
    // Annotation-based caching будет работать через Spring Cache abstraction
    // В dev используется in-memory, в prod можно подключить Redis
}