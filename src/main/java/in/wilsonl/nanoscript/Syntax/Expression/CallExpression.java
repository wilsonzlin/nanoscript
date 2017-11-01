package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class CallExpression extends Expression {
    private final boolean nullSafe;
    private final Expression callee;
    private final Arguments arguments;

    public CallExpression(Position position, boolean nullSafe, Expression callee, Arguments arguments) {
        super(position);
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

            boolean optional = tokens.skipIfNext(T_KEYWORD_OPTIONAL);

            Expression value = parseExpression(tokens, new AcceptableTokenTypes(T_COMMA, T_PARENTHESIS_RIGHT));

            arguments.pushArgument(new Argument(optional, value));
        } while (tokens.skipIfNext(T_COMMA));

        return arguments;
    }

    public boolean isNullSafe() {
        return nullSafe;
    }

    public Expression getCallee() {
        return callee;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public static class Argument {
        private final boolean optional;
        private final Expression value;

        public Argument(boolean optional, Expression value) {
            this.optional = optional;
            this.value = value;
        }

        public boolean isOptional() {
            return optional;
        }

        public Expression getValue() {
            return value;
        }
    }

    public static class Arguments {
        private final List<Argument> arguments = new ROList<>();

        public List<Argument> getArguments() {
            return arguments;
        }

        public void pushArgument(Argument expr) {
            arguments.add(expr);
        }
    }
}
