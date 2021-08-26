package com.lordgasmic.collections.models.config.repository;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RepositoryConfigParser {

    private static final Gson gson = new Gson();

    public static ItemDescriptor parse(final File config) {
        try (final BufferedReader br = new BufferedReader(new FileReader(config))) {
            return gson.fromJson(br, ItemDescriptor.class);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
