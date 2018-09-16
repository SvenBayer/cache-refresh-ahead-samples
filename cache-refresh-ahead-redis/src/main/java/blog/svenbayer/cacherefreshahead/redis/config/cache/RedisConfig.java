package blog.svenbayer.cacherefreshahead.redis.config.cache;

import blog.svenbayer.cacherefreshahead.redis.config.cache.converter.RedisRefreshAheadSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@EnableConfigurationProperties(RedisSettings.class)
@Configuration
public class RedisConfig {

    private RedisSettings redisCacheSettings;
    private RedisRefreshAheadSerializer refreshAheadSerializer;

    @Autowired
    public RedisConfig(RedisSettings redisCacheSettings, RedisRefreshAheadSerializer refreshAheadSerializer) {
        this.redisCacheSettings = redisCacheSettings;
        this.refreshAheadSerializer = refreshAheadSerializer;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
        redisConf.setHostName(redisCacheSettings.getHost());
        redisConf.setPort(redisCacheSettings.getPort());
        redisConf.setPassword(RedisPassword.of(redisCacheSettings.getPassword()));
        return new LettuceConnectionFactory(redisConf);
    }

    @Bean
    public RedisTemplate<ReloadAheadKey, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<ReloadAheadKey, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(refreshAheadSerializer);
        return redisTemplate;
    }
}
