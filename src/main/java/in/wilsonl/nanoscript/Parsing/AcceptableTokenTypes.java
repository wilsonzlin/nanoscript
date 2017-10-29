package in.wilsonl.nanoscript.Parsing;

import in.wilsonl.nanoscript.Utils.Acceptable;

public class AcceptableTokenTypes extends Acceptable<TokenType> {
    public AcceptableTokenTypes(TokenType... tokenTypes) {
        super(tokenTypes);
    }

    public boolean has(Token t) {
        return has(t.getType());
    }
}
