package blog.svenbayer.cacherefreshahead.caffeine.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SampleCaffeineService {

    @Cacheable(value = "longrun", cacheManager = "reloadAheadCaffeineCacheManager", keyGenerator = "reloadAheadKeyGenerator")
    public String longRunningSimulation(String value) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2L);
        return "Hello " + value;
    }
}
