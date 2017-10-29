package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.T_COMMA;
import static in.wilsonl.nanoscript.Parsing.TokenType.T_PARENTHESIS_RIGHT;

public class CallExpression extends Expression {
    private final boolean nullSafe;
    private final Expression callee;
    private final Arguments arguments;

    public CallExpression(boolean nullSafe, Expression callee, Arguments arguments) {
        this.callee = callee;
        this.arguments = arguments;
        this.nullSafe = nullSafe;
    }

    public static Arguments parseCallExpressionArguments(Tokens tokens) {
        Arguments arguments = new Arguments();

        do {
            // Allow function call arguments list to end with comma
            // No arguments is valid
            if (tokens.peekType() == T_PARENTHESIS_RIGHT) {
                break;
            }

            Expression arg = parseExpression(tokens, new AcceptableTokenTypes(T_COMMA, T_PARENTHESIS_RIGHT));

            arguments.pushPositionalArgument(arg);
        } while (tokens.skipIfNext(T_COMMA));

        return arguments;
    }

    public static class Arguments {
        private final List<Expression> positionalArguments = new ROList<>();

        public void pushPositionalArgument(Expression expr) {
            positionalArguments.add(expr);
        }
    }
}
