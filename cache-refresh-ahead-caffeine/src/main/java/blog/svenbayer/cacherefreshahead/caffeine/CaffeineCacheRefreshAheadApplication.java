package blog.svenbayer.cacherefreshahead.caffeine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CaffeineCacheRefreshAheadApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaffeineCacheRefreshAheadApplication.class, args);
	}
}

