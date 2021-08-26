package com.lordgasmic.collections.repository;

import com.lordgasmic.collections.helper.DataSource;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.models.config.repository.Property;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class GSARepository implements MutableRepository {
    private String repositoryName;
    private String definitionFiles;
    private String dataSource;

    private DataSource mDatasource;
    private Map<String, ItemDescriptor> itemDescriptors;

    @Override
    public List<RepositoryItem> getAllRepositoryItems(final String itemDescriptor) throws SQLException {
        final List<RepositoryItem> items = new ArrayList<>();

        // currently, only going to support single tables for item descriptors
        final ItemDescriptor id = itemDescriptors.get(itemDescriptor);
        final String query = String.format("select * from %s", id.getTables().get(0));
        final ResultSet rs = mDatasource.query(query);
        while (rs.next()) {
            items.add(hydrateItem(rs));
        }

        return items;
    }

    @Override
    public RepositoryItem getRepositoryItem(final String id, final String itemDescriptor) {
        final RepositoryItem item = new RepositoryItemImpl();

        return item;
    }

    private static RepositoryItem hydrateItem(final ResultSet rs) throws SQLException {
        final RepositoryItemImpl item = new RepositoryItemImpl();
        for (final Property property : id.getTables().get(0).getProperties()) {
            switch (property.getDataType()) {
                case INT -> item.setProperty(property.getName(), rs.getInt(property.getColumn()));
                case DOUBLE -> item.setProperty(property.getName(), rs.getDouble(property.getColumn()));
                case STRING -> item.setProperty(property.getName(), rs.getString(property.getColumn()));
                default -> throw new IllegalArgumentException("cant find data type");
            }
        }
        return item;
    }
}
