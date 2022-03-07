package com.lordgasmic.collections.jdbc;

import com.lordgasmic.collections.GenericService;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.repository.RepositoryItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    public String insert(final String sql, final Function<ResultSet, String> function) throws SQLException {
        try (final Connection conn = DriverManager.getConnection(connectionString())) {
            final PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
            final ResultSet rs = stmt.getGeneratedKeys();
            return function.apply(rs);
        }
    }

    public void update(final String sql) {
        try (final Connection conn = DriverManager.getConnection(connectionString())) {
            final PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String connectionString();
}
