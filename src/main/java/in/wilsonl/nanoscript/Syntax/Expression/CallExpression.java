package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

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

        boolean finishedPositionalArgs = false;

        do {
            // Allow function call arguments list to end with comma
            // No arguments is valid
            // <delimiter> could be T_BRACE_RIGHT if implicit instantiation
            if (tokens.peekType() == T_PARENTHESIS_RIGHT) {
                break;
            }

            Expression arg = parseExpression(tokens, new AcceptableTokenTypes(T_COMMA, T_ASSIGNMENT, T_PARENTHESIS_RIGHT));

            if (tokens.skipIfNext(T_ASSIGNMENT)) {
                if (!(arg instanceof IdentifierExpression)) {
                    throw tokens.constructMalformedSyntaxException("Keyword argument is not an identifier");
                }
                finishedPositionalArgs = true;
                Expression value = parseExpression(tokens, new AcceptableTokenTypes(T_COMMA, T_PARENTHESIS_RIGHT));
                arguments.setKeywordArgument(((IdentifierExpression) arg).getIdentifier(), value);
            } else {
                if (finishedPositionalArgs) {
                    throw tokens.constructMalformedSyntaxException("Positional argument after keyword arguments");
                }
                arguments.pushPositionalArgument(arg);
            }
        } while (tokens.skipIfNext(T_COMMA));

        return arguments;
    }

    public static class Arguments {
        private final List<Expression> positionalArguments = new ROList<>();
        private final Map<Identifier, Expression> keywordArguments = new HashMap<>();

        public void pushPositionalArgument(Expression expr) {
            positionalArguments.add(expr);
        }

        public void setKeywordArgument(Identifier id, Expression expr) {
            if (keywordArguments.containsKey(id)) {
                throw new IllegalStateException("Duplicate keyword argument");
            }
            keywordArguments.put(id, expr);
        }
    }
}
