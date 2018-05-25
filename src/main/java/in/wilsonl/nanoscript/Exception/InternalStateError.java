package in.wilsonl.nanoscript.Exception;

public class InternalStateError extends Error {
  public InternalStateError (String message) {
    super(String.format(
      "\n==============================================\n" +
      "PLEASE REPORT THIS ERROR:\n\n" +
      "%s\n\n" +
      "This is an internal error that should not have happened.\n" +
      "Please report this error, along with its stack trace, to nanoscript developers.",
      message));
  }
}
