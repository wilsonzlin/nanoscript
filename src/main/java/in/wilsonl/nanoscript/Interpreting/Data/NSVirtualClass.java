package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.Evaluator.ExpressionEvaluator;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Syntax.Class.Class;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassConstructor;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassMethod;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassVariable;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Expression.LambdaExpression;
import in.wilsonl.nanoscript.Syntax.Reference;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

public class NSVirtualClass extends NSClass {
    private final Context parentContext;
    // Order matters
    private final List<ClassVariable> rawInstanceVariables = new ROList<>();

    private NSVirtualClass(Context parentContext) {
        super();
        this.parentContext = parentContext;
    }

    public static NSClass from(Context parentContext, Class st_class) {
        // Parent context should be the global/chunk context,
        // as nested or variable classes are not allowed
        NSVirtualClass nsClass = new NSVirtualClass(parentContext);

        // Get name
        String name = st_class.getName().getName();
        nsClass.setName(name);

        // Load parents
        for (Reference st_parent_ref : st_class.getParents()) {
            Expression st_deref_expr = st_parent_ref.toExpression();
            // If reference is invalid, an exception will be thrown
            NSData result = ExpressionEvaluator.evaluateExpression(parentContext, st_deref_expr);
            if (result.getType() != Type.CLASS) {
                throw VMError.from(BuiltinClass.TypeError, String.format("Parent `%s` is not a class", st_deref_expr.toString()));
            }
            NSClass p = (NSClass) result;
            nsClass.addParent(p);
        }

        // Don't check that no parent has a non-default constructor with more than zero parameters,
        // as the parent might be a NSNativeClass (also, because this is a scripting language)
        ClassConstructor st_constructor = st_class.getConstructor();
        if (st_constructor != null) {
            LambdaExpression lambda = st_constructor.getLambda();
            nsClass.setConstructor(NSVirtualCallable.from(parentContext, lambda));
        }

        // Process methods
        for (ClassMethod st_method : st_class.getMethods()) {
            String methodName = st_method.getName().getName();
            NSVirtualCallable callable = NSVirtualCallable.from(parentContext, st_method.getLambda());
            if (st_method.isStatic()) {
                nsClass.addStaticMethod(methodName, callable);
            } else {
                nsClass.addInstanceMethod(methodName, callable);
            }
        }

        // Process variables
        for (ClassVariable st_var : st_class.getVariables()) {
            String varName = st_var.getVariable().getName().getName();
            if (st_var.isStatic()) {
                // Order matters
                NSData value = ExpressionEvaluator.evaluateExpression(parentContext, st_var.getVariable().getInitialiser());
                nsClass.addStaticVariable(varName, value);
            } else {
                nsClass.addInstanceVariable(st_var);
            }
        }

        return nsClass;
    }

    private void addInstanceVariable(ClassVariable classVariable) {
        rawInstanceVariables.add(classVariable);
    }

    @Override
    protected void applyOwnInstanceVariables(NSObject target) {
        Context tempEvaluationCtx = new Context() {
            @Override
            public NSData getContextSymbol(String name) {
                if (name.equals("self")) {
                    return target;
                }
                return parentContext.getContextSymbol(name);
            }

            @Override
            public boolean setContextSymbol(String name, NSData value) {
                return parentContext.setContextSymbol(name, value);
            }
        };
        for (ClassVariable st_var : rawInstanceVariables) {
            String name = st_var.getVariable().getName().getName();
            Expression st_init = st_var.getVariable().getInitialiser();

            target.createOrUpdateMemberVariable(name, ExpressionEvaluator.evaluateExpression(tempEvaluationCtx, st_init));
        }
    }
}
