package com.lordgasmic.collections.jdbc;

import com.lordgasmic.collections.helper.GenericService;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.repository.RepositoryItem;
import lombok.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiFunction;

@Data
public class DataSource implements GenericService {

    private String host;
    private String database;
    private String username;
    private String password;

    public List<RepositoryItem> query(final String sql,
                                      final ItemDescriptor itemDescriptor,
                                      final BiFunction<ItemDescriptor, ResultSet, List<RepositoryItem>> function) throws SQLException {
        final String url = String.format("%s/%s?user=%s&password=%s", host, database, username, password);
        try (final Connection conn = DriverManager.getConnection(url)) {

            final PreparedStatement stmt = conn.prepareStatement(sql);
            final ResultSet rs = stmt.executeQuery();

            return function.apply(itemDescriptor, rs);
        }
    }
}
