package in.wilsonl.nanoscript.Syntax;


import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;


public class Parameter {
    private final Variable variable;
    private final boolean variableLength;

    public Parameter(Variable variable, boolean variableLength) {
        if (variable.hasInitialiser()) {
            throw new IllegalArgumentException("Parameter variable cannot have initialiser");
        }
        this.variable = variable;
        this.variableLength = variableLength;
    }

    public Parameter(Variable variable) {
        this(variable, false);
    }

    public static Parameter parseParameter(Tokens tokens) {
        Variable variable = new Variable();

        Identifier name;
        boolean isVarArg;

        isVarArg = tokens.skipIfNext(T_ELLIPSIS);
        name = Identifier.requireIdentifier(tokens);

        variable.setName(name);

        return new Parameter(variable, isVarArg);
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
}
