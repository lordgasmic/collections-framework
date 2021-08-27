package com.lordgasmic.collections.jdbc;

public class MySqlDataSource extends DataSource {
    private String host;
    private String database;
    private String username;
    private String password;

    @Override
    protected String connectionString() {
        return String.format("%s/%s?user=%s&password=%s", host, database, username, password);
    }
}
