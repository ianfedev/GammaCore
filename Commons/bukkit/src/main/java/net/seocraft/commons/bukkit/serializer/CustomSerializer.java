package net.seocraft.commons.bukkit.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.seocraft.commons.bukkit.serializer.minecraft.MaterialDeserializer;
import net.seocraft.commons.bukkit.serializer.minecraft.MaterialSerializer;
import org.bukkit.Material;

public class CustomSerializer {

    public static SimpleModule getCustomSerializerModule() {
        SimpleModule module = new SimpleModule("CustomSerializerModule", Version.unknownVersion());
        module.addSerializer(Material.class, new MaterialSerializer());
        module.addDeserializer(Material.class, new MaterialDeserializer());
        return module;
    }
}
