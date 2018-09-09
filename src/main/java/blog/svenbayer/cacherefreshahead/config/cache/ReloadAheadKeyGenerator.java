package blog.svenbayer.cacherefreshahead.config.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("reloadAheadKeyGenerator")
public class ReloadAheadKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object o, Method method, Object... objects) {
        return new ReloadAheadKey(o, method, objects);
    }
}
