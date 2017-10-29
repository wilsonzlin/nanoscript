package in.wilsonl.nanoscript.Utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

public class ROSet<T> extends HashSet<T> {
    public ROSet() {
        super();
    }

    public ROSet(Collection<? extends T> collection) {
        super(collection);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This operation is not supported on a ROSet");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This operation is not supported on a ROSet");
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException("This operation is not supported on a ROSet");
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException("This operation is not supported on a ROSet");
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("This operation is not supported on a ROSet");
    }
}
