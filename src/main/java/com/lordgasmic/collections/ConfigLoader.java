package com.lordgasmic.collections;

import com.lordgasmic.collections.helper.Component;
import com.lordgasmic.collections.models.config.component.ComponentConfigParser;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.models.config.repository.RepositoryConfigParser;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {

    private static final Map<String, Component> components = new HashMap<>();
    private static final Map<String, ItemDescriptor> definitionFiles = new HashMap<>();

    public static void loadAllConfig() {
        final File resources = new File("src/main/resources");
        final File[] mixedConfig = resources.listFiles();

        Arrays.stream(mixedConfig).parallel().forEach(ConfigLoader::parse);
    }

    private static void parse(final File file) {
        if (file.getName().endsWith(".properties")) {
            components.put(file.getName().substring(0, file.getName().indexOf('.')), ComponentConfigParser.parse(file));
        } else if (file.getName().endsWith(".json")) {
            definitionFiles.put(file.getName().substring(0, file.getName().indexOf('.')), RepositoryConfigParser.parse(file));
        } else {
            System.out.println("Skipping parsing of file: " + file.getName());
        }
    }
}
