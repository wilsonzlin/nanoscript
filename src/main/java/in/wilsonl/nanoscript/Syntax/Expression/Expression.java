package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Exception.UnexpectedEndOfCodeException;
import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.ShuntingYard;
import in.wilsonl.nanoscript.Parsing.Token;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralExpression;
import in.wilsonl.nanoscript.Syntax.Operator;

import static in.wilsonl.nanoscript.Parsing.ShuntingYard.UnitType.EXPRESSION;
import static in.wilsonl.nanoscript.Parsing.ShuntingYard.UnitType.OPERATOR;
import static in.wilsonl.nanoscript.Parsing.TokenType.*;
import static in.wilsonl.nanoscript.Syntax.Operator.Arity.UNARY;
import static in.wilsonl.nanoscript.Syntax.Operator.Associativity.LEFT;
import static in.wilsonl.nanoscript.Syntax.Operator.Associativity.RIGHT;

public abstract class Expression {

    public static Expression parseExpression(Tokens tokens, AcceptableTokenTypes breakOn) {
        // Initialise shunting yard
        ShuntingYard yard = new ShuntingYard();

        boolean done = false;

        while (!done) {
            Token nextToken;
            try {
                nextToken = tokens.peek();
            } catch (UnexpectedEndOfCodeException ueoce) {
                break;
            }
            TokenType nextTokenType = nextToken.getType();
            Operator nextTokenAsOperator = Operator.isOperatorToken(nextTokenType) ? Operator.parseOperator(tokens) : null;
            // Last pushed unit type could also be null
            ShuntingYard.UnitType lastPushedUnitType = yard.getLastPushedUnitType();
            Operator lastPushedAsOperator = lastPushedUnitType == OPERATOR ? yard.peekTopOperator().getOperator() : null;
            if (nextTokenAsOperator == Operator.CALL && lastPushedUnitType != EXPRESSION) {
                nextTokenAsOperator = null;
            }

            /*
             *
             *   If the last pushed unit was an expression or a unary left-associative operator,
             *   and the next unit is not a unary left-associative or binary operator, the
             *   expression has ended.
             *
             */
            if ((nextTokenAsOperator == null || (nextTokenAsOperator.getArity() == UNARY && nextTokenAsOperator.getAssociativity() == RIGHT)) &&
                    (lastPushedUnitType == EXPRESSION || (lastPushedAsOperator != null && lastPushedAsOperator.getArity() == UNARY && lastPushedAsOperator.getAssociativity() == LEFT))) {
                if (breakOn != null && !breakOn.has(nextTokenType)) {
                    throw tokens.constructMalformedSyntaxException("Unexpected " + nextTokenType);
                }
                done = true;
            } else {
                if (nextTokenAsOperator != null) {
                    Object additionalData;

                    switch (nextTokenAsOperator) {
                        case LOOKUP:
                        case NULL_LOOKUP:
                            additionalData = LookupExpression.parseLookupExpressionTerms(tokens);
                            tokens.require(T_SQUARE_BRACKET_RIGHT);
                            break;

                        case CALL:
                        case NULL_CALL:
                            additionalData = CallExpression.parseCallExpressionArguments(tokens);
                            tokens.require(T_PARENTHESIS_RIGHT);
                            break;

                        default:
                            additionalData = null;
                    }
                    yard.processShuntingYard(nextTokenAsOperator);
                    yard.pushOperator(nextTokenAsOperator, additionalData, nextToken.getPosition());
                } else {
                    switch (nextTokenType) {
                        case T_BRACE_LEFT:
                            AnonymousObjectExpression anon = AnonymousObjectExpression.parseAnonymousObjectExpression(tokens);
                            yard.pushExpression(anon);
                            break;

                        case T_PARENTHESIS_LEFT:
                            // Grouping
                            tokens.require(T_PARENTHESIS_LEFT);
                            Expression nested = parseExpression(tokens, new AcceptableTokenTypes(T_PARENTHESIS_RIGHT));
                            tokens.require(T_PARENTHESIS_RIGHT);
                            yard.pushExpression(nested);
                            break;

                        case T_KEYWORD_FUNCTION:
                            LambdaExpression lambdaExpression = LambdaExpression.parseLambdaExpression(tokens);
                            yard.pushExpression(lambdaExpression);
                            break;

                        case T_KEYWORD_IF:
                            ConditionalBranchesExpression ifExpression = ConditionalBranchesExpression.parseConditionalBranchesExpression(tokens);
                            yard.pushExpression(ifExpression);
                            break;

                        case T_IDENTIFIER:
                            IdentifierExpression identifier = IdentifierExpression.parseIdentifierExpression(tokens);
                            yard.pushExpression(identifier);
                            break;

                        default:
                            if (LiteralExpression.PARSERS.containsKey(nextTokenType)) {
                                LiteralExpression literalExpression = LiteralExpression.PARSERS.get(nextTokenType).apply(tokens);
                                yard.pushExpression(literalExpression);
                            } else {
                                throw tokens.constructMalformedSyntaxException("Unexpected " + nextTokenType);
                            }
                    }
                }
            }
        }

        yard.processShuntingYard();

        if (yard.getExpressionCount() != 1) {
            throw tokens.constructMalformedSyntaxException("Malformed expression; mismatched parts");
        }

        return yard.popExpression();
    }

    public static Expression parseExpression(Tokens tokens) {
        return parseExpression(tokens, null);
    }

}
