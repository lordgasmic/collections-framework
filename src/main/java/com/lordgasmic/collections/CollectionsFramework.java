package com.lordgasmic.collections;

import java.io.IOException;

public final class CollectionsFramework {

    private static CollectionsFramework instance;

    static {
        try {
            instance = new CollectionsFramework();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private CollectionsFramework() throws IOException {
        ConfigLoader.loadAllConfig();
    }

    public static CollectionsFramework getInstance() {
        return instance;
    }

    public GenericService getGenericService(final String serviceName) {
        return ConfigLoader.getGenericService(serviceName);
    }
}
