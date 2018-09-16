package blog.svenbayer.cacherefreshahead.redis.config.cache.converter;

import blog.svenbayer.cacherefreshahead.redis.config.cache.RedisAheadKeyConversionService;
import blog.svenbayer.cacherefreshahead.redis.config.cache.ReloadAheadKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class RedisRefreshAheadSerializer implements RedisSerializer<ReloadAheadKey> {

    private static final Logger logger = LoggerFactory.getLogger(RedisRefreshAheadSerializer.class);

    private RedisAheadKeyConversionService conversionService;

    @Autowired
    public RedisRefreshAheadSerializer(RedisAheadKeyConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public byte[] serialize(ReloadAheadKey refreshAheadKey) {
        try {
            return conversionService.convert(refreshAheadKey, byte[].class);
        } catch (Exception e) {
            logger.error("Could not serialize refreshAheadKey to byte[] : " + refreshAheadKey, e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ReloadAheadKey deserialize(byte[] bytes) {
        try {
            return conversionService.convert(bytes, ReloadAheadKey.class);
        } catch (Exception e) {
            logger.error("Could not deserialize byte[] to ReloadAheadKey : " + new String(bytes), e);
            throw new IllegalStateException(e);
        }
    }
}
