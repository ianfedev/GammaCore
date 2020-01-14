package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Map;

public class ItemStackSerializer extends StdSerializer<ItemStack> {

    public ItemStackSerializer() {
        this(null);
    }

    private ItemStackSerializer(Class<ItemStack> t) {
        super(t);
    }

    @Override
    public void serialize(ItemStack stack, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("material", stack.getType());
        jsonGenerator.writeNumberField("durability", stack.getDurability());
        jsonGenerator.writeNumberField("materialData", stack.getData().getData());
        if (stack.getEnchantments() != null) {
            jsonGenerator.writeArrayFieldStart("enchantments");
            for (Map.Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("name", entry.getKey());
                jsonGenerator.writeNumberField("level", entry.getValue());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeNumberField("amount", stack.getAmount());
        jsonGenerator.writeObjectField("itemMeta", stack.getItemMeta());
        jsonGenerator.writeEndObject();
    }
}