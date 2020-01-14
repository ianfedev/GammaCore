package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemMetaDeserializer extends StdDeserializer<ItemMeta> {

    public ItemMetaDeserializer() {
        this(null);
    }

    private ItemMetaDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ItemMeta deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ItemMeta meta = new ItemStack(Material.GRASS, 1).getItemMeta();
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

        meta.setDisplayName(node.get("display").toString());
        List<String> loreList = new ArrayList<>();

        @Nullable JsonNode loreArrayNode = node.get("lore");
        if (loreArrayNode != null && loreArrayNode.isArray()) {
            for (JsonNode loreNode : node.get("lore")) {
                loreList.add(loreNode.toString());
            }
        }
        meta.setLore(loreList);


        @Nullable JsonNode enchantmentArrayNode = node.get("enchantments");
        if (enchantmentArrayNode != null && enchantmentArrayNode.isArray()) {
            for (JsonNode enchantmentNode : node.get("enchantments")) {
                @Nullable Enchantment enchantment = mapper.readValue(
                        enchantmentNode.get("name").toString(),
                        Enchantment.class
                );
                if (enchantment != null) {
                    meta.addEnchant(
                            enchantment,
                            node.get("level").asInt(),
                            true
                    );
                }
            }
        }

        @Nullable JsonNode flagArrayNode = node.get("flag");
        if (flagArrayNode != null && flagArrayNode.isArray()) {
            for (JsonNode flagNode: node.get("flag")) {
                @Nullable ItemFlag flag = mapper.readValue(flagNode.toString(), ItemFlag.class);
                if (flag != null) meta.addItemFlags(flag);
            }
        }

        System.out.println("Starting enchantment parsing");
        meta.getEnchants().forEach((enchantment, integer) -> {
            System.out.println(enchantment.getName());
        });
        System.out.println("From deserializer" + meta.getDisplayName());
        return meta;
    }

}
