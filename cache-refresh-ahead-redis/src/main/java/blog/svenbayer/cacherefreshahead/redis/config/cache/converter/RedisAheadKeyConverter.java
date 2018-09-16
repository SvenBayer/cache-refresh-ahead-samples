package blog.svenbayer.cacherefreshahead.redis.config.cache.converter;

import blog.svenbayer.cacherefreshahead.redis.config.cache.ReloadAheadKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

@Service
public class RedisAheadKeyConverter implements GenericConverter {

    private static final Set<ConvertiblePair> CONVERSION_TYPES = new HashSet<>();

    static {
        CONVERSION_TYPES.add(new ConvertiblePair(byte[].class, ReloadAheadKey.class));
        CONVERSION_TYPES.add(new ConvertiblePair(String.class, ReloadAheadKey.class));
        CONVERSION_TYPES.add(new ConvertiblePair(ReloadAheadKey.class, String.class));
        CONVERSION_TYPES.add(new ConvertiblePair(ReloadAheadKey.class, byte[].class));
    }

    @Autowired
    private ObjectMapper mapper;

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return CONVERSION_TYPES;
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

    @Nullable
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
                throw new IllegalStateException("Source '" +  source + "', targetType '" + targetType + "'");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getCacheNameFor(byte[] key) {
        String keyAsString = new String(key);
        return keyAsString.substring(0, keyAsString.indexOf("::"));
    }

    private <T> T convertStringToTargetType(String sourceAsString, Class<T> targetType) throws IOException {
        if (!sourceAsString.startsWith("{")) {
            sourceAsString = sourceAsString.substring(sourceAsString.indexOf('{'));
        }
        try {
            return mapper.readValue(sourceAsString, targetType);
        } catch (Exception e) {
            throw new IllegalStateException("Something went wrong for targetType '" + targetType + "' and sourceAsString '" + sourceAsString + "'", e);
        }
    }
}
