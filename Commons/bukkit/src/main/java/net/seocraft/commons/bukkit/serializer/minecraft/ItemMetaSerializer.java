package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;

public class ItemMetaSerializer extends StdSerializer<ItemMeta> {

    public ItemMetaSerializer() {
        this(null);
    }

    private ItemMetaSerializer(Class<ItemMeta> t) {
        super(t);
    }

    @Override
    public void serialize(ItemMeta meta, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("display", meta.getDisplayName());
        if (meta.getLore() != null) {
            jsonGenerator.writeArrayFieldStart("lore");
            for (String loreLine : meta.getLore()) jsonGenerator.writeString(loreLine);
            jsonGenerator.writeEndArray();
        }
        if (meta.getItemFlags() != null) {
            jsonGenerator.writeArrayFieldStart("flag");
            for (ItemFlag flag : meta.getItemFlags()) jsonGenerator.writeObject(flag);
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();
    }
}