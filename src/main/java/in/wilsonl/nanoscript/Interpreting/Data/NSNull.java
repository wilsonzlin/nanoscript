package in.wilsonl.nanoscript.Interpreting.Data;

public class NSNull extends NSData {
  public static final NSNull NULL = new NSNull();
  private static final NSString stringRepresentation = NSString.from("null");

  private NSNull () {
    super(Type.NULL);
  }

  @Override
  public boolean nsIsNotNull () {
    return false;
  }

  // Don't need hashCode or equals, as the default implements are correct as long
  // as only <NULL> is used

  @Override
  public NSBoolean nsToBoolean () {
    return NSBoolean.FALSE;
  }

  @Override
  public NSString nsToString () {
    return stringRepresentation;
  }
}
