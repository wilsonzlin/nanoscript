package in.wilsonl.nanoscript.Interpreting.Arguments;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Utils.ROMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NSValidatedArguments implements Iterable<NSValidatedArguments.NSValidatedArgument> {
    private final Map<String, NSData> values;

    public NSValidatedArguments(Map<String, NSData> values) {
        this.values = new ROMap<>(values);
    }

    public Set<String> getNames() {
        return new HashSet<>(values.keySet());
    }

    public NSData get(String name) {
        return values.get(name);
    }

    @Override
    public Iterator<NSValidatedArgument> iterator() {
        Iterator<Map.Entry<String, NSData>> mapIter = values.entrySet().iterator();
        return new Iterator<NSValidatedArgument>() {
            @Override
            public boolean hasNext() {
                return mapIter.hasNext();
            }

            @Override
            public NSValidatedArgument next() {
                Map.Entry<String, NSData> next = mapIter.next();
                return new NSValidatedArgument(next.getKey(), next.getValue());
            }
        };
    }

    public static class NSValidatedArgument {
        private final String name;
        private final NSData value;

        public NSValidatedArgument(String name, NSData value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public NSData getValue() {
            return value;
        }
    }
}
