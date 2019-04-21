package net.seocraft.api.shared.serialization.models;


public interface Deserializer<T> {

    T deserializeModel(String json);
}
