package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinNSListMethods;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.KeyError;
import static in.wilsonl.nanoscript.Interpreting.Data.NSData.Type.LIST;
import static in.wilsonl.nanoscript.Interpreting.Data.NSData.Type.NUMBER;

public class NSList extends NSData {
    private static final NSDataHelperMethods helperMethods = new NSDataHelperMethods(NSList.class, BuiltinNSListMethods.class);
    private final List<NSData> rawList;

    private NSList(List<NSData> initialList) {
        super(LIST);
        rawList = initialList;
    }

    public static NSList from(List<NSData> initialList) {
        return new NSList(new ArrayList<>(initialList));
    }

    public static NSList fromEmpty() {
        return new NSList(new ArrayList<>());
    }

    // Equality of lists is only if they are the same instance, not based on the contents

    public static NSList from(NSData[] initialList) {
        List<NSData> list = new ArrayList<>();
        // Collections.addAll does not work
        //noinspection ManualArrayToCollectionCopy
        for (NSData v : initialList) {
            //noinspection UseBulkOperation
            list.add(v);
        }
        return new NSList(list);
    }

    public List<NSData> getRawList() {
        return rawList;
    }

    public int getValidIndex(NSData index, boolean canBeAtLength) {
        if (index.getType() != NUMBER) {
            throw VMError.from(KeyError, "Index is not a number");
        }
        // Will throw exception if not an integer
        long rawIdx;
        try {
            rawIdx = ((NSNumber) index).toInt();
        } catch (NumberFormatException nfe) {
            throw VMError.from(KeyError, "Index is not an integer");
        }

        if (rawIdx < 0) {
            rawIdx = rawList.size() + rawIdx;
        }

        if (rawIdx < 0 || rawIdx > rawList.size() || (!canBeAtLength && rawIdx == rawList.size())) {
            throw VMError.from(BuiltinClass.OutOfBoundsError, "Index is out of bounds");
        }

        return (int) rawIdx;
    }

    private int getValidIndexFromTerms(List<NSData> terms) {
        if (terms.size() != 1) {
            throw VMError.from(KeyError, "No index provided");
        }
        NSData index = terms.get(0);
        return getValidIndex(index, false);
    }

    @Override
    public NSData nsApplyHashOperator() {
        return NSNumber.from(rawList.size());
    }

    @Override
    public NSData nsAccess(String member) {
        try {
            return helperMethods.getMethod(this, member);
        } catch (NoSuchMethodException e) {
            throw VMError.from(BuiltinClass.ReferenceError, String.format("Member `%s` does not exist", member));
        }
    }

    @Override
    public NSData nsLookup(List<NSData> terms) {
        int idx = getValidIndexFromTerms(terms);
        return rawList.get(idx);
    }

    @Override
    public void nsUpdate(List<NSData> terms, NSData value) {
        if (terms.isEmpty()) {
            rawList.add(value);
        } else {
            int idx = getValidIndexFromTerms(terms);
            rawList.set(idx, value);
        }
    }

    @Override
    public NSBoolean nsToBoolean() {
        return NSBoolean.TRUE;
    }

    @Override
    public NSString nsToString() {
        List<String> strvals = new ROList<>(rawList.size());
        for (NSData v : rawList) {
            strvals.add(v.nsToString().getRawString());
        }
        return NSString.from("[" + Utils.join(", ", strvals) + "]");
    }

    @Override
    public NSIterator nsIterate() {
        Iterator<NSData> iter = rawList.iterator();

        return new NSIterator() {
            @Override
            public NSData next() {
                if (!iter.hasNext()) {
                    throw VMError.from(BuiltinClass.EndOfIterationError);
                }
                return iter.next();
            }
        };
    }
}
