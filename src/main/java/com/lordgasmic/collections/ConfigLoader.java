package com.lordgasmic.collections;

import com.lordgasmic.collections.helper.DataSource;
import com.lordgasmic.collections.helper.GenericService;
import com.lordgasmic.collections.models.config.component.ComponentConfigParser;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.models.config.repository.RepositoryConfigParser;
import com.lordgasmic.collections.repository.GSARepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigLoader {

    private static final Map<String, GenericService> components = new HashMap<>();
    private static final Map<String, ItemDescriptor> definitionFiles = new HashMap<>();

    public static void loadAllConfig() {
        final File resources = new File("src/main/resources");
        final File[] mixedConfig = resources.listFiles();

        Arrays.stream(mixedConfig).flatMap(ConfigLoader::toRegularFile).forEach(ConfigLoader::parse);
        hydrateComponents();
    }

    public static GenericService getGenericService(final String componentName) {
        return components.get(componentName);
    }

    private static Stream<File> toRegularFile(final File file) {
        return listDirs(file).stream();
    }

    private static List<File> listDirs(final File file) {
        final List<File> fileList = new ArrayList<>();

        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            for (final File f : files) {
                fileList.addAll(listDirs(f));
            }
        } else {
            fileList.add(file);
        }

        return fileList;
    }

    private static void parse(final File file) {
        if (file.getName().endsWith(".properties")) {
            components.put(file.getName().substring(0, file.getName().indexOf('.')), ComponentConfigParser.parse(file));
        } else if (file.getName().endsWith(".json")) {
            definitionFiles.put(file.getName(), RepositoryConfigParser.parse(file));
        } else {
            System.out.println("Skipping parsing of file: " + file.getName());
        }
    }

    private static void hydrateComponents() {
        for (final Map.Entry<String, GenericService> entry : components.entrySet()) {
            if (entry.getValue() instanceof GSARepository) {
                final GSARepository repository = (GSARepository) entry.getValue();
                repository.setMDatasource((DataSource) components.get(repository.getDataSource()));
                final String[] defFiles = repository.getDefinitionFiles().split(",");
                for (final String def : defFiles) {
                    final ItemDescriptor itemDescriptor = definitionFiles.get(def);
                    repository.getMItemDescriptors().put(itemDescriptor.getName(), itemDescriptor);
                }
            }
        }
    }
}
