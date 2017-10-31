package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NSList extends NSData<List<NSData<?>>> {
    private NSList(List<NSData<?>> initialList) {
        super(Type.LIST, initialList);
    }

    private NSList() {
        this(new ArrayList<>());
    }

    // Equality of lists is only if they are the same instance, not based on the contents

    public static NSList from(List<NSData<?>> initialList) {
        return new NSList(new ArrayList<>(initialList));
    }

    public static NSList from(NSData[] initialList) {
        List<NSData<?>> list = new ArrayList<>();
        // Collections.addAll does not work
        //noinspection ManualArrayToCollectionCopy
        for (NSData v : initialList) {
            list.add(v);
        }
        return new NSList(list);
    }

    @Override
    public NSIterator nsIterate() {
        Iterator<NSData<?>> iter = getRawValue().iterator();

        return new NSIterator() {
            @Override
            public NSData<?> next() {
                if (!iter.hasNext()) {
                    throw VMError.from(BuiltinClass.EndOfIterationError);
                }
                return iter.next();
            }
        };
    }
}
