package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GlobalScope extends BlockScope {
  private final Map<String, NSData> exports = new TreeMap<>();

  public GlobalScope () {
    super(null, Type.GLOBAL);
  }

  public void addExport (String name, NSData value) {
    if (exports.containsKey(name)) {
      throw VMError.from(BuiltinClass.ReferenceError, String.format("An export called `%s` already exists", name));
    }
    exports.put(name, value);
  }

  public Exports consumeExports () {
    Map<String, NSData> rv = new HashMap<>(exports);
    exports.clear();
    return new Exports(rv);
  }
}
