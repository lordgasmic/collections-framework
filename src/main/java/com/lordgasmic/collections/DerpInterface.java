package com.lordgasmic.collections;

public interface DerpInterface {

    DerpInterface INSTANCE = new DerpClass();

    default DerpInterface getInstance() {
        return INSTANCE;
    }

    class DerpClass implements DerpInterface {

    }
}
