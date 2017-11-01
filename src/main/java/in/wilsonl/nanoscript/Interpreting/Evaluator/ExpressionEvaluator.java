package in.wilsonl.nanoscript.Interpreting.Evaluator;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSArgument;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.Data.NSBoolean;
import in.wilsonl.nanoscript.Interpreting.Data.NSClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;
import in.wilsonl.nanoscript.Interpreting.Data.NSObject;
import in.wilsonl.nanoscript.Interpreting.Data.NSString;
import in.wilsonl.nanoscript.Interpreting.Data.NSVirtualCallable;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Syntax.Expression.AnonymousObjectExpression;
import in.wilsonl.nanoscript.Syntax.Expression.AnonymousObjectExpression.Member;
import in.wilsonl.nanoscript.Syntax.Expression.CallExpression;
import in.wilsonl.nanoscript.Syntax.Expression.ConditionalBranchesExpression;
import in.wilsonl.nanoscript.Syntax.Expression.ConditionalBranchesExpression.Branch;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Expression.General.BinaryExpression;
import in.wilsonl.nanoscript.Syntax.Expression.General.UnaryExpression;
import in.wilsonl.nanoscript.Syntax.Expression.IdentifierExpression;
import in.wilsonl.nanoscript.Syntax.Expression.LambdaExpression;
import in.wilsonl.nanoscript.Syntax.Expression.ListExpression;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralBooleanExpression;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralNullExpression;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralNumberExpression;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralStringExpression;
import in.wilsonl.nanoscript.Syntax.Expression.LookupExpression;
import in.wilsonl.nanoscript.Syntax.Expression.SelfExpression;
import in.wilsonl.nanoscript.Syntax.Operator;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionEvaluator {
    private static final Map<String, NSData.Type> SPECIAL_INSTANCEOF_TARGETS = _createSpecialInstanceOfTargets();

    private static Map<String, NSData.Type> _createSpecialInstanceOfTargets() {
        Map<String, NSData.Type> map = new HashMap<>();

        map.put("Boolean", NSData.Type.BOOLEAN);
        map.put("Callable", NSData.Type.CALLABLE);
        map.put("Class", NSData.Type.CLASS);
        map.put("Iterator", NSData.Type.ITERATOR);
        map.put("List", NSData.Type.LIST);
        map.put("Number", NSData.Type.NUMBER);
        map.put("Object", NSData.Type.OBJECT);
        map.put("String", NSData.Type.STRING);

        return map;
    }

    // Helper function
    public static boolean evaluateTypeOfExpression(Context context, NSData value, Expression st_type) {
        if (st_type instanceof IdentifierExpression) {
            String id = ((IdentifierExpression) st_type).getIdentifier().getName();
            NSData.Type valueType = value.getType();
            if (SPECIAL_INSTANCEOF_TARGETS.containsKey(id)) {
                NSData.Type targetType = SPECIAL_INSTANCEOF_TARGETS.get(id);
                return valueType == targetType;
            }
        }

        if (value.getType() != NSData.Type.OBJECT) {
            return false;
        }

        NSData targetValue = evaluateExpression(context, st_type);
        if (targetValue.getType() != NSData.Type.CLASS) {
            throw VMError.from(BuiltinClass.TypeError, "RHS of type check is not a class");
        }
        return ((NSObject) value).isInstanceOf((NSClass) targetValue).isTrue();
    }

    // Helper function
    private static List<NSData> evaluateListOfExpressions(Context context, List<Expression> expressions) {
        List<NSData> evaluated = new ROList<>();

        for (Expression a : expressions) {
            evaluated.add(evaluateExpression(context, a));
        }

        return evaluated;
    }

    public static NSData evaluateExpression(Context context, Expression expression) {
        try {
            if (expression instanceof LambdaExpression) {
                return evaluateLambdaExpression(context, (LambdaExpression) expression);

            } else if (expression instanceof AnonymousObjectExpression) {
                return evaluateAnonymousObjectExpression(context, (AnonymousObjectExpression) expression);

            } else if (expression instanceof CallExpression) {
                return evaluateCallExpression(context, (CallExpression) expression);

            } else if (expression instanceof LookupExpression) {
                return evaluateLookupExpression(context, (LookupExpression) expression);

            } else if (expression instanceof ListExpression) {
                return evaluateListExpression(context, (ListExpression) expression);

            } else if (expression instanceof IdentifierExpression) {
                return evaluateIdentifierExpression(context, (IdentifierExpression) expression);

            } else if (expression instanceof LiteralBooleanExpression) {
                return evaluateLiteralBooleanExpression((LiteralBooleanExpression) expression);

            } else if (expression instanceof LiteralStringExpression) {
                return evaluateLiteralStringExpression((LiteralStringExpression) expression);

            } else if (expression instanceof LiteralNullExpression) {
                return evaluateLiteralNullExpression();

            } else if (expression instanceof LiteralNumberExpression) {
                return evaluateLiteralNumberExpression((LiteralNumberExpression) expression);

            } else if (expression instanceof BinaryExpression) {
                return evaluateBinaryExpression(context, (BinaryExpression) expression);

            } else if (expression instanceof UnaryExpression) {
                return evaluateUnaryExpression(context, (UnaryExpression) expression);

            } else if (expression instanceof SelfExpression) {
                return evaluateSelfExpression(context);

            } else if (expression instanceof ConditionalBranchesExpression) {
                return evaluateConditionalBranchesExpression(context, (ConditionalBranchesExpression) expression);

            } else {
                throw new InternalStateError("Unknown expression type");
            }
        } catch (VMError vme) {
            if (vme.hasPosition()) {
                throw vme;
            }
            throw new VMError(vme.getValue(), expression.getPosition());
        }
    }

    private static NSData evaluateListExpression(Context context, ListExpression expression) {
        List<NSData> values = new ROList<>(expression.getValues().size());
        for (Expression st_expr : expression.getValues()) {
            values.add(evaluateExpression(context, st_expr));
        }
        return NSList.from(values);
    }

    private static NSData evaluateAssignmentOrUpdateExpression(Context context, BinaryExpression binaryExpression) {
        Expression st_lhs = binaryExpression.getLHS();
        Expression st_rhs = binaryExpression.getRHS();
        NSData value;

        if (st_lhs instanceof LookupExpression) {
            LookupExpression st_source = (LookupExpression) st_lhs;
            NSData source = evaluateExpression(context, st_source.getSource());
            value = evaluateExpression(context, st_rhs);
            List<NSData> terms = evaluateListOfExpressions(context, st_source.getTerms().getTerms());
            source.nsUpdate(terms, value);
        } else if (st_lhs instanceof BinaryExpression && ((BinaryExpression) st_lhs).getOperator() == Operator.ACCESSOR) {
            Expression st_source = ((BinaryExpression) st_lhs).getLHS();
            Expression st_member = ((BinaryExpression) st_lhs).getRHS();
            if (!(st_member instanceof IdentifierExpression)) {
                throw VMError.from(BuiltinClass.SyntaxError, "Invalid member assignment");
            }
            NSData source = evaluateExpression(context, st_source);
            String member = ((IdentifierExpression) st_member).getIdentifier().getName();
            value = evaluateExpression(context, st_rhs);
            source.nsAssign(member, value);
        } else if (st_lhs instanceof IdentifierExpression) {
            String symbol = ((IdentifierExpression) st_lhs).getIdentifier().getName();
            value = evaluateExpression(context, st_rhs);
            if (!context.setContextSymbol(symbol, value)) {
                throw VMError.from(BuiltinClass.ReferenceError, String.format("The variable `%s` does not exist", symbol));
            }
        } else {
            throw VMError.from(BuiltinClass.SyntaxError, "Invalid assignment LHS");
        }

        return value;
    }

    private static NSData evaluateConditionalBranchesExpression(Context context, ConditionalBranchesExpression expression) {
        for (Branch b : expression.getConditionalBranches()) {
            NSData condition = evaluateExpression(context, b.getCondition());
            NSBoolean passed = condition.nsToBoolean();
            if (passed == NSBoolean.TRUE) {
                return evaluateExpression(context, b.getValue());
            }
        }
        return evaluateExpression(context, expression.getFinalBranchValue());
    }

    private static NSData evaluateSelfExpression(Context context) {
        NSData value = context.getContextSymbol("self");
        if (value == null) {
            throw VMError.from(BuiltinClass.ReferenceError, "`self` is not available in this context");
        }
        return value;
    }

    private static NSData evaluateUnaryExpression(Context context, UnaryExpression expression) {
        Operator operator = expression.getOperator();
        NSData operand = evaluateExpression(context, expression.getOperand());

        switch (operator) {
            case NOT:
                return operand.nsToBoolean().invert();

            case HASH:
                return operand.nsApplyHashOperator();

            default:
                throw new InternalStateError("Unimplemented unary operator");
        }
    }

    private static NSData evaluateBinaryExpression(Context context, BinaryExpression expression) {
        Expression st_lhs = expression.getLHS();
        Expression st_rhs = expression.getRHS();
        Operator operator = expression.getOperator();

        if (operator == Operator.ASSIGNMENT) {
            return evaluateAssignmentOrUpdateExpression(context, expression);
        }

        // For ASSIGNMENT, <lhs> may not be evaluated, so check before here
        NSData lhs = evaluateExpression(context, st_lhs);
        NSData rhs;

        switch (operator) {
            case NULL_ACCESSOR:
                if (NSNull.NULL.equals(lhs)) {
                    return lhs;
                }
                // Fall
            case ACCESSOR:
                if (!(st_rhs instanceof IdentifierExpression)) {
                    throw VMError.from(BuiltinClass.SyntaxError, "Invalid member access");
                }
                String member = ((IdentifierExpression) st_rhs).getIdentifier().getName();
                return lhs.nsAccess(member);

            case NULL_COALESCING:
                if (NSNull.NULL.equals(lhs)) {
                    return evaluateExpression(context, st_rhs);
                } else {
                    return lhs;
                }

            case AND:
                if (lhs.nsToBoolean().isTrue()) {
                    return evaluateExpression(context, st_rhs);
                } else {
                    return lhs;
                }

            case OR:
                if (!lhs.nsToBoolean().isTrue()) {
                    return evaluateExpression(context, st_rhs);
                } else {
                    return lhs;
                }

            case EXPONENTIATE:
            case MULTIPLY:
            case DIVIDE:
            case MODULO:
            case PLUS:
            case MINUS:
                rhs = evaluateExpression(context, st_rhs);
                switch (operator) {
                    case EXPONENTIATE:
                        return lhs.nsExponentiate(rhs);
                    case MULTIPLY:
                        return lhs.nsMultiply(rhs);
                    case DIVIDE:
                        return lhs.nsDivide(rhs);
                    case MODULO:
                        return lhs.nsModulo(rhs);
                    case PLUS:
                        return lhs.nsAdd(rhs);
                    case MINUS:
                        return lhs.nsSubtract(rhs);
                    default:
                        throw new InternalStateError("Unrecognised arithmetic operator");
                }

            case EQ:
            case NEQ:
                rhs = evaluateExpression(context, st_rhs);
                return lhs.nsTestEquality(rhs);

            case LT:
            case LEQ:
            case GT:
            case GEQ:
            case SPACESHIP:
                rhs = evaluateExpression(context, st_rhs);
                NSNumber result = lhs.nsCompare(rhs);
                double rawResult = result.getRawNumber();
                switch (operator) {
                    case SPACESHIP:
                        return result;
                    case LT:
                        return NSBoolean.from(rawResult < 0);
                    case LEQ:
                        return NSBoolean.from(rawResult <= 0);
                    case GT:
                        return NSBoolean.from(rawResult > 0);
                    case GEQ:
                        return NSBoolean.from(rawResult >= 0);
                    default:
                        throw new InternalStateError("Unknown relation operator");
                }

            case TYPEOF:
            case NOT_TYPEOF:
                boolean isInstance = evaluateTypeOfExpression(context, lhs, st_rhs);
                if (operator == Operator.NOT_TYPEOF) {
                    isInstance = !isInstance;
                }
                return NSBoolean.from(isInstance);

            default:
                throw new InternalStateError("Unimplemented binary operator");
        }
    }

    private static NSData evaluateLiteralNumberExpression(LiteralNumberExpression expression) {
        return NSNumber.from(expression.getValue());
    }

    private static NSData evaluateLiteralNullExpression() {
        return NSNull.NULL;
    }

    private static NSData evaluateLiteralStringExpression(LiteralStringExpression expression) {
        return NSString.from(expression.getValue());
    }

    private static NSData evaluateLiteralBooleanExpression(LiteralBooleanExpression expression) {
        return NSBoolean.from(expression.getValue());
    }

    private static NSData evaluateIdentifierExpression(Context context, IdentifierExpression expression) {
        String name = expression.getIdentifier().getName();
        NSData value = context.getContextSymbol(name);
        if (value == null) {
            throw VMError.from(BuiltinClass.ReferenceError, String.format("The variable `%s` does not exist", name));
        }
        return value;
    }

    private static NSData evaluateLookupExpression(Context context, LookupExpression expression) {
        NSData source = evaluateExpression(context, expression.getSource());
        if (expression.isNullSafe() && NSNull.NULL.equals(source)) {
            return NSNull.NULL;
        }

        return source.nsLookup(evaluateListOfExpressions(context, expression.getTerms().getTerms()));
    }

    private static NSData evaluateCallExpression(Context context, CallExpression expression) {
        NSData callee = evaluateExpression(context, expression.getCallee());
        if (expression.isNullSafe() && NSNull.NULL.equals(callee)) {
            return NSNull.NULL;
        }
        List<NSArgument> arguments = new ROList<>();
        for (CallExpression.Argument st_arg : expression.getArguments().getArguments()) {
            NSData value = evaluateExpression(context, st_arg.getValue());
            arguments.add(new NSArgument(st_arg.isOptional(), value));
        }
        return callee.nsCall(arguments);
    }

    private static NSData evaluateLambdaExpression(Context context, LambdaExpression expression) {
        return NSVirtualCallable.from(context, expression);
    }

    private static NSData evaluateAnonymousObjectExpression(Context context, AnonymousObjectExpression expression) {
        NSObject newObj = NSObject.from(null);
        for (Member m : expression.getMembers()) {
            newObj.nsAssign(m.getKey().getName(), evaluateExpression(context, m.getValue()));
        }
        return newObj;
    }
}
