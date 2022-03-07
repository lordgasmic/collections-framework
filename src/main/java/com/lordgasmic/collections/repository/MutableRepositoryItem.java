package com.lordgasmic.collections.repository;

public interface MutableRepositoryItem extends RepositoryItem {

    void setProperty(String propertyName, Object value);

    void setItemDescriptorName(String itemDescriptorName);
}
