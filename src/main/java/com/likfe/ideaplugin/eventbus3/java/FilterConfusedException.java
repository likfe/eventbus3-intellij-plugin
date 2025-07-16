package com.likfe.ideaplugin.eventbus3.java;

public class FilterConfusedException extends Exception {
    public final Object[] values;

    public FilterConfusedException(String message) {
        this(message, (Object[]) null);
    }

    public FilterConfusedException(String message, Object... values) {
        super(message);
        this.values = values;
    }
}
