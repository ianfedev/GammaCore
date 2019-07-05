package net.seocraft.api.shared.serialization.model;

public interface ModelSerializationHandler {

    <T> String serializeModel(T object, Class rawClass);

    <T> T deserializeModel(String object, Class rawClass);

}
