package blog.svenbayer.cacherefreshahead.caffeine.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@EnableCaching
@Configuration
public class CaffeineCacheConfig extends CachingConfigurerSupport {

    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .weigher((key, value) -> value.toString().length())
                .maximumWeight(50L)
                .expireAfterAccess(Duration.ofSeconds(15L))
                .recordStats();
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}