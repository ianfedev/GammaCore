package net.seocraft.api.shared.serialization.model;


public interface Deserializer<T> {

    T deserializeModel(String json);
}
