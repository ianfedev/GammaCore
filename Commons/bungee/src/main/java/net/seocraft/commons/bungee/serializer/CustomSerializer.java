package net.seocraft.commons.bungee.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.seocraft.commons.core.serializer.date.DateDeserializer;
import net.seocraft.commons.core.serializer.date.DateSerializer;

import java.util.Date;

public class CustomSerializer {

    public static SimpleModule getCustomSerializerModule() {
        SimpleModule module = new SimpleModule("CustomSerializerModule", Version.unknownVersion());
        module.addSerializer(Date.class, new DateSerializer());
        module.addDeserializer(Date.class, new DateDeserializer());

        return module;
    }

}
