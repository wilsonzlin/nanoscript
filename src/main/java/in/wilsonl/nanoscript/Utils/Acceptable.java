package in.wilsonl.nanoscript.Utils;

import java.util.Arrays;
import java.util.Set;

public class Acceptable<T> {
    private final Set<T> set;

    @SafeVarargs
    public Acceptable(T... t) {
        set = new ROSet<>(Arrays.asList(t));
    }

    protected Acceptable(Set<T> set) {
        this.set = set;
    }

    public boolean has(T t) {
        return set.contains(t);
    }
}
