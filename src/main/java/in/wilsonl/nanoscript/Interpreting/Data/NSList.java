package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.VMError.EndOfIterationError;

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

    public static NSList from(List<NSData<?>> initialList) {
        return new NSList(initialList);
    }

    @Override
    public NSIterator iterate() {
        Iterator<NSData<?>> iter = getRawValue().iterator();

        return new NSIterator() {
            @Override
            public NSData<?> next() throws EndOfIterationError {
                if (!iter.hasNext()) {
                    throw new EndOfIterationError();
                }
                return iter.next();
            }
        };
    }
}
