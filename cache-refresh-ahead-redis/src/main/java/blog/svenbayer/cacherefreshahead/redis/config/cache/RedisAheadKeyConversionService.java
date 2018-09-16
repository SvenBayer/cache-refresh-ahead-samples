package blog.svenbayer.cacherefreshahead.redis.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

@Service
public class RedisAheadKeyConversionService implements ConversionService {

    private static final Map<Class, List<Class>> CONVERSION_TYPES = new HashMap<>();

    static {
        CONVERSION_TYPES.put(byte[].class, Collections.singletonList(ReloadAheadKey.class));
        CONVERSION_TYPES.put(String.class, Collections.singletonList(ReloadAheadKey.class));
        CONVERSION_TYPES.put(ReloadAheadKey.class, Arrays.asList(String.class, byte[].class));
    }

    @Autowired
    private ObjectMapper mapper;

    @Nullable
    @Override
    public boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        return CONVERSION_TYPES.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(sourceType) && entry.getValue().stream()
                        .anyMatch(value -> value.equals(targetType)));
    }

    @Nullable
    @Override
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        Class<?> sourceObjectType;
        if (sourceType == null) {
            sourceObjectType = null;
        } else {
            sourceObjectType = sourceType.getObjectType();
        }
        return canConvert(sourceObjectType, targetType.getObjectType());
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object source, Class<T> targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        try {
            if (source instanceof ReloadAheadKey && targetType.equals(String.class)) {
                ReloadAheadKey reloadAheadKey = (ReloadAheadKey) source;
                return (T) mapper.writeValueAsString(reloadAheadKey);
            } else if (source instanceof ReloadAheadKey && targetType.equals(byte[].class)) {
                ReloadAheadKey reloadAheadKey = (ReloadAheadKey) source;
                return (T) mapper.writeValueAsBytes(reloadAheadKey);
            } else if (source instanceof byte[] && targetType.equals(ReloadAheadKey.class)) {
                String sourceAsString = new String((byte[]) source);
                return convertStringToTargetType(sourceAsString, targetType);
            } else if (source instanceof String && targetType.equals(ReloadAheadKey.class)) {
                String sourceAsString = (String) source;
                return convertStringToTargetType(sourceAsString, targetType);
            } else {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nullable
    @Override
    public Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        if (sourceType == null) {
            Assert.isTrue(source == null, "Source must be [null] if source type == [null]");
        } else if (source != null && !sourceType.getObjectType().isInstance(source)) {
            throw new IllegalArgumentException("Source to convert from must be an instance of [" + sourceType + "]; instead it was a [" + source.getClass().getName() + "]");
        }
        return convert(source, targetType.getObjectType());
    }

    private <T> T convertStringToTargetType(String sourceAsString, Class<T> targetType) throws IOException {
        if (sourceAsString.contains("::")) {
            sourceAsString = sourceAsString.substring(sourceAsString.indexOf("::") + 2);
        }
        return mapper.readValue(sourceAsString, targetType);
    }
}
