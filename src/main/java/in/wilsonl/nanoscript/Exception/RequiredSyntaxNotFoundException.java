package in.wilsonl.nanoscript.Exception;

import in.wilsonl.nanoscript.Utils.Position;

public class RequiredSyntaxNotFoundException extends MalformedSyntaxException {
  public RequiredSyntaxNotFoundException (String message, Position position) {
    super(message, position);
  }
}
