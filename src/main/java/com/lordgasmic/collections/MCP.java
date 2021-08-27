package com.lordgasmic.collections;

import com.lordgasmic.collections.repository.GSARepository;
import com.lordgasmic.collections.repository.RepositoryItem;

import java.sql.SQLException;
import java.util.List;

public class MCP {
    public static void main(final String... args) throws SQLException {
        ConfigLoader.loadAllConfig();

        final GSARepository comicRepository = (GSARepository) ConfigLoader.getGenericService("MemeRepository");
        final List<RepositoryItem> items = comicRepository.getAllRepositoryItems("memeTag");
        items.forEach(item -> System.out.println(item.getPropertyValue("uuid")));

        final RepositoryItem item = comicRepository.getRepositoryItem("1322591424915.jpeg", "memeTag");
        System.out.println(item.getPropertyValue("tag"));
    }
}

/*
ComponentNameResolver
 */
