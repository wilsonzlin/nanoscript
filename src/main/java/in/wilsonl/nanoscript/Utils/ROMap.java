package in.wilsonl.nanoscript.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ROMap<K, V> extends HashMap<K, V> {
    @Override
    public V put(K k, V v) {
        if (containsKey(k)) {
            throw new UnsupportedOperationException("The key already exists");
        }
        return super.put(k, v);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (K k : map.keySet()) {
            if (containsKey(k)) {
                throw new UnsupportedOperationException("The key already exists");
            }
        }
        super.putAll(map);
    }

    @Override
    public V remove(Object o) {
        throw new UnsupportedOperationException("This operation is not supported on a ROMap");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This operation is not supported on a ROMap");
    }

    @Override
    public boolean remove(Object o, Object o1) {
        throw new UnsupportedOperationException("This operation is not supported on a ROMap");
    }

    @Override
    public boolean replace(K k, V v, V v1) {
        throw new UnsupportedOperationException("This operation is not supported on a ROMap");
    }

    @Override
    public V replace(K k, V v) {
        throw new UnsupportedOperationException("This operation is not supported on a ROMap");
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> biFunction) {
        throw new UnsupportedOperationException("This operation is not supported on a ROMap");
    }
}
