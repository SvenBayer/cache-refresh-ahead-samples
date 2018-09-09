package blog.svenbayer.cacherefreshahead.config.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

@Configuration
public class CaffeineCacheConfig extends CachingConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineCacheConfig.class);

    @Bean
    public CacheManager reloadAheadCaffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        CacheLoader<Object, Object> loader = this::reloadAheadMethod;
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .weigher((key, value) -> value.toString().length())
                .maximumWeight(50L)
                .expireAfterAccess(Duration.ofSeconds(10L))
                .refreshAfterWrite(Duration.ofSeconds(5L))
                .recordStats();
        cacheManager.setCacheLoader(loader);
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

    private Object reloadAheadMethod(Object objectKey) {
        ReloadAheadKey key = (ReloadAheadKey) objectKey;
        try {
            logger.info("Starting re-population for parameters '{}'", key.getParameters());
            Object cacheValue = key.getMethod().invoke(key.getInstance(), key.getParameters());
            logger.info("Finished re-population for parameters '{}' with value '{}'", key.getParameters(), cacheValue);
            return cacheValue;
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("I FAILED TO RELOAD AHEAD!!!");
            throw new IllegalStateException(e);
        }
    }
}