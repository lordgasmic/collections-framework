package com.lordgasmic.collections.repository;

import com.lordgasmic.collections.helper.Component;

import java.sql.SQLException;
import java.util.List;

public interface Repository extends Component {

    public List<RepositoryItem> getAllRepositoryItems(String itemDescriptor) throws SQLException;

    public RepositoryItem getRepositoryItem(final String id, String itemDescriptor);
}
