package in.wilsonl.nanoscript.Utils;

import in.wilsonl.nanoscript.Parsing.Tokens;

import java.util.Collection;
import java.util.Iterator;

public class Utils {
    private Utils() {
    }

    public static String join(String delimiter, Collection<?> collection) {
        StringBuilder str = new StringBuilder();
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            str.append(it.next().toString());
            if (it.hasNext()) {
                str.append(delimiter);
            }
        }
        return str.toString();
    }

    public static String join(String delimiter, String[] array, int offset, int before) {
        StringBuilder str = new StringBuilder();
        if (before < 0) {
            before = array.length + before;
        }
        for (int i = offset; i < before; i++) {
            str.append(array[i]);
            if (i < before - 1) {
                str.append(delimiter);
            }
        }
        return str.toString();
    }

    public static String join(String delimiter, String[] array) {
        return join(delimiter, array, 0, array.length);
    }

    public static <E extends Enum<E>> E requireValueInEnum(Class<E> e, String v, Tokens tokens) {
        try {
            return Enum.valueOf(e, v);
        } catch (IllegalArgumentException iae) {
            throw tokens.constructMalformedSyntaxException(String.format("Required a valid %s, got `%s`", e.getSimpleName(), v));
        }
    }
}
