package blog.svenbayer.cacherefreshahead.redis.config.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

    private CacheProperties redisCacheSettings;

    @Autowired
    public RedisCacheConfig(CacheProperties redisCacheSettings) {
        this.redisCacheSettings = redisCacheSettings;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(redisCacheSettings.getRedis().getTimeToLive())
                .disableCachingNullValues();
    }

    @Bean
    public CacheManager reloadAheadRedisCacheManager(RedisConnectionFactory redisConnectionFactory, RedisCacheConfiguration redisCacheConfiguration) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .transactionAware()
                .build();
    }
}