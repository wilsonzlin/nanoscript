package in.wilsonl.nanoscript.Interpreting.Builtin;

import in.wilsonl.nanoscript.Interpreting.Arguments.NSArgument;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;
import in.wilsonl.nanoscript.Interpreting.VMError;

import java.util.Collections;
import java.util.List;

import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.*;
import static in.wilsonl.nanoscript.Interpreting.Data.NSData.Type.*;

@BuiltinMethods(target = NSList.class)
public class BuiltinNSListMethods {
    /**
     * Sets the value of every index in the list to {@code filler}.
     * @param filler the value to use.
     * @return the NSList instance.
     */
    @BuiltinMethod(name = "fill")
    public static NSData fill(NSList self, @BuiltinMethodParameter(name = "filler") NSData filler) {
        List<NSData> rawList = self.getRawList();
        for (int i = 0; i < rawList.size(); i++) {
            rawList.set(i, filler);
        }
        return self;
    }

    /**
     * Removes the last value in the list.
     * @return the removed value.
     * @throws NoSuchElementError if the list is empty.
     */
    @BuiltinMethod(name = "pop")
    public static NSData pop(NSList self) {
        List<NSData> rawList = self.getRawList();
        try {
            return rawList.remove(rawList.size() - 1);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            throw VMError.from(NoSuchElementError, "List is empty");
        }
    }

    /**
     * Adds a value to the end of the list.
     * @param value the value to add.
     * @return the NSList instance.
     */
    @BuiltinMethod(name = "push")
    public static NSData push(NSList self, @BuiltinMethodParameter(name = "value") NSData value) {
        List<NSData> rawList = self.getRawList();
        rawList.add(value);
        return self;
    }

    /**
     * Reverses the list in-place.
     * This modifies the list; it does not return a new instance.
     * @return the NSList instance.
     */
    @BuiltinMethod(name = "reverse")
    public static NSData reverse(NSList self) {
        List<NSData> rawList = self.getRawList();
        Collections.reverse(rawList);
        return self;
    }

    /**
     * Removes a value at a specific index.
     * @param index the position of the value to remove from the list.
     * @return the removed value.
     * @throws KeyError if {@code index} is not an integer.
     * @throws OutOfBoundsError if {@code index} is out of bounds.
     */
    @BuiltinMethod(name = "removeAt")
    public static NSData removeAt(NSList self, @BuiltinMethodParameter(name = "index", types = {NUMBER}) NSData index) {
        List<NSData> rawList = self.getRawList();
        int rawIdx = self.getValidIndex(index, false);
        return rawList.remove(rawIdx);
    }

    /**
     * Removes the first value in the list.
     * @return the removed value.
     * @throws NoSuchElementError if the list is empty.
     */
    @BuiltinMethod(name = "shift")
    public static NSData shift(NSList self) {
        List<NSData> rawList = self.getRawList();
        try {
            return rawList.remove(0);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            throw VMError.from(NoSuchElementError, "List is empty");
        }
    }

    /**
     * Gets the amount of values in the list.
     * @return how many values are in the list.
     */
    @BuiltinMethod(name = "size")
    public static NSData size(NSList self) {
        List<NSData> rawList = self.getRawList();
        return NSNumber.from(rawList.size());
    }

    /**
     * Sorts the list in-place, using zero or more comparators.
     * If no comparators are provided, the spaceship operator will be used
     * to compare values.
     * If more than one comparator is provided, for each comparison, the
     * comparators are called in order until one returns a non-zero value; this
     * essentially allows for multiple sort keys.
     * This modifies the list; it does not return a new instance.
     * @param comparators zero or more functions that take two values ({@code a}
     *                    and {@code b}) and returns a number that is less than 0
     *                    if {@code a} < {@code b}, zero if they are equal, or
     *                    greater than zero if {@code a} > {@code b}.
     * @return the NSList instance.
     * @throws TypeError if any provided comparator function returns a non-number.
     * @throws UnsupportedOperationError if no comparators were provided and two
     *                                   values in the list cannot be compared.
     */
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
                    // Don't cast to int or floor/round; 0.3 and 0.1 are not equal and comparator may use (0.3 - 0.1)
                    double rawRes = ((NSNumber) res).getRawNumber();
                    if (rawRes != 0) {
                        return rawRes < 0 ? -1 : 1;
                    }
                }
                return 0;
            });
        } else {
            // VMError exception will be thrown if can't be compared
            rawList.sort((a, b) -> (int) a.nsCompare(b).toInt());
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

    /**
     * Adds a value to the beginning of the list.
     * All existing values in the list will have their index incremented by one,
     * then the new value will take the index {@code 0}.
     * @param value the value to add.
     * @return the NSList instance.
     */
    @BuiltinMethod(name = "unshift")
    public static NSData unshift(NSList self, @BuiltinMethodParameter(name = "value") NSData value) {
        List<NSData> rawList = self.getRawList();
        rawList.add(0, value);
        return self;
    }
}
