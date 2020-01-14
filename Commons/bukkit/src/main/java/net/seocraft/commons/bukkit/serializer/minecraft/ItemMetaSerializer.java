package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;

public class ItemMetaSerializer extends StdSerializer<ItemMeta> {

    public ItemMetaSerializer() {
        this(null);
    }

    public ItemMetaSerializer(Class<ItemMeta> t) {
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
        jsonGenerator.writeEndObject();
    }
}