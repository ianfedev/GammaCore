package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;

public class ItemStackDeserializer extends StdDeserializer<ItemStack> {

    public ItemStackDeserializer() {
        this(null);
    }

    private ItemStackDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ItemStack deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        System.out.println("Starting deserialization");
        JsonNode materialNode = node.get("material");
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        System.out.println(
            mapper.readValue(
                    materialNode.asText(),
                    Material.class
            )
        );
        ItemStack stack = new ItemStack(
                deserializationContext.readValue(
                        node.get("material").traverse(),
                        Material.class
                ),
                node.get("amount").asInt(),
                (short) node.get("materialData").asInt()
        );
        stack.setItemMeta(((ObjectMapper) jsonParser.getCodec()).readValue(node.get("itemMeta").asText(), ItemMeta.class));
        stack.setDurability((short) node.get("durability").asInt());
        return stack;
    }
}
