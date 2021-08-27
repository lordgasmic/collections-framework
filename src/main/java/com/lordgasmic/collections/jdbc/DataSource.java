package com.lordgasmic.collections.jdbc;

import com.lordgasmic.collections.GenericService;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.repository.RepositoryItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiFunction;

public abstract class DataSource implements GenericService {

    public List<RepositoryItem> query(final String sql,
                                      final ItemDescriptor itemDescriptor,
                                      final BiFunction<ItemDescriptor, ResultSet, List<RepositoryItem>> function) throws SQLException {
        final String url = connectionString();
        try (final Connection conn = DriverManager.getConnection(url)) {

            final PreparedStatement stmt = conn.prepareStatement(sql);
            final ResultSet rs = stmt.executeQuery();

            return function.apply(itemDescriptor, rs);
        }
    }

    protected abstract String connectionString();
}
