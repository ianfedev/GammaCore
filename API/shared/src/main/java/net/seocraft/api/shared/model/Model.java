package net.seocraft.api.shared.model;

import net.seocraft.api.shared.serialization.model.FieldName;

public interface Model {

    @FieldName("_id")
    String id();
    
}
