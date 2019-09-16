package net.seocraft.commons.bungee.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.seocraft.api.core.user.UserExpulsion;
import net.seocraft.commons.core.serializer.CoreResolver;
import net.seocraft.commons.core.user.PlayerExpulsion;

public class InterfaceDeserializer {

    public static SimpleModule getAbstractTypes() {
        SimpleModule module = new SimpleModule("InterfaceDeserializerModule", Version.unknownVersion());
        SimpleAbstractTypeResolver resolver = CoreResolver.getCoreResolver();
        module.setAbstractTypes(resolver);
        return module;
    }

}
