package net.seocraft.commons.bukkit.util;

import org.bukkit.ChatColor;

import java.util.AbstractList;
import java.util.Arrays;

public class LoreDisplayArray<E> extends AbstractList<E> {

    private Object[] elementData;
    private int size;
    private static final Object[] METADATA = {};
    private static final int DEFAULT_CAPACITY = 10;

    LoreDisplayArray() {
        elementData = METADATA;
    }


    private void ensureCapacity(int minCapacity) {
        if (elementData == METADATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }


        if (minCapacity - elementData.length > 0) {
            int oldCapacity = elementData.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity - minCapacity < 0) {
                newCapacity = minCapacity;
            }

            elementData = Arrays.copyOf(elementData, newCapacity);
        }


    }

    public void add(String e, ChatColor color) {
        String[] split = e.split("%n%");
        for (String s : split) {
            s.replace("%n%", "");
            add(color + s);
        }
    }

    @Override
    public boolean add(Object e) {
        ensureCapacity(size + 1);
        elementData[size++] = e;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        rangeCheck(index);
        return (E) elementData[index];
    }

    private void rangeCheck(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int size() {

        return size;
    }
}
