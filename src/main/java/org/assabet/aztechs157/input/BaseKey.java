package org.assabet.aztechs157.input;

public abstract class BaseKey<T extends BaseKey<T>> {
    private String label = "Unlabeled Key";

    @SuppressWarnings("unchecked")
    public T label(final String label) {
        this.label = label;
        return (T) this;
    }

    @Override
    public String toString() {
        return label;
    }
}
