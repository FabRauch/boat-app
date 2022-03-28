package com.openwt.boatapp.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.openwt.boatapp.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final Long maxEntries;
    private final Integer timeToLiveSeconds;

    public CacheConfiguration(
        @Value("${boatapp.cache.caffeine.max-entries}") Long maxEntries,
        @Value("${boatapp.cache.caffeine.time-to-live-seconds}") Integer timeToLiveSeconds
    ) {
        this.maxEntries = maxEntries;
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        createCache(caffeineCacheManager, UserRepository.USERS_BY_LOGIN_CACHE);
        createCache(caffeineCacheManager, UserRepository.USERS_BY_EMAIL_CACHE);
        return caffeineCacheManager;
    }

    private void createCache(CaffeineCacheManager cm, String cacheName) {
        Cache<Object, Object> newCache = Caffeine
            .newBuilder()
            .maximumSize(this.maxEntries)
            .expireAfterWrite(this.timeToLiveSeconds, TimeUnit.SECONDS)
            .build();
        cm.registerCustomCache(cacheName, newCache);
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
