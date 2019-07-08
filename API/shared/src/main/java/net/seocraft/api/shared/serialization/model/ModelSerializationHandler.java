package net.seocraft.api.shared.serialization.model;

import java.lang.reflect.Type;

public interface ModelSerializationHandler {

    <T> String serializeModel(T object, Class rawClass);

    <T> T deserializeModel(String object, Class rawClass);

    <T> T deserializeModel(String object, Type rawClass);

}
