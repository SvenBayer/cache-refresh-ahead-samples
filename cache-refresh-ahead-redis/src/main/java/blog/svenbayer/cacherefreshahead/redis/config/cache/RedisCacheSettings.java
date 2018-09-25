package blog.svenbayer.cacherefreshahead.redis.config.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = RedisCacheSettings.SPRING_CACHE_REDIS)
public class RedisCacheSettings {

    static final String SPRING_CACHE_REDIS = "spring.cache.redis";

    @NotNull(message = "Time-to-live of Redis cache should be zero or positive")
    private Duration timeToLive;

    public Duration getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }
}
