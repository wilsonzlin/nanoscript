package in.wilsonl.nanoscript.Utils;


import in.wilsonl.nanoscript.Exception.InternalStateError;

public class SetOnce<T> {
  private final boolean isNullable;
  private final T defaultValue;
  private T value;
  private boolean hasBeenSet = false;
  private boolean hasDefaultValue = false;

  private SetOnce (boolean isNullable, boolean hasDefaultValue, T defaultValue) {
    if (hasDefaultValue && defaultValue == null && !isNullable) {
      throw new InternalStateError("Value is not nullable");
    }
    if (!hasDefaultValue && defaultValue != null) {
      throw new InternalStateError("Default value is provided");
    }
    this.isNullable = isNullable;
    this.hasDefaultValue = hasDefaultValue;
    this.defaultValue = defaultValue;
  }

  public SetOnce (boolean isNullable, T defaultValue) {
    this(isNullable, true, defaultValue);
  }

  public SetOnce (boolean isNullable) {
    this(isNullable, false, null);
  }

  public SetOnce () {
    this(false);
  }

  public T get () {
    if (!hasBeenSet) {
      if (!hasDefaultValue) {
        throw new InternalStateError("Value has not been set");
      }
      return defaultValue;
    }
    return value;
  }

  public void set (T value) {
    if (hasBeenSet) {
      throw new InternalStateError("Value has already been set");
    }
    if (!isNullable && value == null) {
      throw new InternalStateError("Value is not nullable");
    }
    this.value = value;
    hasBeenSet = true;
  }

  public boolean isSet () {
    return hasBeenSet;
  }
}
