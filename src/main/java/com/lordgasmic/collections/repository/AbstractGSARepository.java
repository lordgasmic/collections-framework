package com.lordgasmic.collections.repository;

import com.lordgasmic.collections.helper.DataSource;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.models.config.repository.Property;
import com.lordgasmic.collections.models.config.repository.Table;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class AbstractGSARepository implements MutableRepository {

    private DataSource mDatasource;
    private Map<String, ItemDescriptor> mItemDescriptors;

    public AbstractGSARepository() {
        mItemDescriptors = new HashMap<>();
    }

    @Override
    public List<RepositoryItem> getAllRepositoryItems(final String itemDescriptor) throws SQLException {
        // currently, only going to support single tables for item descriptors
        final ItemDescriptor id = mItemDescriptors.get(itemDescriptor);
        final String query = String.format("select * from %s", id.getTables().get(0).getName());
        final List<RepositoryItem> items = mDatasource.query(query, id, this::hydrateItem);

        return items;
    }

    @Override
    public RepositoryItem getRepositoryItem(final String id, final String itemDescriptor) throws SQLException {
        final ItemDescriptor itemDesc = mItemDescriptors.get(itemDescriptor);
        final Table table = itemDesc.getTables().get(0);
        final String query = String.format("select * from %s where %s=\"%s\"", table.getName(), table.getIdColumn(), id);
        final List<RepositoryItem> items = mDatasource.query(query, itemDesc, this::hydrateItem);

        return items.get(0);
    }

    private List<RepositoryItem> hydrateItem(final ItemDescriptor id, final ResultSet rs) {
        final List<RepositoryItem> items = new ArrayList<>();
        try {
            while (rs.next()) {
                final RepositoryItemImpl item = new RepositoryItemImpl();
                for (final Property property : id.getTables().get(0).getProperties()) {
                    switch (property.getDataType()) {
                        case INT -> item.setProperty(property.getName(), rs.getInt(property.getColumn()));
                        case DOUBLE -> item.setProperty(property.getName(), rs.getDouble(property.getColumn()));
                        case STRING -> item.setProperty(property.getName(), rs.getString(property.getColumn()));
                        default -> throw new IllegalArgumentException("cant find data type");
                    }
                }
                items.add(item);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return items;
    }
}
