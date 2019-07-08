package net.seocraft.api.shared.model;

import com.google.gson.annotations.SerializedName;

public interface Model {

    @SerializedName("_id")
    String id();
    
}
