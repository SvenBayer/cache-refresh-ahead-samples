package blog.svenbayer.cacherefreshahead.caffeine.config.cache;

import org.cache2k.Cache2kBuilder;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.cache2k.integration.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class Cache2kConfig extends CachingConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(Cache2kConfig.class);

    @Bean
    public CacheManager reloadAheadCache2kManager() {
        CacheLoader<ReloadAheadKey, Object> loader = new CacheLoader<ReloadAheadKey, Object>() {
            @Override
            public Object load(ReloadAheadKey o) {
                return reloadAheadMethod(o);
            }
        };
        SpringCache2kCacheManager cacheManager = new SpringCache2kCacheManager()
                .defaultSetup(builder -> Cache2kBuilder.of(ReloadAheadKey.class, Object.class)
                        .expireAfterWrite(8L, TimeUnit.SECONDS)
                        .resilienceDuration(4L, TimeUnit.SECONDS)
                        .refreshAhead(true)
                        .loader(loader));
        cacheManager.setAllowUnknownCache(true);
        return cacheManager;
    }

    private Object reloadAheadMethod(ReloadAheadKey key) {
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