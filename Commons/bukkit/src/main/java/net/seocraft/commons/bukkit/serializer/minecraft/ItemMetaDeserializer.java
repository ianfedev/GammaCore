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
    public ItemMeta deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        JsonNode node;
        ItemMeta meta = new ItemStack(Material.GRASS, 1).getItemMeta();
        try {
            node = jsonParser.getCodec().readTree(jsonParser);
            ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

            meta.setDisplayName(node.get("display").toString());
            List<String> loreList = new ArrayList<>();

            @Nullable JsonNode loreArrayNode = node.get("lore");
            System.out.println(loreArrayNode);
            if (node.get("lore").isArray()) {
                for (JsonNode loreNode : node.get("lore")) {
                    System.out.println(loreList.toString());
                    loreList.add(loreNode.toString());
                }
            }

            meta.setLore(loreList);
            if (node.get("enchantments").isArray()) {
                System.out.println("Is array");
                for (JsonNode enchantmentNode : node.get("enchantments")) {
                    System.out.println("Array node");
                    System.out.println(enchantmentNode.toString());
                    meta.addEnchant(
                            mapper.readValue(enchantmentNode.toString(), Enchantment.class),
                            node.get("level").asInt(),
                            true
                    );
                }
            }

            if (node.get("flag").isArray()) {
                for (JsonNode flagNode: node.get("flag")) {
                    System.out.println(flagNode);
                    meta.addItemFlags(mapper.readValue(flagNode.toString(), ItemFlag.class));
                }
            }
            System.out.println("From deserializer" + meta.getDisplayName());
            return meta;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return meta;
    }

}
