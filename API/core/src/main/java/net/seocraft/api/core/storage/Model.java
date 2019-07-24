package net.seocraft.api.core.storage;


import com.fasterxml.jackson.annotation.JsonProperty;

public interface Model {

    @JsonProperty("_id")
    String getId();
}
