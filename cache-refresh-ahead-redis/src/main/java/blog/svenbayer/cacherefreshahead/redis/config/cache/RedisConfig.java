package blog.svenbayer.cacherefreshahead.redis.config.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@EnableConfigurationProperties(RedisSettings.class)
@Configuration
public class RedisConfig {

    private RedisSettings redisCacheSettings;

    @Autowired
    public RedisConfig(RedisSettings redisCacheSettings) {
        this.redisCacheSettings = redisCacheSettings;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
        redisConf.setHostName(redisCacheSettings.getHost());
        redisConf.setPort(redisCacheSettings.getPort());
        redisConf.setPassword(RedisPassword.of(redisCacheSettings.getPassword()));
        return new LettuceConnectionFactory(redisConf);
    }
}
