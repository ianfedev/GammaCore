package net.seocraft.api.core.redis;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

public class RedissonJacksonCodec extends BaseCodec {

    public static final RedissonJacksonCodec INSTANCE = new RedissonJacksonCodec();

    @JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
    @JsonAutoDetect(fieldVisibility = Visibility.ANY,
            getterVisibility = Visibility.PUBLIC_ONLY,
            setterVisibility = Visibility.NONE,
            isGetterVisibility = Visibility.NONE)
    public static class ThrowableMixIn {

    }

    protected final ObjectMapper mapObjectMapper;

    private final Encoder encoder = new Encoder() {
        @Override
        public ByteBuf encode(Object in) throws IOException {
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
        }
    };

    private final Decoder<Object> decoder = new Decoder<Object>() {
        @Override
        public Object decode(ByteBuf buf, State state) throws IOException {
            return mapObjectMapper.readValue((InputStream) new ByteBufInputStream(buf), Object.class);
        }
    };

    public RedissonJacksonCodec() {
        this(new ObjectMapper(), true);
    }

    public RedissonJacksonCodec(ClassLoader classLoader) {
        this(createObjectMapper(classLoader, new ObjectMapper()),true);
    }

    public RedissonJacksonCodec(ClassLoader classLoader, RedissonJacksonCodec codec) {
        this(createObjectMapper(classLoader, codec.mapObjectMapper.copy()),true);
    }

    protected static ObjectMapper createObjectMapper(ClassLoader classLoader, ObjectMapper om) {
        TypeFactory tf = TypeFactory.defaultInstance().withClassLoader(classLoader);
        om.setTypeFactory(tf);
        return om;
    }

    public RedissonJacksonCodec(ObjectMapper mapObjectMapper, boolean changeConfig) {
        this.mapObjectMapper = mapObjectMapper.copy();
        if(changeConfig){
            init(this.mapObjectMapper);
        }

        mapObjectMapper.addMixIn(Throwable.class, ThrowableMixIn.class);
        initTypeInclusion(this.mapObjectMapper);
    }

    protected void initTypeInclusion(ObjectMapper mapObjectMapper) {
        TypeResolverBuilder<?> mapTyper = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL) {
            public boolean useForType(JavaType t) {
                switch (_appliesFor) {
                    case NON_CONCRETE_AND_ARRAYS:
                        while (t.isArrayType()) {
                            t = t.getContentType();
                        }
                        // fall through
                    case OBJECT_AND_NON_CONCRETE:
                        return (t.getRawClass() == Object.class) || !t.isConcrete();
                    case NON_FINAL:
                        while (t.isArrayType()) {
                            t = t.getContentType();
                        }
                        // to fix problem with wrong long to int conversion
                        if (t.getRawClass() == Long.class) {
                            return true;
                        }
                        if (t.getRawClass() == XMLGregorianCalendar.class) {
                            return false;
                        }
                        return !t.isFinal(); // includes Object.class
                    default:
                        // case JAVA_LANG_OBJECT:
                        return t.getRawClass() == Object.class;
                }
            }
        };
        mapTyper.init(JsonTypeInfo.Id.CLASS, null);
        mapTyper.inclusion(JsonTypeInfo.As.PROPERTY);
        mapObjectMapper.setDefaultTyping(mapTyper);

        // warm up codec
        try {
            byte[] s = mapObjectMapper.writeValueAsBytes(1);
            mapObjectMapper.readValue(s, Object.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void init(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setVisibility(objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mapObjectMapper.getTypeFactory().getClassLoader() != null) {
            return mapObjectMapper.getTypeFactory().getClassLoader();
        }

        return super.getClassLoader();
    }

    public ObjectMapper getObjectMapper() {
        return mapObjectMapper;
    }
}
