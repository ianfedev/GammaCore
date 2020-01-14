package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonParser;
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
    public Enchantment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String finalCodec = node.asText();
        for (Enchantment enchant : Enchantment.values()) {
            System.out.println(enchant.getName());
            System.out.println(finalCodec.toUpperCase());
            if (finalCodec.equalsIgnoreCase(enchant.getName())) return enchant;
        }
        return null;
    }
}
