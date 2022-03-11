package com.lordgasmic.collections.repository;

import com.lordgasmic.collections.jdbc.DataSource;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.models.config.repository.Property;
import com.lordgasmic.collections.models.config.repository.Table;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
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
            if (mutableRepositoryItem.getPropertyValue(properties.get(i).getName()) == null) {
                continue;
            }
            if (i != 0) {
                fields.append(",");
                values.append(",");
            }
            fields.append(properties.get(i).getColumn());
            values.append("?");
        }
        final String insert = String.format("insert into %s (%s) values (%s)", table.getName(), fields, values);
        final String id = mDatasource.insert(insert, table, mutableRepositoryItem, this::createPreparedStatement);
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
                        case BINARY -> item.setProperty(property.getName(), rs.getBytes(property.getColumn()));
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

    private void createPreparedStatement(final Table table, final RepositoryItem item, final PreparedStatement stmt) throws SQLException {
        final List<Property> properties = table.getProperties();
        for (int i = 0; i < properties.size(); i++) {
            final String propertyName = properties.get(i).getName();
            if (item.getPropertyValue(propertyName) == null) {
                continue;
            }
            switch (properties.get(i).getDataType()) {
                case DOUBLE -> stmt.setDouble(i + 1, (Double) item.getPropertyValue(propertyName));
                case INT -> stmt.setInt(i + 1, (Integer) item.getPropertyValue(propertyName));
                case STRING -> stmt.setString(i + 1, (String) item.getPropertyValue(propertyName));
                case BINARY -> stmt.setBytes(i + 1, (byte[]) item.getPropertyValue(propertyName));
                default -> throw new IllegalArgumentException("cant find data type");
            }
        }
    }
}
