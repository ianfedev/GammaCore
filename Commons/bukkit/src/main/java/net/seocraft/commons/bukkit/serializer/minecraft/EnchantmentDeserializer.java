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
        System.out.println(node.toString().toUpperCase());
        switch (node.toString().toUpperCase()) {
            case "PROTECTION_ENVIRONMENTAL":
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            case "PROTECTION_FIRE":
                return Enchantment.PROTECTION_FIRE;
            case "PROTECTION_FALL":
                return Enchantment.PROTECTION_FALL;
            case "PROTECTION_EXPLOSIONS":
                return Enchantment.PROTECTION_EXPLOSIONS;
            case "PROTECTION_PROJECTILE":
                return Enchantment.PROTECTION_PROJECTILE;
            case "OXYGEN":
                return Enchantment.OXYGEN;
            case "WATER_WORKER":
                return Enchantment.WATER_WORKER;
            case "THORNS" :
                return Enchantment.THORNS;
            case "DEPTH_STRIDER":
                return Enchantment.DEPTH_STRIDER;
            case "DAMAGE_ALL":
                return Enchantment.DAMAGE_ALL;
            case "DAMAGE_UNDEAD":
                return Enchantment.DAMAGE_UNDEAD;
            case "DAMAGE_ARTHROPODS":
                return Enchantment.DAMAGE_ARTHROPODS;
            case "KNOCKBACK":
                return Enchantment.KNOCKBACK;
            case "FIRE_ASPECT":
                return Enchantment.FIRE_ASPECT;
            case "LOOT_BONUS_MOBS":
                return Enchantment.LOOT_BONUS_MOBS;
            case "DIG_SPEED":
                return Enchantment.DIG_SPEED;
            case "SILK_TOUCH":
                return Enchantment.SILK_TOUCH;
            case "DURABILITY":
                return Enchantment.DURABILITY;
            case "LOOT_BONUS_BLOCKS":
                return Enchantment.LOOT_BONUS_BLOCKS;
            case "ARROW_DAMAGE":
                return Enchantment.ARROW_DAMAGE;
            case "ARROW_KNOCKBACK":
                return Enchantment.ARROW_KNOCKBACK;
            case "ARROW_FIRE":
                return Enchantment.ARROW_FIRE;
            case "ARROW_INFINITE":
                return Enchantment.ARROW_INFINITE;
            case "LUCK":
                return Enchantment.LUCK;
            case "LURE":
                return Enchantment.LURE;
            default:
                return Enchantment.getByName(node.toString().toUpperCase());
        }
    }
}
