package in.wilsonl.nanoscript.Exception;

import in.wilsonl.nanoscript.Utils.Position;

public class ImbalancedShuntingYardException extends MalformedSyntaxException {
  public ImbalancedShuntingYardException (Position position) {
    super("Invalid expression", position);
  }
}
