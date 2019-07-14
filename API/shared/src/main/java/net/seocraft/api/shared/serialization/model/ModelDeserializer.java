package net.seocraft.api.shared.serialization.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelDeserializer<O> implements JsonDeserializer<O> {
    @NotNull
    private TypeToken<O> typeToken;

    public ModelDeserializer(@NotNull Class<O> clazz) {
        typeToken = TypeToken.get(clazz);
    }

    public ModelDeserializer(@NotNull TypeToken<O> typeToken) {
        this.typeToken = typeToken;
    }

    @Override
    public O deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Class<? super O> rawType = typeToken.getRawType();
        Method[] typeMethods = rawType.getMethods();

        Map<String, TypeToken<?>> elementsType = new HashMap<>();

        for (Method method : typeMethods) {
            if (ModelSerializer.shouldIgnoreMethod(method)) {
                continue;
            }

            String elementName = ModelSerializer.getElementName(method);
            Type methodReturnType = method.getGenericReturnType();

            TypeToken<?> returnType = TypeToken.get(methodReturnType);

            if (methodReturnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) methodReturnType;

                returnType = TypeToken.getParameterized(parameterizedType.getRawType(), parameterizedType.getActualTypeArguments());
            }

            elementsType.put(elementName, returnType);
        }

        Map<String, Object> deserializedObjects = new HashMap<>();

        elementsType.forEach((elementName, elementType) -> {
            JsonElement element = object.get(elementName);

            deserializedObjects.put(elementName, jsonDeserializationContext.deserialize(element, elementType.getType()));
        });


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

            if (!deserializedObjects.keySet().containsAll(properties)) {
                continue;
            }

            constructorToUse = constructor;
            constructorProperties = properties;

            break;
        }

        if (constructorToUse == null) {
            throw new JsonParseException("The class " + classToCreate.getName() + " doesn't has a constructor with ConstructorProperties annotation!");
        }

        List<Object> constructorObjects = constructorProperties.stream()
                .filter(deserializedObjects::containsKey)
                .map(deserializedObjects::get).collect(Collectors.toList());

        try {
            return (O) constructorToUse.newInstance(constructorObjects.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonParseException(e);
        }
    }
}
