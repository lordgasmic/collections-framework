package com.lordgasmic.collections.repository;

import com.lordgasmic.collections.helper.DataSource;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;

/**
 * Responsible for retrieving data from the database (DataSource)
 */
public class RepositoryItemDescriptor {

    private final DataSource dataSource;
    private final ItemDescriptor itemDescriptor;

    public RepositoryItemDescriptor(final DataSource dataSource, final ItemDescriptor itemDescriptor) {
        this.dataSource = dataSource;
        this.itemDescriptor = itemDescriptor;
    }


}
