package in.wilsonl.nanoscript.Syntax;

import in.wilsonl.nanoscript.Parsing.Token;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;

public class Identifier {
    private final String name;
    private final Position position;

    public Identifier(Position position, String name) {
        this.position = position;
        this.name = name;
    }

    public static Identifier requireIdentifier(Tokens tokens) {
        Token value = tokens.require(TokenType.T_IDENTIFIER);
        return new Identifier(value.getPosition(), value.getValue());
    }

    public static Identifier acceptIdentifier(Tokens tokens) {
        Token token = tokens.acceptOptional(TokenType.T_IDENTIFIER);
        if (token == null) {
            return null;
        }
        return new Identifier(token.getPosition(), token.getValue());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Identifier && getName().equals(((Identifier) o).getName());
    }

    public Position getPosition() {
        return position;
    }
}
