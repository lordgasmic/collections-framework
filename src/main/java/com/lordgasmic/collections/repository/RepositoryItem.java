package com.lordgasmic.collections.repository;

public interface RepositoryItem {

    Object getPropertyValue(String propertyName);

    String getItemDescriptorName();
}
