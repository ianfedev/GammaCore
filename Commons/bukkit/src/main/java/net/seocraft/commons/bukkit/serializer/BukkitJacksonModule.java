package net.seocraft.commons.bukkit.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.seocraft.commons.bukkit.serializer.minecraft.BukkitJacksonDeserializer;
import net.seocraft.commons.bukkit.serializer.minecraft.BukkitJacksonSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

class BukkitJacksonModule extends SimpleModule {
    BukkitJacksonModule() {
        addSerializer(new BukkitJacksonSerializer<>(ItemStack.class, null))
                .addDeserializer(ItemStack.class, new BukkitJacksonDeserializer<>(ItemStack.class));
        addKeyDeserializer(Enchantment.class, new KeyDeserializer() {
            @Override
            public Object deserializeKey(String s, DeserializationContext deserializationContext) {
                return Enchantment.getByName(s.toUpperCase());
            }
        });
        addKeySerializer(Enchantment.class, new JsonSerializer<Enchantment>() {
            @Override
            public void serialize(Enchantment enchantment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(enchantment.getName());
            }
        });
    }
}
