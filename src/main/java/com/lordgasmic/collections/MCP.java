package com.lordgasmic.collections;

import com.lordgasmic.collections.repository.GSARepository;
import com.lordgasmic.collections.repository.RepositoryItem;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class MCP {
    public static void main(final String... args) throws SQLException, IOException {
        ConfigLoader.loadAllConfig();

        final GSARepository repository = (GSARepository) ConfigLoader.getGenericService("MemeRepository");
        final List<RepositoryItem> items = repository.getAllRepositoryItems("memeTag");
        items.forEach(item -> log.info("{}", item.getPropertyValue("uuid")));

        final RepositoryItem item = repository.getRepositoryItem("1322591424915.jpeg", "memeTag");
        log.info("{}", item.getPropertyValue("tag"));
    }
}

/*
ComponentNameResolver
IDGenerator
Cache
 */
