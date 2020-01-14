package net.seocraft.commons.bukkit.serializer.minecraft;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;

public class EnchantmentSerializer extends StdSerializer<Enchantment> {

    public EnchantmentSerializer() {
        this(null);
    }

    private EnchantmentSerializer(Class<Enchantment> t) {
        super(t);
    }

    @Override
    public void serialize(Enchantment enchantment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(enchantment.getId());
    }
    
}
