package com.lordgasmic.collections.helper;

import lombok.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class DataSource implements Component {

    private String host;
    private String database;
    private String username;
    private String password;

    public ResultSet query(final String sql) throws SQLException {
        final String url = String.format("%s/%s?user=%s&password=%s", host, database, username, password);
        try (final Connection conn = DriverManager.getConnection(url)) {

            final PreparedStatement stmt = conn.prepareStatement(sql);
            return stmt.executeQuery();
        }
    }
}
