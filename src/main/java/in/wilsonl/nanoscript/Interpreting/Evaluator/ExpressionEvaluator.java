package in.wilsonl.nanoscript.Interpreting.Evaluator;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.Data.NSBoolean;
import in.wilsonl.nanoscript.Interpreting.Data.NSCallable;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSNumber;
import in.wilsonl.nanoscript.Interpreting.Data.NSObject;
import in.wilsonl.nanoscript.Interpreting.Data.NSString;
import in.wilsonl.nanoscript.Interpreting.Exception.ReferenceError;
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
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralBooleanExpression;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralNullExpression;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralNumberExpression;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralStringExpression;
import in.wilsonl.nanoscript.Syntax.Expression.LookupExpression;
import in.wilsonl.nanoscript.Syntax.Expression.SelfExpression;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

public class ExpressionEvaluator {
    public static NSData<?> evaluateExpression(Context context, Expression expression) {
        if (expression instanceof LambdaExpression) {
            return evaluateLambdaExpression(context, (LambdaExpression) expression);

        } else if (expression instanceof AnonymousObjectExpression) {
            return evaluateAnonymousObjectExpression(context, (AnonymousObjectExpression) expression);

        } else if (expression instanceof CallExpression) {
            return evaluateCallExpression(context, (CallExpression) expression);

        } else if (expression instanceof LookupExpression) {
            return evaluateLookupExpression(context, (LookupExpression) expression);

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
    }

    private static NSData<?> evaluateConditionalBranchesExpression(Context context, ConditionalBranchesExpression expression) {
        for (Branch b : expression.getConditionalBranches()) {
            NSData<?> condition = evaluateExpression(context, b.getCondition());
            NSBoolean passed = condition.toNSBoolean();
            if (passed == NSBoolean.TRUE) {
                return evaluateExpression(context, b.getValue());
            }
        }
        return evaluateExpression(context, expression.getFinalBranchValue());
    }

    private static NSData<?> evaluateSelfExpression(Context context) {
        NSData<?> value = context.getContextSymbol("self");
        if (value == null) {
            throw new ReferenceError("`self` is not available in this context");
        }
        return value;
    }

    private static NSData<?> evaluateUnaryExpression(Context context, UnaryExpression expression) {
        NSData<?> operand = evaluateExpression(context, expression.getOperand());
        return operand.applyUnaryOperator(expression.getOperator());
    }

    private static NSData<?> evaluateBinaryExpression(Context context, BinaryExpression expression) {
        NSData<?> lhs = evaluateExpression(context, expression.getLhs());
        NSData<?> rhs = evaluateExpression(context, expression.getRhs());
        return lhs.applyBinaryOperator(expression.getOperator(), rhs);
    }

    private static NSData<?> evaluateLiteralNumberExpression(LiteralNumberExpression expression) {
        return NSNumber.from(expression.getValue());
    }

    private static NSData<?> evaluateLiteralNullExpression() {
        return NSNull.NULL;
    }

    private static NSData<?> evaluateLiteralStringExpression(LiteralStringExpression expression) {
        return NSString.from(expression.getValue());
    }

    private static NSData<?> evaluateLiteralBooleanExpression(LiteralBooleanExpression expression) {
        return NSBoolean.from(expression.getValue());
    }

    private static NSData<?> evaluateIdentifierExpression(Context context, IdentifierExpression expression) {
        String name = expression.getIdentifier().getName();
        NSData<?> value = context.getContextSymbol(name);
        if (value == null) {
            throw new ReferenceError(String.format("The variable `%s` does not exist", name));
        }
        return value;
    }

    private static NSData<?> evaluateLookupExpression(Context context, LookupExpression expression) {
        NSData<?> source = evaluateExpression(context, expression.getSource());
        if (expression.isNullSafe() && NSNull.NULL.equals(source)) {
            return NSNull.NULL;
        }
        List<NSData<?>> terms = new ROList<>();
        for (Expression a : expression.getTerms().getTerms()) {
            terms.add(evaluateExpression(context, a));
        }
        return source.applyLookup(terms);
    }

    private static NSData<?> evaluateCallExpression(Context context, CallExpression expression) {
        NSData<?> callee = evaluateExpression(context, expression.getCallee());
        if (expression.isNullSafe() && NSNull.NULL.equals(callee)) {
            return NSNull.NULL;
        }
        List<NSData<?>> arguments = new ROList<>();
        for (Expression a : expression.getArguments().getPositionalArguments()) {
            arguments.add(evaluateExpression(context, a));
        }
        return callee.applyCall(arguments);
    }

    public static NSData<?> evaluateLambdaExpression(Context context, LambdaExpression expression) {
        return NSCallable.from(context, expression.getParameters(), expression.getBody());
    }

    public static NSData<?> evaluateAnonymousObjectExpression(Context context, AnonymousObjectExpression expression) {
        NSObject newObj = NSObject.from(null);
        for (Member m : expression.getMembers()) {
            newObj.applyAssignment(m.getKey().getName(), evaluateExpression(context, m.getValue()));
        }
        return newObj;
    }
}
