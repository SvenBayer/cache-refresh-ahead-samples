package blog.svenbayer.cacherefreshahead.redis.config.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@EnableConfigurationProperties(RedisSettings.class)
@Configuration
public class RedisConfig {

    private RedisSettings redisSettings;

    @Autowired
    public RedisConfig(RedisSettings redisSettings) {
        this.redisSettings = redisSettings;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
        redisConf.setHostName(redisSettings.getHost());
        redisConf.setPort(redisSettings.getPort());
        redisConf.setPassword(RedisPassword.of(redisSettings.getPassword()));
        return new LettuceConnectionFactory(redisConf);
    }
}
