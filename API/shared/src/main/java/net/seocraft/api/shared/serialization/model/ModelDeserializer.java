package net.seocraft.api.shared.serialization.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.seocraft.api.shared.model.Model;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelDeserializer<O extends Model> implements JsonDeserializer<O> {
    @NotNull
    private TypeToken<O> typeToken;

    public ModelDeserializer() {
        typeToken = new TypeToken<O>() {
        };
    }

    @Override
    public O deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Class<? super O> rawType = typeToken.getRawType();
        Method[] typeMethods = rawType.getDeclaredMethods();

        Map<String, Class<?>> typeElements = new HashMap<>();

        for (Method method : typeMethods) {
            if (method.getParameterCount() > 0) {
                continue;
            }

            String elementName = ModelSerializer.getElementName(method);
            Class<?> elementType = method.getReturnType();

            typeElements.put(elementName, elementType);
        }

        Map<String, Object> deserializedObjects = new HashMap<>();

        for (Map.Entry<String, Class<?>> entry : typeElements.entrySet()) {
            String elementName = entry.getKey();
            Class<?> elementType = entry.getValue();

            JsonElement element = object.get(elementName);

            deserializedObjects.put(elementName, jsonDeserializationContext.deserialize(element, elementType));
        }


        Class<?> classToCreate = rawType;

        if (rawType.isInterface() || Modifier.isAbstract(rawType.getModifiers())) {
            if (!rawType.isAnnotationPresent(ImplementedBy.class)) {
                throw new JsonParseException("The class " + rawType.getName() + " isn't a concrete class and it doesn't has a ImplementedBy annotation!");
            }

            ImplementedBy implementedBy = rawType.getAnnotation(ImplementedBy.class);

            classToCreate = implementedBy.value();
        }

        Constructor<?> constructorToUse = null;
        List<String> constructorProperties = null;

        for (Constructor<?> constructor : classToCreate.getDeclaredConstructors()) {
            if (!constructor.isAnnotationPresent(ConstructorProperties.class)) {
                continue;
            }

            ConstructorProperties propertiesAnnotation = constructor.getAnnotation(ConstructorProperties.class);
            List<String> properties = Arrays.asList(propertiesAnnotation.value());

            if (properties.size() != deserializedObjects.keySet().size() || !properties.containsAll(deserializedObjects.keySet())) {
                continue;
            }

            constructorToUse = constructor;
            constructorProperties = properties;
        }

        if (constructorToUse == null) {
            throw new JsonParseException("The class " + classToCreate.getName() + " doesn't has a constructor with ConstructorProperties annotation!");
        }

        List<Object> constructorObjects = constructorProperties.stream().map(deserializedObjects::get).collect(Collectors.toList());

        try {
            return (O) constructorToUse.newInstance(constructorObjects.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonParseException(e);
        }
    }
}
