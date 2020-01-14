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
        stack.setDurability((short) node.get("durability").asInt());

        JsonNode testNode = node.get("itemMeta");
        System.out.println(testNode.toString());
        JsonParser parser = testNode.traverse(jsonParser.getCodec());
        ItemMeta meta = parser.readValueAs(ItemMeta.class);
        System.out.println(meta.getDisplayName());
        return stack;
    }
}
