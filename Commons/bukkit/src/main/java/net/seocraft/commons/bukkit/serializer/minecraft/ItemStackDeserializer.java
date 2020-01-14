package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

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
        ItemStack stack;

        @Nullable JsonNode material = node.get("material");
        @Nullable JsonNode amount = node.get("amount");
        if (material != null && amount != null) {
            @Nullable JsonNode data = node.get("materialData");
            if (data != null) {
                stack = new ItemStack(
                        mapper.readValue(node.get("material").toString(), Material.class),
                        amount.asInt(),
                        (short) data.asInt()
                );
            } else {
                stack = new ItemStack(
                        mapper.readValue(node.get("material").toString(), Material.class),
                        amount.asInt()
                );
            }
        } else {
            throw new IOException("There was no material specified at serialization.");
        }

        @Nullable JsonNode durability = node.get("durability");
        if (durability != null)
            stack.setDurability((short) durability.asInt());

        @Nullable JsonNode enchantmentArrayNode = node.get("enchantments");
        if (enchantmentArrayNode != null && enchantmentArrayNode.isArray()) {
            for (JsonNode enchantmentNode : enchantmentArrayNode) {
                int level = 1;
                @Nullable Enchantment enchantment = mapper.readValue(
                        enchantmentNode.get("name").toString(),
                        Enchantment.class
                );
                @Nullable JsonNode levelNode = enchantmentNode.get("level");
                if (levelNode != null && levelNode.isInt()) level = levelNode.asInt();
                if (enchantment != null) {
                    stack.addUnsafeEnchantment(
                            enchantment,
                            level
                    );
                }
            }
        }

        System.out.println("Success");
        JsonNode testNode = node.get("itemMeta");
        ItemMeta meta = mapper.readValue(testNode.toString(), ItemMeta.class);
        return stack;
    }
}
