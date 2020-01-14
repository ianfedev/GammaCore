package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ItemStackSerializer extends StdSerializer<ItemStack> {

    public ItemStackSerializer() {
        this(null);
    }

    public ItemStackSerializer(Class<ItemStack> t) {
        super(t);
    }

    @Override
    public void serialize(ItemStack stack, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("material", stack.getType());
        jsonGenerator.writeNumberField("materialData", stack.getData().getData());
        jsonGenerator.writeNumberField("amount", stack.getAmount());
        jsonGenerator.writeObjectField("itemMeta", stack.getItemMeta());
        jsonGenerator.writeEndObject();
    }
}