package blog.svenbayer.cacherefreshahead.redis.config.cache.refreshahead;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheReloadAheadService {

    private RedisConnectionFactory redisConnectionFactory;
    private CacheManager redisCacheManager;
    private ReloadAheadService reloadAheadService;

    @Autowired
    public RedisCacheReloadAheadService(RedisConnectionFactory redisConnectionFactory, CacheManager redisCacheManager, ReloadAheadService reloadAheadService, RedisCacheConfiguration redisCacheConfiguration) {
        this.redisConnectionFactory = redisConnectionFactory;
        if (!(redisCacheManager instanceof RedisCacheManager)) {
            throw new AssertionError();
        }
        this.redisCacheManager = redisCacheManager;
        this.reloadAheadService = reloadAheadService;
    }

    @EventListener
    public void refreshCaches(ContextRefreshedEvent event) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::reloadAheadValuesForKeys,0L, 4L, TimeUnit.SECONDS);
    }

    private void reloadAheadValuesForKeys() {
        RedisConnection connection = redisConnectionFactory.getConnection();

        Collection<String> cacheNames = redisCacheManager.getCacheNames();
        cacheNames.stream()
                .map(cacheName -> redisCacheManager.getCache(cacheName))
                .filter(Objects::nonNull)
                .forEach(cache -> {
                    String cacheName = cache.getName();
                    Cursor<byte[]> scan = connection.keyCommands().scan(ScanOptions.scanOptions().match(cacheName + "*").build());
                    scan.forEachRemaining(key -> {
                        ReloadAheadKey convertedKey = transformReloadAheadKey(key, cacheName);
                        Object updatedValue = reloadAheadService.reloadAheadMethod(convertedKey);
                        cache.put(convertedKey, updatedValue);
                });
        });
    }

    private ReloadAheadKey transformReloadAheadKey(byte[] key, String cacheName) {
        byte[] prefixBytes = (cacheName + "::").getBytes();
        byte[] keyWithoutPrefix = Arrays.copyOfRange(key, prefixBytes.length, key.length);
        byte[] decodedKey = Base64.getDecoder().decode(keyWithoutPrefix);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decodedKey))) {
            return (ReloadAheadKey) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
