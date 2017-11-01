package in.wilsonl.nanoscript.Parsing;

import in.wilsonl.nanoscript.Exception.ImbalancedShuntingYardException;
import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Syntax.Expression.CallExpression;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Expression.General.BinaryExpression;
import in.wilsonl.nanoscript.Syntax.Expression.General.UnaryExpression;
import in.wilsonl.nanoscript.Syntax.Expression.LookupExpression;
import in.wilsonl.nanoscript.Syntax.Operator;
import in.wilsonl.nanoscript.Utils.Position;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import static in.wilsonl.nanoscript.Syntax.Operator.Associativity.LEFT;
import static in.wilsonl.nanoscript.Syntax.Operator.Associativity.RIGHT;

public class ShuntingYard {

    private final Deque<Expression> expressionStack = new ArrayDeque<>();
    private final Deque<OperatorContainer> operatorStack = new ArrayDeque<>();
    private UnitType lastPushedUnitType = null;

    public UnitType getLastPushedUnitType() {
        return lastPushedUnitType;
    }

    public void pushOperator(OperatorContainer op) {
        lastPushedUnitType = UnitType.OPERATOR;
        operatorStack.add(op);
    }

    public void removeLastOperator() {
        operatorStack.removeLast();
    }

    public boolean hasOperator() {
        return operatorStack.size() > 0;
    }

    public OperatorContainer peekTopOperator() {
        return operatorStack.peekLast();
    }

    public void pushExpression(Expression expr) {
        lastPushedUnitType = UnitType.EXPRESSION;
        expressionStack.add(expr);
    }

    public Expression popExpression() {
        return expressionStack.removeLast();
    }

    public int getExpressionCount() {
        return expressionStack.size();
    }

    public Expression peekTopExpression() {
        return expressionStack.peekLast();
    }

    public void processShuntingYard() {
        processShuntingYard(null);
    }

    public Expression popExpressionForOperator(OperatorContainer operator) {
        try {
            return popExpression();
        } catch (NoSuchElementException nsee) {
            throw new ImbalancedShuntingYardException(operator.getPosition());
        }
    }

    public void processShuntingYard(Operator operatorInProcessing) {
        boolean operatorInProcessingProvided = operatorInProcessing != null;

        Operator.Associativity operatorAssociativity = null;
        int operatorPrecedence = -1;

        if (operatorInProcessingProvided) {
            operatorAssociativity = operatorInProcessing.getAssociativity();
            operatorPrecedence = operatorInProcessing.getPrecedence();
        }

        while (hasOperator()) {
            OperatorContainer lastOperatorContainer = peekTopOperator();
            Operator lastOperator = lastOperatorContainer.getOperator();
            Object lastOperatorData = lastOperatorContainer.getAdditionalData();
            int lastOperatorPrecedence = lastOperator.getPrecedence();
            Operator.Arity lastOperatorArity = lastOperator.getArity();

            if (!operatorInProcessingProvided || (operatorAssociativity == LEFT && operatorPrecedence <= lastOperatorPrecedence || operatorAssociativity == RIGHT && operatorPrecedence < lastOperatorPrecedence)) {
                removeLastOperator();

                Expression result;
                Position lastOperatorPosition = lastOperatorContainer.position;

                switch (lastOperator) {
                    case LOOKUP:
                    case NULL_LOOKUP:
                        Expression source = popExpressionForOperator(lastOperatorContainer);
                        boolean isNullSafe = lastOperator == Operator.NULL_LOOKUP;
                        if (lastOperatorData instanceof LookupExpression.Terms) {
                            result = new LookupExpression(lastOperatorPosition, isNullSafe, source, (LookupExpression.Terms) lastOperatorData);
                        } else {
                            throw new InternalStateError("Operator data is not Terms or Slices");
                        }
                        break;

                    case CALL:
                    case NULL_CALL:
                        if (!(lastOperatorData instanceof CallExpression.Arguments)) {
                            throw new InternalStateError("Operator data is not Arguments");
                        }
                        Expression callee = popExpressionForOperator(lastOperatorContainer);
                        result = new CallExpression(lastOperatorPosition, lastOperator == Operator.NULL_CALL, callee, (CallExpression.Arguments) lastOperatorData);
                        break;

                    default:
                        if (lastOperatorArity == Operator.Arity.UNARY) {
                            Expression operand = popExpressionForOperator(lastOperatorContainer);
                            result = new UnaryExpression(lastOperatorPosition, lastOperator, operand);

                        } else if (lastOperatorArity == Operator.Arity.BINARY) {
                            Expression rhs = popExpressionForOperator(lastOperatorContainer);
                            Expression lhs = popExpressionForOperator(lastOperatorContainer);
                            result = new BinaryExpression(lastOperatorPosition, lhs, lastOperator, rhs);

                        } else {
                            throw new InternalStateError(String.format("Unhandled operator %s with arity %s", lastOperator, lastOperatorArity));
                        }
                }

                pushExpression(result);
            } else {
                break;
            }
        }
    }

    public void pushOperator(Operator operator, Object additionalData, Position position) {
        pushOperator(new OperatorContainer(operator, additionalData, position));
    }

    public enum UnitType {
        EXPRESSION, OPERATOR
    }

    public static class OperatorContainer {
        private final Operator operator;
        private final Object additionalData;
        private final Position position;

        private OperatorContainer(Operator operator, Object additionalData, Position position) {
            this.operator = operator;
            this.additionalData = additionalData;
            this.position = position;
        }

        public Operator getOperator() {
            return operator;
        }

        public Object getAdditionalData() {
            return additionalData;
        }

        public Position getPosition() {
            return position;
        }
    }
}
