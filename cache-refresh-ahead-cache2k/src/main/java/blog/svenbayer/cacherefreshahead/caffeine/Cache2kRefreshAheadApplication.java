package blog.svenbayer.cacherefreshahead.caffeine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Cache2kRefreshAheadApplication {

	public static void main(String[] args) {
		SpringApplication.run(Cache2kRefreshAheadApplication.class, args);
	}
}