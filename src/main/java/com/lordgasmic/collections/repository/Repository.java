package com.lordgasmic.collections.repository;

import com.lordgasmic.collections.GenericService;

import java.sql.SQLException;
import java.util.List;

public interface Repository extends GenericService {

    List<RepositoryItem> getAllRepositoryItems(String itemDescriptor) throws SQLException;

    RepositoryItem getRepositoryItem(final String id, String itemDescriptor) throws SQLException;

    List<RepositoryItem> getRepositoryItems(List<String> ids, String itemDescriptor) throws SQLException;
}
