package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.VMError.EndOfIterationError;

public abstract class NSIterator extends NSData<Object> {
    protected NSIterator() {
        super(Type.ITERATOR, null);
    }

    public abstract NSData<?> next() throws EndOfIterationError;
}
