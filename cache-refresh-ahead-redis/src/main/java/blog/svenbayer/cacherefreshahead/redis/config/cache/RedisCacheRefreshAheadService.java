package blog.svenbayer.cacherefreshahead.redis.config.cache;

import blog.svenbayer.cacherefreshahead.redis.config.cache.converter.RedisAheadKeyConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheRefreshAheadService {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheRefreshAheadService.class);

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisAheadKeyConverter redisAheadKeyConverter;

    @Scheduled(initialDelay = 10_000L, fixedRate = 10_000L)
    public void reloadAheadValuesForKeys() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        Cursor<byte[]> scan = connection.keyCommands().scan(ScanOptions.scanOptions().match("*").build());
        scan.forEachRemaining(key -> {
            logger.info("Reading Key " + new String(key));

            ReloadAheadKey convertedKey = redisAheadKeyConverter.convert(key, ReloadAheadKey.class);
            if (convertedKey != null) {
                Object value = reloadAheadMethod(convertedKey);
                String jsonKey = redisAheadKeyConverter.convert(convertedKey, String.class);
                String jsonValue = null;
                try {
                    jsonValue = mapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException(e);
                }
                try {
                    redisTemplate.opsForValue().set(convertedKey, "HI! " + value, 60L, TimeUnit.SECONDS);
                } catch (Exception e) {
                    logger.error("Could not save key to redis for jsonKey '" + jsonKey, e);
                }
                System.out.println("Updating key: " + jsonKey);
            }
        });
    }

    public Object reloadAheadMethod(Object objectKey) {
        ReloadAheadKey key = (ReloadAheadKey) objectKey;
        try {
            logger.info("Starting re-population for parameters '{}'", key.getParameters());

            Object proxyBean = beanFactory.getBean(Class.forName(key.getInstanceName()));
            Object bean;
            if (proxyBean instanceof Advised) {
                bean = ((Advised) proxyBean).getTargetSource().getTarget();
            } else {
                bean = proxyBean;
            }
            if (bean == null) {
                logger.warn("Bean for cache could not be resolved!");
                return null;
            }
            Class[] methodClazzes = Arrays.stream(key.getParameterClazzNames())
                    .map(clazzName -> {
                        try {
                            return Class.forName(clazzName);
                        } catch (ClassNotFoundException e) {
                            throw new IllegalStateException("Could not find Class '" + clazzName + "' for parameter!", e);
                        }
                    }).toArray(Class[]::new);
            Method method = bean.getClass().getMethod(key.getMethodName(), methodClazzes);
            Object cacheValue = method.invoke(bean, key.getParameters());
            logger.info("Finished re-population for parameters '{}' with value '{}'", key.getParameters(), cacheValue);
            return cacheValue;
        } catch (Exception e) {
            logger.error("I FAILED TO RELOAD AHEAD " + objectKey);
            throw new IllegalStateException("ReloadError for key " + objectKey, e);
        }
    }
}
