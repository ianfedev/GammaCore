package net.seocraft.commons.bukkit.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.seocraft.commons.bukkit.serializer.minecraft.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomSerializer {

    public static SimpleModule getCustomSerializerModule() {
        /*SimpleModule module = new SimpleModule("CustomSerializerModule", Version.unknownVersion());*/
        /*
        module.addSerializer(Material.class, new MaterialSerializer());
        module.addDeserializer(Material.class, new MaterialDeserializer());
        module.addSerializer(Enchantment.class, new EnchantmentSerializer());
        module.addDeserializer(Enchantment.class, new EnchantmentDeserializer());
        module.addSerializer(ItemMeta.class, new ItemMetaSerializer());
        module.addDeserializer(ItemMeta.class, new ItemMetaDeserializer());
        module.addSerializer(ItemStack.class, new ItemStackSerializer());
        module.addDeserializer(ItemStack.class, new ItemStackDeserializer());
         */

        return new BukkitJacksonModule();
    }
}
