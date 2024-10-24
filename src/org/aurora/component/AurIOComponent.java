package org.aurora.component;

public abstract class AurIOComponent<T extends AurIOComponent<T>> implements Cloneable {

    @Override
    public abstract AurIOComponent<T> clone();
}
