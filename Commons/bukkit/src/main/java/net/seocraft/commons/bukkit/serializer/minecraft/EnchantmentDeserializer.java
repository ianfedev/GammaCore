package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;

public class EnchantmentDeserializer extends StdDeserializer<Enchantment> {

    public EnchantmentDeserializer() {
        this(null);
    }

    private EnchantmentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Enchantment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            return Enchantment.getByName(node.asText().toUpperCase());
        } catch (IllegalArgumentException ignore) {
            return null;
        }
    }
}
