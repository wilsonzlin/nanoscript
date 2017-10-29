package in.wilsonl.nanoscript.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ROList<T> extends ArrayList<T> {
    @Override
    public void trimToSize() {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    public void ensureCapacity(int i) {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    public T remove(int i) {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    protected void removeRange(int i, int i1) {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    public void replaceAll(UnaryOperator<T> unaryOperator) {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }

    @Override
    public void sort(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("This operation is not supported on a ROList");
    }
}
