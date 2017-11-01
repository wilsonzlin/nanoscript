package in.wilsonl.nanoscript.Syntax;


import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;


public class Parameter {
    private final Identifier name;
    private final boolean optional;
    private final boolean variableLength;

    public Parameter(Identifier name, boolean optional, boolean variableLength) {
        this.name = name;
        this.optional = optional;
        this.variableLength = variableLength;
    }

    public static Parameter parseParameter(Tokens tokens) {
        boolean optional = tokens.skipIfNext(T_KEYWORD_OPTIONAL);
        boolean variableLength = tokens.skipIfNext(T_ELLIPSIS);
        Identifier name = Identifier.requireIdentifier(tokens);
        return new Parameter(name, optional, variableLength);
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

    public boolean isOptional() {
        return optional;
    }

    public boolean isVariableLength() {
        return variableLength;
    }
}
