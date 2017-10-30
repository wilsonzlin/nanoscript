package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.VMError.ExportError;

import java.util.Map;
import java.util.TreeMap;

public class GlobalScope extends BlockScope {
    private final Map<String, NSData<?>> exports = new TreeMap<>();

    public GlobalScope() {
        super(null, Type.GLOBAL);
    }

    public void addExport(String name, NSData<?> value) {
        if (exports.containsKey(name)) {
            throw new ExportError(String.format("An export called `%s` already exists", name));
        }
        exports.put(name, value);
    }
}
