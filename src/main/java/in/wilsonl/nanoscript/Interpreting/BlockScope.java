package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;

public class BlockScope implements Context {
    private final ContextHelper contextHelper;
    private final Type type;

    public BlockScope(Context parentContext, Type type) {
        this.contextHelper = new ContextHelper(parentContext);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public NSData<?> getContextSymbol(String name) {
        return contextHelper.getSymbol(name);
    }

    @Override
    public boolean setContextSymbol(String name, NSData<?> value) {
        return contextHelper.setSymbol(name, value);
    }

    @Override
    public void createContextSymbol(String name, NSData<?> initialValue) {
        contextHelper.createSymbol(name, initialValue);
    }

    public void clearSymbols() {
        contextHelper.clearSymbols();
    }

    public enum Type {
        CONDITIONAL_BRANCH, LOOP, FOR, TRY, CATCH, GLOBAL
    }
}
