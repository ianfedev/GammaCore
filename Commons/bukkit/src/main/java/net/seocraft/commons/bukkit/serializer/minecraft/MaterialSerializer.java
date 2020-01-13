package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.Material;

import java.io.IOException;

public class MaterialSerializer extends StdSerializer<Material> {

    public MaterialSerializer() {
        this(null);
    }

    public MaterialSerializer(Class<Material> t) {
        super(t);
    }

    @Override
    public void serialize(Material material, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(material.toString().toLowerCase());
    }
}
