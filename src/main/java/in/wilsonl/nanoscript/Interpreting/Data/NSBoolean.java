package in.wilsonl.nanoscript.Interpreting.Data;

public class NSBoolean extends NSData {
  public static final NSBoolean TRUE = new NSBoolean(true);
  public static final NSBoolean FALSE = new NSBoolean(false);
  private final boolean rawBoolean;
  private final NSString stringValue;

  // Don't use constructor, use NSBoolean.from or <TRUE>/<FALSE>
  private NSBoolean (boolean value) {
    super(Type.BOOLEAN);
    this.rawBoolean = value;
    this.stringValue = NSString.from(value ?
      "true" :
      "false");
  }

  public static NSBoolean from (boolean b) {
    return b ?
      TRUE :
      FALSE;
  }

  public NSBoolean invert () {
    return from(!rawBoolean);
  }

  public boolean isTrue () {
    return rawBoolean;
  }

  // Don't need hashCode or equals, as the default implements are correct as long
  // as only <TRUE> and <FALSE> are used

  @Override
  public NSBoolean nsToBoolean () {
    return this;
  }

  @Override
  public NSString nsToString () {
    return stringValue;
  }
}
