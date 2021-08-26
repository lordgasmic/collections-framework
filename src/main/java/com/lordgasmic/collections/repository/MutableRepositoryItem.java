package com.lordgasmic.collections.repository;

public interface MutableRepositoryItem extends RepositoryItem {

    public void setProperty(String propertyName, Object value);
}
