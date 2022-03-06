package com.lordgasmic.collections.service;

import com.lordgasmic.collections.jdbc.DataSource;
import lombok.Data;

@Data
public class IdGenerator extends AbstractIdGenerator {

    private DataSource dataSource;

    public String nextId() {
        int currentId = Integer.parseInt(getMCurrentId());
        currentId++;
        dataSource.update(String.format("update id_generator set current_id = %s where name = %s", currentId, ""));

        final String prefix = getMPrefix() == null ? "" : getMPrefix();
        final String suffix = getMSuffix() == null ? "" : getMSuffix();
        return prefix + currentId + suffix;
    }
}
// i need to load in data and give it an id even if that id is meaningless

// id generator needs to know about all of the item descriptors (like all of them)
// id generator is dependant on datasource
// generic services can be dependant on other services

// sql loader

// do i do it via postman or try and load it at startup?
// atg had a loader at the beginning of the process that loaded all of the data for you in scripts
// there were also processes that used atg to load data files, dataloader comes to mind

// can i leave this all up to the database to assign?
// do i need to create an id at object creation? can i just make a derp object and give it an id when i submit it?

// createItem() doesnt need an id. i can just add stuff to it and then the id comes later
// works if the item is never saved, no id is written to the database

// is duplicate?
