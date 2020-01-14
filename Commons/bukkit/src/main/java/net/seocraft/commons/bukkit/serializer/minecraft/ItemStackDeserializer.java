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
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        ItemStack stack = new ItemStack(
                mapper.readValue(node.get("material").toString(), Material.class),
                node.get("amount").asInt(),
                (short) node.get("materialData").asInt()
        );
        stack.setItemMeta(mapper.readValue(node.get("itemMeta").toString(), ItemMeta.class));
        stack.setDurability((short) node.get("durability").asInt());
        return stack;
    }
}
