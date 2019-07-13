package net.seocraft.api.shared.serialization.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ModelSerializer<O> implements JsonSerializer<O> {

    @NotNull
    private TypeToken<O> typeToken;

    public ModelSerializer(@NotNull Class<O> clazz) {
        typeToken = TypeToken.get(clazz);
    }

    public ModelSerializer(@NotNull TypeToken<O> typeToken) {
        this.typeToken = typeToken;
    }


    @Override
    public JsonElement serialize(O o, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        Class<? super O> rawType = typeToken.getRawType();
        Method[] typeMethods = rawType.getMethods();

        for (Method method : typeMethods) {
            if (method.getParameterCount() > 0 || method.getReturnType() == Void.TYPE || method.getReturnType() == Void.class || isAnObjectMethod(method)) {
                continue;
            }

            String elementName = getElementName(method);
            Object elementValue;
            try {
                elementValue = method.invoke(o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new JsonParseException("An exception ocurred while serializing the element " + elementName + " on the class " + type.getTypeName(), e);
            }

            jsonObject.add(elementName, jsonSerializationContext.serialize(elementValue, method.getReturnType()));
        }

        return jsonObject;
    }

    @Nullable
    private static String getMethodPrefix(Method method) {
        String name = method.getName();

        if (name.startsWith("has")) {
            return "has";
        }

        if (name.startsWith("is")) {
            return "is";
        }

        if (name.startsWith("get")) {
            return "get";
        }

        return null;
    }

    /**
     * @param method - The method to check against
     * @return - The name to be used in the serialization
     * @throws JsonParseException - If the method doesn't has a SerializedName annotation or a valid method name
     */
    public static String getElementName(Method method) throws JsonParseException {
        String methodPrefix = getMethodPrefix(method);
        
        if(methodPrefix == null && !method.isAnnotationPresent(FieldName.class)) {
            throw new JsonParseException("The method with name " + method.getName() + " doesn't has a FieldName or a valid name");
        }
        
        if(method.isAnnotationPresent(FieldName.class)) {
            FieldName serializedNameAnnotation = method.getAnnotation(FieldName.class);

            String name = serializedNameAnnotation.value();

            if (name.isEmpty()) {
                throw new JsonParseException("The method with name " + method.getName() + " has an invalid SerializedName annotation");
            }

            return name;
        }
        
        String name = method.getName().substring(methodPrefix.length());

        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    /**
     * This method checks if a method is a method added by default
     * by the object class
     *
     * @param method - The method to check
     * @return - True if is a method of the Object class
     */
    public static boolean isAnObjectMethod(Method method) {
        return method.getDeclaringClass() == Object.class;
    }

}
