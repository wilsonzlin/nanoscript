package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;

import java.util.Map;
import java.util.TreeMap;

public class ContextHelper {
    private final Context parent;
    // <variables> can be cleared
    private final Map<String, NSData> variables = new TreeMap<>();

    public ContextHelper(Context parent) {
        this.parent = parent;
    }

    public ContextHelper() {
        this(null);
    }

    public NSData getSymbol(String name) {
        NSData nsData = variables.get(name);
        if (nsData == null && parent != null) {
            nsData = parent.getContextSymbol(name);
        }
        return nsData;
    }

    public boolean setSymbol(String name, NSData value) {
        if (!variables.containsKey(name)) {
            return parent != null && parent.setContextSymbol(name, value);
        }
        variables.put(name, value);
        return true;
    }

    public void createSymbol(String name, NSData initialValue) {
        if (variables.containsKey(name)) {
            throw VMError.from(BuiltinClass.ReferenceError, String.format("Variable `%s` already exists", name));
        }
        variables.put(name, initialValue);
    }

    public void clearSymbols() {
        variables.clear();
    }

    public boolean hasSymbol(String name) {
        return variables.containsKey(name);
    }
}
