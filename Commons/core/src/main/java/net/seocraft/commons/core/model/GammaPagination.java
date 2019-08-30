package net.seocraft.commons.core.model;

import net.seocraft.api.core.storage.Pagination;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GammaPagination<T> implements Pagination<T> {

    private int pageSize;
    @NotNull private List<T> objects;

    public GammaPagination(int pageSize, @NotNull List<T> objects) {
        this.pageSize = pageSize;
        this.objects = objects;
    }

    public GammaPagination(int pageSize, @NotNull Set<T> objects) {
        this.pageSize = pageSize;
        this.objects = new ArrayList<>(objects);
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    public @NotNull List<T> getObjects() {
        return this.objects;
    }

    public boolean pageExists(int page) {
        return !(page < 0) && page < totalPages();
    }

    public int totalPages() {
        return (int) Math.ceil((double) this.objects.size() / this.pageSize);
    }

    public List<T> getPage(int page) {
        List<T> pageResult = new ArrayList<>();

        int min = (page * this.pageSize) - pageSize;
        int max = page * this.pageSize - 1;

        if (max > this.objects.size()) max = this.objects.size();

        for (int i = min; max > i; i++) {
            pageResult.add(this.objects.get(i));
        }

        return pageResult;
    }

}
