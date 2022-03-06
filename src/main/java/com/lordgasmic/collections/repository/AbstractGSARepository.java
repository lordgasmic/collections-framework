package com.lordgasmic.collections.repository;

import com.lordgasmic.collections.jdbc.DataSource;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.models.config.repository.Property;
import com.lordgasmic.collections.models.config.repository.Table;
import com.lordgasmic.collections.service.IdGenerator;
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
    private Map<String, IdGenerator> mIdGenerators;
    private Map<String, ItemDescriptor> mItemDescriptors;

    public AbstractGSARepository() {
        mIdGenerators = new HashMap<>();
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
        final IdGenerator idGenerator = mIdGenerators.get(itemDescriptor);
        final ItemDescriptor itemDesc = mItemDescriptors.get(itemDescriptor);
        final Table table = itemDesc.getTables().get(0);

        final MutableRepositoryItem item = new RepositoryItemImpl();
        item.setProperty(table.getIdColumnName(), idGenerator.nextId());
        return item;
    }

    @Override
    public RepositoryItem updateItem(final RepositoryItem repositoryItem) {
        return null;
    }

    @Override
    public RepositoryItem addItem(final MutableRepositoryItem mutableRepositoryItem) throws IllegalStateException {
        return null;
    }

    public void initialize() {
        try {
            for (final String item : mItemDescriptors.keySet()) {
                final String query = String.format("select * from id_generator where item_descriptor in ('%s')", item);
                final ResultSet rs = mDatasource.query(query);
                final IdGenerator idGenerator = new IdGenerator();
                if (rs.next()) {
                    idGenerator.setMItemDescriptor(rs.getString("item_descriptor"));
                    idGenerator.setMCurrentId(rs.getString("current_id"));
                    idGenerator.setMPrefix(rs.getString("prefix"));
                    idGenerator.setMSuffix(rs.getString("suffix"));
                } else {
                    idGenerator.setMItemDescriptor(item);
                    idGenerator.setMCurrentId("0");
                    final String insert = String.format("insert into id_generator(item_descriptor, current_id) values('%s','%s')",
                                                        idGenerator.getMItemDescriptor(),
                                                        idGenerator.getMCurrentId());
                    mDatasource.insert(insert);
                }
                mIdGenerators.put(item, idGenerator);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
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

    private RepositoryItem warmItem(final ItemDescriptor id) {
        final MutableRepositoryItem item = new RepositoryItemImpl();
        item.setItemDescriptorName(id.getName());
        return item;
    }
}
