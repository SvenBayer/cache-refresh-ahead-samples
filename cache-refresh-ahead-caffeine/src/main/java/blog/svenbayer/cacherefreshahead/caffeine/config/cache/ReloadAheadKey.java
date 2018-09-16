package blog.svenbayer.cacherefreshahead.caffeine.config.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Objects;

public class ReloadAheadKey {

    private String instanceName;
    private String methodName;
    private Object[] parameters;
    private String[] parameterClazzNames;

    ReloadAheadKey(String instanceName, String methodName, Object[] parameters, String[] parameterClazzNames) {
        this.instanceName = instanceName;
        this.methodName = methodName;
        this.parameters = parameters;
        this.parameterClazzNames = parameterClazzNames;
    }

    String getInstanceName() {
        return instanceName;
    }

    String getMethodName() {
        return methodName;
    }

    Object[] getParameters() {
        return parameters;
    }

    String[] getParameterClazzNames() {
        return parameterClazzNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReloadAheadKey)) return false;
        ReloadAheadKey that = (ReloadAheadKey) o;
        return Objects.equals(instanceName, that.instanceName) &&
                Objects.equals(methodName, that.methodName) &&
                Arrays.equals(parameters, that.parameters) &&
                Arrays.equals(parameterClazzNames, that.parameterClazzNames);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(instanceName, methodName);
        result = 31 * result + Arrays.hashCode(parameters);
        result = 31 * result + Arrays.hashCode(parameterClazzNames);
        return result;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
