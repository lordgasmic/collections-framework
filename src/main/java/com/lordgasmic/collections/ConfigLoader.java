package com.lordgasmic.collections;

import com.lordgasmic.collections.jdbc.DataSource;
import com.lordgasmic.collections.models.config.component.ComponentConfigParser;
import com.lordgasmic.collections.models.config.repository.ItemDescriptor;
import com.lordgasmic.collections.models.config.repository.RepositoryConfigParser;
import com.lordgasmic.collections.repository.GSARepository;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class ConfigLoader {

    private static final Map<String, GenericService> components = new HashMap<>();
    private static final Map<String, ItemDescriptor> definitionFiles = new HashMap<>();

    static void loadAllConfig() throws IOException {
        final ClassLoader loader = ConfigLoader.class.getClassLoader();

        final Set<String> results; //avoid duplicates in case it is a subdirectory
        final String path = "collections-config/";
        final URL url = loader.getResource(path);
        log.info(url.getPath());
        if (url.getProtocol().equals("file")) {
            results = Files.walk(Paths.get(url.getPath()))
                           .filter(Files::isRegularFile)
                           .map(Path::toString)
                           .map(p -> p.substring(p.indexOf(path)))
                           .collect(toSet());
        } else if (url.getProtocol().equals("jar")) {
            log.info("in the jar");
            final String jarPath = url.getPath().substring(5, url.getPath().lastIndexOf("!")); //strip out only the JAR file
            log.info(jarPath);
            final JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));
            results = jar.stream()
                         .map(ZipEntry::getName)
                         .peek(log::info)
                         .filter(n -> n.startsWith(path))
                         .peek(System.out::println)
                         .filter(u -> !u.endsWith("/"))
                         .collect(toSet());
        } else {
            results = new HashSet<>();
        }

        results.forEach(System.out::println);
        //        results.forEach(log::info);

        results.forEach(ConfigLoader::parse);
        hydrateComponents();
    }

    static GenericService getGenericService(final String componentName) {
        return components.get(componentName);
    }

    private static void parse(final String path) {
        final InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream(path);
        if (path.endsWith(".properties")) {
            components.put(path.substring(path.lastIndexOf('/') + 1, path.indexOf('.')), ComponentConfigParser.parse(is));
        } else if (path.endsWith(".json")) {
            definitionFiles.put(path.substring(path.indexOf('/') + 1), RepositoryConfigParser.parse(is));
        } else {
            log.info("Skipping parsing of file: " + path);
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
