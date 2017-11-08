package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.Arguments.NSArgument;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;
import in.wilsonl.nanoscript.Interpreting.VMError;

import java.util.Collections;
import java.util.List;

import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.OutOfBoundsError;
import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.TypeError;
import static in.wilsonl.nanoscript.Interpreting.Data.NSData.Type.*;

@BuiltinMethods(target = NSList.class)
public class BuiltinNSListMethods {
    @BuiltinMethod(name = "fill")
    public static NSData fill(NSList self, @BuiltinMethodParameter(name = "filler") NSData filler) {
        List<NSData> rawList = self.getRawList();
        for (int i = 0; i < rawList.size(); i++) {
            rawList.set(i, filler);
        }
        return self;
    }

    @BuiltinMethod(name = "pop")
    public static NSData pop(NSList self) {
        List<NSData> rawList = self.getRawList();
        try {
            return rawList.remove(rawList.size() - 1);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            throw VMError.from(BuiltinClass.NoSuchElementError, "List is empty");
        }
    }

    @BuiltinMethod(name = "push")
    public static NSData push(NSList self, @BuiltinMethodParameter(name = "value") NSData value) {
        List<NSData> rawList = self.getRawList();
        rawList.add(value);
        return self;
    }

    @BuiltinMethod(name = "reverse")
    public static NSData reverse(NSList self) {
        List<NSData> rawList = self.getRawList();
        Collections.reverse(rawList);
        return self;
    }

    @BuiltinMethod(name = "removeAt")
    public static NSData removeAt(NSList self, @BuiltinMethodParameter(name = "index", types = {NUMBER}) NSData index) {
        List<NSData> rawList = self.getRawList();
        int rawIdx = self.getValidIndex(index, false);
        return rawList.remove(rawIdx);
    }

    @BuiltinMethod(name = "shift")
    public static NSData shift(NSList self) {
        List<NSData> rawList = self.getRawList();
        try {
            return rawList.remove(0);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            throw VMError.from(BuiltinClass.NoSuchElementError, "List is empty");
        }
    }

    @BuiltinMethod(name = "sort")
    public static NSData sort(NSList self, @BuiltinMethodParameter(optional = true, variableLength = true, name = "comparators", types = {CALLABLE}) List<NSData> comparators) {
        List<NSData> rawList = self.getRawList();
        if (!comparators.isEmpty()) {
            rawList.sort((a, b) -> {
                for (NSData comparator : comparators) {
                    NSData res = comparator.nsCall(NSArgument.buildArguments(a, b));
                    if (res.getType() != NUMBER) {
                        throw VMError.from(TypeError, "Return value from comparator is not a number");
                    }
                    int rawRes = (int) ((NSNumber) res).getRawNumber();
                    if (rawRes != 0) {
                        return rawRes;
                    }
                }
                return 0;
            });
        } else {
            // VMError exception will be thrown if can't be compared
            rawList.sort((a, b) -> (int) a.nsCompare(b).getRawNumber());
        }
        return self;
    }

    @BuiltinMethod(name = "splice")
    public static NSData splice(NSList self, @BuiltinMethodParameter(name = "start", types = {NUMBER}) NSData start, @BuiltinMethodParameter(name = "end", types = {NUMBER}) NSData end, @BuiltinMethodParameter(optional = true, variableLength = true, name = "replacements") List<NSData> replacements) {
        List<NSData> rawList = self.getRawList();
        int startIdx = self.getValidIndex(start, true);
        int endIdx = self.getValidIndex(end, true);

        if (startIdx > endIdx) {
            throw VMError.from(OutOfBoundsError, "End is before start");
        }

        NSList sublist;
        if (startIdx < rawList.size()) {
            List<NSData> sublistView = rawList.subList(startIdx, endIdx);
            sublist = NSList.from(sublistView);
            sublistView.clear();
        } else {
            sublist = NSList.fromEmpty();
        }

        for (NSData r : replacements) {
            rawList.add(startIdx, r);
            startIdx++;
        }

        return sublist;
    }

    @BuiltinMethod(name = "unshift")
    public static NSData unshift(NSList self, @BuiltinMethodParameter(name = "value") NSData value) {
        List<NSData> rawList = self.getRawList();
        rawList.add(0, value);
        return self;
    }
}
