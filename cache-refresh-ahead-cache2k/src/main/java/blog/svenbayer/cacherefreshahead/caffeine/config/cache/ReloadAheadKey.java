package blog.svenbayer.cacherefreshahead.caffeine.config.cache;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class ReloadAheadKey {

    private Object instance;
    private Method method;
    private Object[] parameters;

    public ReloadAheadKey(Object instance, Method method, Object[] parameters) {
        this.instance = instance;
        this.method = method;
        this.parameters = parameters;
    }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReloadAheadKey)) return false;
        ReloadAheadKey that = (ReloadAheadKey) o;
        return Objects.equals(instance, that.instance) &&
                Objects.equals(method, that.method) &&
                Arrays.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(instance, method);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }
}
