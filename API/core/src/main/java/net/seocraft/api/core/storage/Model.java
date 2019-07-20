package net.seocraft.api.core.storage;

import net.seocraft.api.core.old.serialization.model.FieldName;

public interface Model {

    @FieldName("_id")
    String id();
    
}
