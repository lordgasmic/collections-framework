package com.lordgasmic.collections.models.config.repository;

import java.sql.SQLException;

@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {

    void apply(A a, B b, C c, D d) throws SQLException;
}