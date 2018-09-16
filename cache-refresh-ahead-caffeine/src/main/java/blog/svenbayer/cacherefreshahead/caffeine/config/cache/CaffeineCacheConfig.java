package blog.svenbayer.cacherefreshahead.caffeine.config.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.CachedExpressionEvaluator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

@EnableCaching
@Configuration
public class CaffeineCacheConfig extends CachingConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineCacheConfig.class);

    @Autowired
    private BeanFactory beanFactory;

    @Bean
    public CacheManager reloadAheadCaffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        CacheLoader<Object, Object> loader = this::reloadAheadMethod;
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .weigher((key, value) -> value.toString().length())
                .maximumWeight(50L)
                .expireAfterWrite(Duration.ofSeconds(8L))
                .refreshAfterWrite(Duration.ofSeconds(4L))
                .recordStats();
        cacheManager.setCaffeine(caffeine);
        cacheManager.setCacheLoader(loader);
        new CachedExpressionEvaluator() {

        };
        return cacheManager;
    }

    private Object reloadAheadMethod(Object objectKey) {
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
                            throw new IllegalStateException(e);
                        }
                    }).toArray(Class[]::new);
            Method method = bean.getClass().getMethod(key.getMethodName(), methodClazzes);
            Object cacheValue = method.invoke(bean, key.getParameters());
            logger.info("Finished re-population for parameters '{}' with value '{}'", key.getParameters(), cacheValue);
            return cacheValue;
        } catch (Exception e) {
            logger.error("I FAILED TO RELOAD AHEAD!!!");
            throw new IllegalStateException(e);
        }
    }
}