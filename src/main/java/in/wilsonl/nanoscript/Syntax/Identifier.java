package in.wilsonl.nanoscript.Syntax;

import in.wilsonl.nanoscript.Parsing.Token;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;

public class Identifier {
    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    public static Identifier requireIdentifier(Tokens tokens) {
        String value = tokens.require(TokenType.T_IDENTIFIER).getValue();
        return new Identifier(value);
    }

    public static Identifier acceptIdentifier(Tokens tokens) {
        Token token = tokens.acceptOptional(TokenType.T_IDENTIFIER);
        if (token == null) {
            return null;
        }
        return new Identifier(token.getValue());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
