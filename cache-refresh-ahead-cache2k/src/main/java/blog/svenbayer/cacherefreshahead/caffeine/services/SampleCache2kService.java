package blog.svenbayer.cacherefreshahead.caffeine.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SampleCache2kService {

    @Cacheable(value = "longrun", cacheManager = "reloadAheadCache2kManager", keyGenerator = "reloadAheadKeyGenerator")
    public String longRunningSimulation(String value) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3L);
        return "Hello " + value;
    }
}