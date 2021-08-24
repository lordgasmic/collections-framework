package com.lordgasmic.collections.models.config.component;

import com.lordgasmic.collections.helper.Component;

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

    public static Component parse(final File config) {
        final Properties prop = new Properties();
        try (final BufferedReader br = new BufferedReader(new FileReader(config))) {
            prop.load(br);
            final String clazzPath = Optional.ofNullable(prop.getProperty(CLASS)).orElseThrow();
            final Class<Component> clazz = (Class<Component>) Class.forName(clazzPath);
            final Component component = clazz.getDeclaredConstructor().newInstance();
            return parseDatasource(prop, component);
        } catch (final IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static Component parseDatasource(final Properties prop, final Component component) throws NoSuchFieldException, IllegalAccessException {
        final Field[] fields = component.getClass().getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            field.set(component, Optional.ofNullable(prop.getProperty(field.getName())).orElseThrow());
        }

        return component;
    }
}
