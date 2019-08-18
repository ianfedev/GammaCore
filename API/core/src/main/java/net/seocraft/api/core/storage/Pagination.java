package net.seocraft.api.core.storage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Pagination<T> {

    int getPageSize();

    @NotNull List<T> getObjects();

    boolean pageExists(int page);

    int totalPages();

    List<T> getPage(int page);
}
