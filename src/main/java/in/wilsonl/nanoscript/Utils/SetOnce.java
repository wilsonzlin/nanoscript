package in.wilsonl.nanoscript.Utils;


import in.wilsonl.nanoscript.Exception.InternalStateError;

public class SetOnce<T> {
    private final boolean isNullable;
    private T value;
    private boolean hasBeenSet = false;

    public SetOnce(boolean isNullable) {
        this.isNullable = isNullable;
    }

    public SetOnce() {
        this(false);
    }

    public T get() {
        if (!hasBeenSet) {
            throw new InternalStateError("Value has not been set");
        }
        return value;
    }

    public void set(T value) {
        if (hasBeenSet) {
            throw new InternalStateError("Value has already been set");
        }
        if (!isNullable && value == null) {
            throw new InternalStateError("Value is not nullable");
        }
        this.value = value;
        hasBeenSet = true;
    }

    public boolean isSet() {
        return hasBeenSet;
    }
}
