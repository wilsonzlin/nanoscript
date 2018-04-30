package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;

import java.util.Map;
import java.util.Set;

public class Exports {
  private final Map<String, NSData> exports;

  public Exports (Map<String, NSData> exports) {
    this.exports = exports;
  }

  public NSData get (String name) {
    return exports.get(name);
  }

  public Set<String> names () {
    return exports.keySet();
  }

  public Map<String, NSData> map () {
    return exports;
  }
}
