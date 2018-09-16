package blog.svenbayer.cacherefreshahead.redis.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SampleRedisService {

    @Cacheable(value = "longrun", cacheManager = "reloadAheadRedisCacheManager", keyGenerator = "reloadAheadKeyGenerator")
    public String longRunningSimulation(String value) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2L);
        return "Hello " + value;
    }
}
