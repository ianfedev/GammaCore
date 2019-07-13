package net.seocraft.api.shared.serialization.model;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ModelRegistrar {
    public static <V> GsonBuilder registerModelSerializer(Class<V> model, GsonBuilder builder) {
        return registerModelSerializer(TypeToken.get(model), builder);
    }

    public static <V> GsonBuilder registerModelSerializer(TypeToken<V> type, GsonBuilder builder) {
        return builder.registerTypeAdapter(type.getType(), new ModelSerializer<>(type))
                .registerTypeAdapter(type.getType(), new ModelDeserializer<>(type));
    }
}
