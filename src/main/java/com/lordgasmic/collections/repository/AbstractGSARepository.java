package com.lordgasmic.collections.repository;

import com.lordgasmic.collections.jdbc.DataSource;
import com.lordgasmic.collections.models.config.repository.DataType;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.models.config.repository.Property;
import com.lordgasmic.collections.models.config.repository.Table;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
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

    @Override
    public List<RepositoryItem> getRepositoryItems(final List<String> ids, final String itemDescriptor) throws SQLException {
        final ItemDescriptor itemDesc = mItemDescriptors.get(itemDescriptor);
        final Table table = itemDesc.getTables().get(0);
        final String query = String.format("select * from %s where id in (%s)", table.getName(), String.join(",", ids));
        return mDatasource.query(query, itemDesc, this::hydrateItem);
    }

    @Override
    public MutableRepositoryItem createItem(final String itemDescriptor) {
        final MutableRepositoryItem item = new RepositoryItemImpl();
        item.setItemDescriptorName(itemDescriptor);
        return item;
    }

    @Override
    public RepositoryItem updateItem(final RepositoryItem repositoryItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RepositoryItem addItem(final MutableRepositoryItem mutableRepositoryItem) throws IllegalStateException, SQLException {
        final Table table = mItemDescriptors.get(mutableRepositoryItem.getItemDescriptorName()).getTables().get(0);
        final List<Property> properties = table.getProperties();
        final StringBuilder fields = new StringBuilder();
        final StringBuilder values = new StringBuilder();
        for (int i = 0; i < properties.size(); i++) {
            if (i != 0) {
                fields.append(",");
                values.append(",");
            }
            fields.append(properties.get(i).getColumn());
            final String propertyName = properties.get(i).getName();
            if (properties.get(i).getDataType() == DataType.STRING) {
                values.append("'");
                values.append(mutableRepositoryItem.getPropertyValue(propertyName));
                values.append("'");
            } else {
                values.append(mutableRepositoryItem.getPropertyValue(propertyName));
            }
        }
        final String insert = String.format("insert into %s (%s) values (%s)", table.getName(), fields, values);
        mDatasource.insert(insert);
        final String id = mDatasource.query("select LAST_INSERT_ID() as last_insert_id", this::getLastInsertId);
        log.info("last insert id " + id);
        return getRepositoryItem(id, mutableRepositoryItem.getItemDescriptorName());
    }

    private List<RepositoryItem> hydrateItem(final ItemDescriptor id, final ResultSet rs) {
        final List<RepositoryItem> items = new ArrayList<>();
        try {
            while (rs.next()) {
                final RepositoryItemImpl item = new RepositoryItemImpl();
                item.setItemDescriptorName(id.getName());
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

    private String getLastInsertId(final ResultSet rs) {
        try {
            rs.next();
            return rs.getString("last_insert_id");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
