package com.lordgasmic.collections.models.config.repository;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RepositoryConfigParser {

    private static final Gson gson = new Gson();

    public static void parse(final File config) {
        try (final BufferedReader br = new BufferedReader(new FileReader(config))) {
            final ItemDescriptor itemDescriptor = gson.fromJson(br, ItemDescriptor.class);
            System.out.println(itemDescriptor);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
