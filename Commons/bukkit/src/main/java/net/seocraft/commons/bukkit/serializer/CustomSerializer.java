package net.seocraft.commons.bukkit.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.seocraft.commons.bukkit.serializer.minecraft.ItemMetaSerializer;
import net.seocraft.commons.bukkit.serializer.minecraft.ItemStackSerializer;
import net.seocraft.commons.bukkit.serializer.minecraft.MaterialDeserializer;
import net.seocraft.commons.bukkit.serializer.minecraft.MaterialSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomSerializer {

    public static SimpleModule getCustomSerializerModule() {
        SimpleModule module = new SimpleModule("CustomSerializerModule", Version.unknownVersion());
        module.addSerializer(Material.class, new MaterialSerializer());
        module.addDeserializer(Material.class, new MaterialDeserializer());
        module.addSerializer(ItemStack.class, new ItemStackSerializer());
        module.addSerializer(ItemMeta.class, new ItemMetaSerializer());
        return module;
    }
}
