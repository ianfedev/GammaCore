package net.seocraft.api.shared.serialization.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ModelSerializationHandlerImp implements ModelSerializationHandler {



    @Override
    @SuppressWarnings("unchecked")
    public <T> String serializeModel(T object, Class rawClass) {
        Gson gson;
        if (rawClass.isInterface()) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(rawClass, new ModelSerializer<T>(rawClass))
                    .enableComplexMapKeySerialization()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .create();
        } else {
            gson = new Gson();
        }
        return gson.toJson(object, rawClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserializeModel(String object, Class rawClass) {
        Gson gson;
        if (rawClass.isInterface()) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(rawClass, new ModelDeserializer<T>(rawClass))
                    .enableComplexMapKeySerialization()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .create();
        } else {
            gson = new Gson();
        }
        return (T) gson.fromJson(object, rawClass);
    }
}
