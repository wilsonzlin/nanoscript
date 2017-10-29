package in.wilsonl.nanoscript.Exception;

import in.wilsonl.nanoscript.Utils.Position;

public class MalformedSyntaxException extends SyntaxException {
    public MalformedSyntaxException(String message, Position position) {
        super(message, position);
    }
}
