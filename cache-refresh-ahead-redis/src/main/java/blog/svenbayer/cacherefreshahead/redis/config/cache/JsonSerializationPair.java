package blog.svenbayer.cacherefreshahead.redis.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisElementReader;
import org.springframework.data.redis.serializer.RedisElementWriter;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class JsonSerializationPair implements RedisSerializationContext.SerializationPair {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public RedisElementReader getReader() {
        return byteBuffer -> {
            try {
                return mapper.readValue(byteBuffer.array(), ReloadAheadKey.class);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    @Override
    public RedisElementWriter getWriter() {
        return reloadAheadKey -> {
            String keyAsString = (String) reloadAheadKey;
            return ByteBuffer.wrap(keyAsString.getBytes());
        };
    }
}