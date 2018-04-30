package in.wilsonl.nanoscript.Interpreting.Data;

import java.util.HashMap;
import java.util.Map;

public class NSMap extends NSData {
  private final Map<NSData, NSData> rawMap;

  private NSMap (Map<NSData, NSData> initialMap) {
    super(Type.MAP);
    rawMap = initialMap;
  }

  public static NSMap from (Map<NSData, NSData> initialMap) {
    return new NSMap(new HashMap<>(initialMap));
  }

  // Equality of maps is only if they are the same instance, not based on the contents

  public Map<NSData, NSData> getRawMap () {
    return rawMap;
  }

  @Override
  public NSBoolean nsToBoolean () {
    return NSBoolean.TRUE;
  }
}
