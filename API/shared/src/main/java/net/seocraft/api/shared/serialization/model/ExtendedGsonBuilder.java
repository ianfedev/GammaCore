package net.seocraft.api.shared.serialization.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;

@AllArgsConstructor
public class ExtendedGsonBuilder {
    
    private GsonBuilder builder;

    public ExtendedGsonBuilder() {
        builder = new GsonBuilder();
    }

    public <V> ExtendedGsonBuilder registerModelSerializer(Class<V> model) {
        return registerModelSerializer(TypeToken.get(model));
    }

    public <V> ExtendedGsonBuilder registerModelSerializer(TypeToken<V> type) {
        builder.registerTypeAdapter(type.getType(), new ModelSerializer<>(type))
                .registerTypeAdapter(type.getType(), new ModelDeserializer<>(type));
        return this;
    }

    public ExtendedGsonBuilder setVersion(double ignoreVersionsAfter) {
        builder.setVersion(ignoreVersionsAfter);
        
        return this;
    }

    public ExtendedGsonBuilder excludeFieldsWithModifiers(int... modifiers) {
        builder.excludeFieldsWithModifiers(modifiers);
        return this;
    }

    public ExtendedGsonBuilder generateNonExecutableJson() {
        builder.generateNonExecutableJson();
        return this;
    }

    public ExtendedGsonBuilder excludeFieldsWithoutExposeAnnotation() {
        builder.excludeFieldsWithoutExposeAnnotation();
        return this;
    }

    public ExtendedGsonBuilder serializeNulls() {
        builder.serializeNulls();
        return this;
    }

    public ExtendedGsonBuilder enableComplexMapKeySerialization() {
        builder.enableComplexMapKeySerialization();
        return this;
    }

    public ExtendedGsonBuilder disableInnerClassSerialization() {
        builder.disableInnerClassSerialization();
        return this;
    }

    public ExtendedGsonBuilder setLongSerializationPolicy(LongSerializationPolicy serializationPolicy) {
        builder.setLongSerializationPolicy(serializationPolicy);
        return this;
    }

    public ExtendedGsonBuilder setFieldNamingPolicy(FieldNamingPolicy namingConvention) {
        builder.setFieldNamingPolicy(namingConvention);
        return this;
    }

    public ExtendedGsonBuilder setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
        builder.setFieldNamingStrategy(fieldNamingStrategy);
        return this;
    }

    public ExtendedGsonBuilder setExclusionStrategies(ExclusionStrategy... strategies) {
        builder.setExclusionStrategies(strategies);
        return this;
    }

    public ExtendedGsonBuilder addSerializationExclusionStrategy(ExclusionStrategy strategy) {
        builder.addSerializationExclusionStrategy(strategy);
        return this;
    }

    public ExtendedGsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy strategy) {
        builder.addDeserializationExclusionStrategy(strategy);
        return this;
    }

    public ExtendedGsonBuilder setPrettyPrinting() {
        builder.setPrettyPrinting();
        return this;
    }

    public ExtendedGsonBuilder setLenient() {
        builder.setLenient();
        return this;
    }

    public ExtendedGsonBuilder disableHtmlEscaping() {
        builder.disableHtmlEscaping();
        return this;
    }

    public ExtendedGsonBuilder setDateFormat(String pattern) {
        builder.setDateFormat(pattern);
        return this;
    }

    public ExtendedGsonBuilder setDateFormat(int style) {
        builder.setDateFormat(style);
        return this;
    }

    public ExtendedGsonBuilder setDateFormat(int dateStyle, int timeStyle) {
        builder.setDateFormat(dateStyle, timeStyle);
        return this;
    }

    public ExtendedGsonBuilder registerTypeAdapter(Type type, Object typeAdapter) {
        builder.registerTypeAdapter(type, typeAdapter);
        return this;
    }

    public ExtendedGsonBuilder registerTypeAdapterFactory(TypeAdapterFactory factory) {
        builder.registerTypeAdapterFactory(factory);
        return this;
    }

    public ExtendedGsonBuilder registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter) {
        builder.registerTypeHierarchyAdapter(baseType, typeAdapter);
        return this;
    }

    public ExtendedGsonBuilder serializeSpecialFloatingPointValues() {
        builder.serializeSpecialFloatingPointValues();
        return this;
    }

    public Gson create() {
        return builder.create();
    }

}
