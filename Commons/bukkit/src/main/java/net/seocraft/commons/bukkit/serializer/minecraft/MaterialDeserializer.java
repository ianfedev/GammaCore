package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;

import java.io.IOException;

public class MaterialDeserializer extends StdDeserializer<Material> {

    public MaterialDeserializer() {
        this(null);
    }

    public MaterialDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Material deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            return Material.valueOf(node.asText().toUpperCase());
        } catch (IllegalArgumentException ignore) {
            return Material.AIR;
        }
    }
}
