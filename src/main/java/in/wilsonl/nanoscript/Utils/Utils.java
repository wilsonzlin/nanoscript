package in.wilsonl.nanoscript.Utils;

import in.wilsonl.nanoscript.Parsing.Tokens;

import java.util.Collection;
import java.util.Iterator;

public class Utils {
  private Utils () {
  }

  private static int normaliseComparisonResult (int res) {
    return Integer.compare(res, 0);
  }

  public static int compare (String a, String b) {
    return normaliseComparisonResult(a.compareTo(b));
  }

  public static int compare (double a, double b) {
    return normaliseComparisonResult(Double.compare(a, b));
  }

  public static int compare (int a, int b) {
    return Integer.compare(a, b);
  }

  public static boolean isInt (double v) {
    return v == Math.floor(v) && !Double.isInfinite(v);
  }

  public static String join (String delimiter, Collection<?> collection) {
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

  public static String join (String delimiter, Object[] array, int offset, int before) {
    StringBuilder str = new StringBuilder();
    if (before < 0) {
      before = array.length + before;
    }
    for (int i = offset; i < before; i++) {
      str.append(array[i].toString());
      if (i < before - 1) {
        str.append(delimiter);
      }
    }
    return str.toString();
  }

  public static String join (String delimiter, Object[] array) {
    return join(delimiter, array, 0, array.length);
  }

  public static <E extends Enum<E>> E requireValueInEnum (Class<E> e, String v, Tokens tokens) {
    try {
      return Enum.valueOf(e, v);
    } catch (IllegalArgumentException iae) {
      throw tokens.constructMalformedSyntaxException(String.format("Required a valid %s, got `%s`", e.getSimpleName(), v));
    }
  }
}
