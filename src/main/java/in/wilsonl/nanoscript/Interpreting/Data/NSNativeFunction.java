package in.wilsonl.nanoscript.Interpreting.Data;

import java.util.List;

public abstract class NSNativeFunction extends NSData<Object> {
    protected NSNativeFunction() {
        super(Type.NATIVE_FUNCTION, null);
    }

    @Override
    public abstract NSData<?> applyCall(List<NSData<?>> arguments);
}
