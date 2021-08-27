package com.lordgasmic.collections.repository;

import lombok.Data;

@Data
public class GSARepository extends AbstractGSARepository {
    private String repositoryName;
    private String definitionFiles;
    private String dataSource;
}
