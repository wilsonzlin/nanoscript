package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.Utils;

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

    private int getValidKeyFromTerms(List<NSData<?>> terms) {
        if (terms.size() != 1) {
            throw VMError.from(BuiltinClass.KeyError, "No index provided");
        }
        NSData<?> index = terms.get(0);
        if (index.getType() != Type.NUMBER) {
            throw VMError.from(BuiltinClass.KeyError, "Index is not a number");
        }
        double rawIdx = ((NSNumber) index).getRawValue();
        if (!Utils.isInt(rawIdx)) {
            throw VMError.from(BuiltinClass.KeyError, "Index is not an integer");
        }

        if (rawIdx < 0) {
            rawIdx = getRawValue().size() + rawIdx;
        }

        if (rawIdx < 0 || rawIdx >= getRawValue().size()) {
            throw VMError.from(BuiltinClass.OutOfBoundsError, "Index is out of bounds");
        }

        return (int) rawIdx;
    }

    @Override
    public NSData<?> nsApplyHashOperator() {
        return NSNumber.from(getRawValue().size());
    }

    @Override
    public NSData<?> nsAccess(String member) {
        switch (member) {
            case "pop":
                return new NSNativeFunction((__, arguments) -> getRawValue().remove(getRawValue().size() - 1));
                // TODO

            default:
                throw VMError.from(BuiltinClass.ReferenceError, String.format("Member `%s` does not exist", member));
        }
    }

    @Override
    public NSData<?> nsLookup(List<NSData<?>> terms) {
        int idx = getValidKeyFromTerms(terms);
        return getRawValue().get(idx);
    }

    @Override
    public void nsUpdate(List<NSData<?>> terms, NSData<?> value) {
        if (terms.isEmpty()) {
            getRawValue().add(value);
        } else {
            int idx = getValidKeyFromTerms(terms);
            getRawValue().set(idx, value);
        }
    }

    @Override
    public NSBoolean nsToBoolean() {
        return NSBoolean.TRUE;
    }

    @Override
    public NSString nsToString() {
        List<String> strvals = new ROList<>(getRawValue().size());
        for (NSData<?> v : getRawValue()) {
            strvals.add(v.nsToString().getRawValue());
        }
        return NSString.from(Utils.join(", ", strvals));
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
