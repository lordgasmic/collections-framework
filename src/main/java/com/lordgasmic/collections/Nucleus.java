package com.lordgasmic.collections;

public class Nucleus {

    private static final Nucleus INSTANCE = new Nucleus();

    private Nucleus(){}

    public static Nucleus getInstance(){
        return INSTANCE;
    }

    public void load() {
        // do load
    }
}
