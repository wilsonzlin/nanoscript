package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.VMError;

import static in.wilsonl.nanoscript.Utils.Utils.compare;
import static in.wilsonl.nanoscript.Utils.Utils.isInt;

// REMEMBER: Numbers are immutable; return a new one when changing
public class NSNumber extends NSData {
  private final double rawNumber;
  private final long asInt;
  private final boolean isInt;

  private NSNumber (double value) {
    super(Type.NUMBER);
    rawNumber = value;
    isInt = isInt(value);
    if (isInt) {
      asInt = (long) value;
    } else {
      asInt = 0;
    }
  }

  private NSNumber (long value) {
    super(Type.NUMBER);
    rawNumber = value;
    asInt = value;
    isInt = true;
  }

  public static NSNumber from (long value) {
    return new NSNumber(value);
  }

  public static NSNumber from (double value) {
    return new NSNumber(value);
  }

  public static NSNumber from (Number value) {
    return new NSNumber(value.doubleValue());
  }

  public long toInt () {
    if (!isInt) {
      throw new NumberFormatException("Number is not an integer");
    }
    return asInt;
  }

  public double getRawNumber () {
    return rawNumber;
  }

  @Override
  public int hashCode () {
    return Double.hashCode(rawNumber);
  }

  @Override
  public boolean equals (Object o) {
    return o instanceof NSNumber && rawNumber == ((NSNumber) o).rawNumber;
  }

  @Override
  public NSNumber nsCompare (NSData other) {
    if (other.getType() != Type.NUMBER) {
      throw VMError.from(BuiltinClass.TypeError, "Attempted to compare non-number to number");
    }

    double otherNumber = ((NSNumber) other).rawNumber;

    return NSNumber.from(compare(rawNumber, otherNumber));
  }

  @Override
  public NSData nsAdd (NSData other) {
    if (other.getType() != Type.NUMBER) {
      throw VMError.from(BuiltinClass.TypeError, "Attempted to add non-number to number");
    }

    return NSNumber.from(rawNumber + ((NSNumber) other).rawNumber);
  }

  @Override
  public NSData nsSubtract (NSData other) {
    if (other.getType() != Type.NUMBER) {
      throw VMError.from(BuiltinClass.TypeError, "Attempted to subtract non-number to number");
    }

    return NSNumber.from(rawNumber - ((NSNumber) other).rawNumber);
  }

  @Override
  public NSData nsMultiply (NSData other) {
    if (other.getType() != Type.NUMBER) {
      throw VMError.from(BuiltinClass.TypeError, "Attempted to multiply non-number to number");
    }

    return NSNumber.from(rawNumber * ((NSNumber) other).rawNumber);
  }

  @Override
  public NSData nsDivide (NSData other) {
    if (other.getType() != Type.NUMBER) {
      throw VMError.from(BuiltinClass.TypeError, "Attempted to divide non-number to number");
    }

    return NSNumber.from(rawNumber / ((NSNumber) other).rawNumber);
  }

  @Override
  public NSData nsExponentiate (NSData other) {
    if (other.getType() != Type.NUMBER) {
      throw VMError.from(BuiltinClass.TypeError, "Attempted to exponentiate non-number to number");
    }

    return NSNumber.from(Math.pow(rawNumber, ((NSNumber) other).rawNumber));
  }

  @Override
  public NSData nsModulo (NSData other) {
    if (other.getType() != Type.NUMBER) {
      throw VMError.from(BuiltinClass.TypeError, "Attempted to modulo non-number to number");
    }

    return NSNumber.from(rawNumber % ((NSNumber) other).rawNumber);
  }

  @Override
  public NSData nsAccess (String member) {
    // TODO
    throw VMError.from(BuiltinClass.UnsupportedOperationError, "Invalid operation on a number");
  }

  @Override
  public NSBoolean nsToBoolean () {
    return NSBoolean.TRUE;
  }

  @Override
  public NSString nsToString () {
    if (isInt) {
      return NSString.from(Long.toString(asInt));
    }
    return NSString.from(Double.toString(rawNumber));
  }
}
