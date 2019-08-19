package net.seocraft.api.core.redis;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;

public class RedissonJacksonCodec extends BaseCodec {

    private ObjectMapper mapObjectMapper;
    private final Encoder encoder;
    private final Decoder<Object> decoder;

    public RedissonJacksonCodec(ObjectMapper mapper) {
        this(mapper, true);
    }

    public RedissonJacksonCodec(ClassLoader classLoader, ObjectMapper mapper) {
        this(createObjectMapper(classLoader, mapper), true);
    }

    public RedissonJacksonCodec(ClassLoader classLoader, RedissonJacksonCodec codec) {
        this(createObjectMapper(classLoader, codec.mapObjectMapper.copy()), true);
    }

    protected static ObjectMapper createObjectMapper(ClassLoader classLoader, ObjectMapper om) {
        TypeFactory tf = TypeFactory.defaultInstance().withClassLoader(classLoader);
        om.setTypeFactory(tf);
        return om;
    }

    public RedissonJacksonCodec(ObjectMapper mapObjectMapper, boolean setMapperVisibility) {
        this.encoder = in -> {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try {
                ByteBufOutputStream os = new ByteBufOutputStream(out);
                mapObjectMapper.writeValue((OutputStream) os, in);
                return os.buffer();
            } catch (IOException e) {
                out.release();
                throw e;
            } catch (Exception e) {
                out.release();
                throw new IOException(e);
            }
        };
        this.decoder = (buf, state) -> mapObjectMapper.readValue((InputStream) new ByteBufInputStream(buf), Object.class);
        this.mapObjectMapper = mapObjectMapper.copy();
        this.init(this.mapObjectMapper, setMapperVisibility);
        this.initTypeInclusion(this.mapObjectMapper);
    }

    protected void initTypeInclusion(ObjectMapper mapObjectMapper) {
        TypeResolverBuilder<?> mapTyper = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL) {
            public boolean useForType(JavaType t) {
                switch(this._appliesFor) {
                    case NON_CONCRETE_AND_ARRAYS:
                        while(t.isArrayType()) {
                            t = t.getContentType();
                        }
                    case OBJECT_AND_NON_CONCRETE:
                        return (t.getRawClass() == Object.class) || !t.isConcrete();
                    case NON_FINAL:
                        while(t.isArrayType()) {
                            t = t.getContentType();
                        }

                        if (t.getRawClass() == Long.class) {
                            return true;
                        }

                        if (t.getRawClass() == XMLGregorianCalendar.class) {
                            return false;
                        }

                        return !t.isFinal();
                    default:
                        return t.getRawClass() == Object.class;
                }
            }
        };

        mapTyper.init(JsonTypeInfo.Id.CLASS, (TypeIdResolver)null);
        mapTyper.inclusion(JsonTypeInfo.As.PROPERTY);
        mapObjectMapper.setDefaultTyping(mapTyper);

        try {
            byte[] s = mapObjectMapper.writeValueAsBytes(1);
            mapObjectMapper.readValue(s, Object.class);
        } catch (IOException var4) {
            throw new IllegalStateException(var4);
        }
    }

    protected void init(ObjectMapper objectMapper, boolean setVisibilityConfigs) {

        if (setVisibilityConfigs) {
            objectMapper.setVisibility(objectMapper.getSerializationConfig()
                    .getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
                    .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        }

        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        objectMapper.addMixIn(Throwable.class, RedissonJacksonCodec.ThrowableMixIn.class);
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return this.decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return this.encoder;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.mapObjectMapper.getTypeFactory().getClassLoader() != null ? this.mapObjectMapper.getTypeFactory().getClassLoader() : super.getClassLoader();
    }

    public ObjectMapper getObjectMapper() {
        return this.mapObjectMapper;
    }

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.IntSequenceGenerator.class,
            property = "@getId"
    )
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
            setterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class ThrowableMixIn {
        public ThrowableMixIn() {
        }
    }
}
