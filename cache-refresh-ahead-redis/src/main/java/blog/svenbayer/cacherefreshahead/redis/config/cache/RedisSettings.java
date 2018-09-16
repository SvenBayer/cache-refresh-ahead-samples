package blog.svenbayer.cacherefreshahead.redis.config.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Validated
@ConfigurationProperties(prefix = RedisSettings.SPRING_REDIS)
public class RedisSettings {

    static final String SPRING_REDIS = "spring.redis";

    @NotBlank(message = "Please define a host for Redis!")
    private String host;

    @Positive(message = "Please define a port for Redis!")
    private int port;

    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
