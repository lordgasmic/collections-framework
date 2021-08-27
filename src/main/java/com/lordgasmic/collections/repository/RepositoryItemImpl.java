package com.lordgasmic.collections.repository;

import java.util.HashMap;
import java.util.Map;

public class RepositoryItemImpl implements MutableRepositoryItem {

    private final Map<String, Object> properties;

    public RepositoryItemImpl() {
        properties = new HashMap<>();
    }

    @Override
    public void setProperty(final String propertyName, final Object value) {
        properties.put(propertyName, value);
    }

    @Override
    public Object getPropertyValue(final String propertyName) {
        return properties.get(propertyName);
    }
}
