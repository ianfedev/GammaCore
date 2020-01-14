package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    public ItemMeta deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

        ItemMeta meta = new ItemStack(Material.GRASS, 1).getItemMeta();
        System.out.println(node.get("display").toString());
        meta.setDisplayName(node.get("display").toString());
        List<String> loreList = new ArrayList<>();
        if (node.get("lore").isArray()) {
            for (JsonNode loreNode : node.get("lore")) {
                loreList.add(loreNode.asText());
            }
        }

        meta.setLore(loreList);
        if (node.get("enchantments").isArray()) {
            for (JsonNode enchantmentNode : node.get("enchantments")) {
                meta.addEnchant(
                        mapper.readValue(enchantmentNode.toString(), Enchantment.class),
                        node.get("level").asInt(),
                        true
                );
            }
        }

        if (node.get("flag").isArray()) {
            for (JsonNode flagNode: node.get("flag")) {
                meta.addItemFlags(mapper.readValue(flagNode.toString(), ItemFlag.class));
            }
        }
        return meta;
    }

}
