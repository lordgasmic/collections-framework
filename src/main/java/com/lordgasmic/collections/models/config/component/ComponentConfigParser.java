package com.lordgasmic.collections.models.config.component;

import com.lordgasmic.collections.GenericService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Properties;

public class ComponentConfigParser {
    private static final String CLASS = "$class";

    public static GenericService parse(final File config) {
        final Properties prop = new Properties();
        try (final BufferedReader br = new BufferedReader(new FileReader(config))) {
            prop.load(br);

            final String clazzPath = Optional.ofNullable(prop.getProperty(CLASS)).orElseThrow();
            final Class<GenericService> clazz = (Class<GenericService>) Class.forName(clazzPath);
            final GenericService genericService = clazz.getDeclaredConstructor().newInstance();

            final Field[] fields = genericService.getClass().getDeclaredFields();
            for (final Field field : fields) {
                field.setAccessible(true);
                field.set(genericService, Optional.ofNullable(prop.getProperty(field.getName())).orElseThrow());
            }

            return genericService;
        } catch (final IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
