package blog.svenbayer.cacherefreshahead.redis.config.cache;

import blog.svenbayer.cacherefreshahead.redis.config.cache.converter.RedisAheadKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;
import java.util.Collections;

@EnableScheduling
@EnableCaching
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Autowired
    private RedisAheadKeyConverter redisAheadKeyConverter;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        GenericConversionService genericConversionService = new GenericConversionService();
        genericConversionService.addConverter(redisAheadKeyConverter);
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(300))
                //.serializeKeysWith(new JsonSerializationPair())
                //.serializeValuesWith(new JsonSerializationPair())
                .withConversionService(genericConversionService)
                .disableCachingNullValues();
    }

    @Bean
    public CacheManager reloadAheadRedisCacheManager(RedisConnectionFactory redisConnectionFactory, RedisCacheConfiguration redisCacheConfiguration) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .transactionAware()
                .disableCreateOnMissingCache()
                .initialCacheNames(Collections.singleton("longrun"))
                .build();

        /*CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        CacheLoader<Object, Object> loader = this::reloadAheadMethod;
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(3L)
                //.expireAfterWrite(Duration.ofSeconds(8L))
                //.refreshAfterWrite(Duration.ofSeconds(4L))
                .writer(new CacheWriter<Object, Object>() {
                    @Override
                    public void write(Object o, Object o2) {
                        logger.info("CacheWriter: writing " + o + " with " + o2);
                    }

                    @Override
                    public void delete(Object o, Object o2, RemovalCause removalCause) {
                        logger.info("CacheWriter: deleting " + o + " with " + o2 + " because of " + removalCause.name());
                    }
                }).executor(new Executor() {
                    @Override
                    public void execute(Runnable command) {
                        logger.info("Executor: I am being executed with " + command);
                    }
                })
                .expireAfter(new Expiry<Object, Object>() {
                    @Override
                    public long expireAfterCreate(Object o, Object o2, long l) {
                        return 10_000L;
                    }

                    @Override
                    public long expireAfterUpdate(Object o, Object o2, long l, long l1) {
                        return 10_000L;
                    }

                    @Override
                    public long expireAfterRead(Object o, Object o2, long l, long l1) {
                        return 10_000L;
                    }
                })
                .recordStats();
        cacheManager.setCaffeine(caffeine);
        return cacheManager;*/
    }
}