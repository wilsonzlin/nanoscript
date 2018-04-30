package in.wilsonl.nanoscript.Interpreting.Data;

public class NSNativeClassInstanceVariable {
  private final String name;
  private final NSData initialValue;

  public NSNativeClassInstanceVariable (String name, NSData initialValue) {
    this.name = name;
    this.initialValue = initialValue;
  }

  public String getName () {
    return name;
  }

  public NSData getInitialValue () {
    return initialValue;
  }
}
