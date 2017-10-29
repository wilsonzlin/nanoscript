package in.wilsonl.nanoscript.Syntax;


import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;


public class Parameter {
    private final Identifier name;

    public Parameter(Identifier name) {
        this.name = name;
    }

    public static Parameter parseParameter(Tokens tokens) {
        return new Parameter(Identifier.requireIdentifier(tokens));
    }

    public static List<Parameter> parseParametersList(Tokens tokens) {
        List<Parameter> parameters = new ROList<>();

        tokens.require(T_PARENTHESIS_LEFT);

        do {
            // Allow parameters to end with comma
            // No arguments is valid
            if (tokens.peekType() == T_PARENTHESIS_RIGHT) {
                break;
            }

            parameters.add(parseParameter(tokens));
        } while (tokens.skipIfNext(T_COMMA));

        tokens.require(T_PARENTHESIS_RIGHT);

        return parameters;
    }

    public Identifier getName() {
        return name;
    }
}
