package in.wilsonl.nanoscript.Exception;

public class UnexpectedEndOfCodeException extends SyntaxException {
  public UnexpectedEndOfCodeException () {
    super("Unexpected end of code");
  }
}
