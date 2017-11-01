package in.wilsonl.nanoscript.Interpreting.Evaluator;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.BlockScope;
import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.Data.NSClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSIterator;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Interpreting.Data.NSObject;
import in.wilsonl.nanoscript.Interpreting.Data.NSVirtualClass;
import in.wilsonl.nanoscript.Interpreting.GlobalScope;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Reference;
import in.wilsonl.nanoscript.Syntax.Statement.BreakStatement;
import in.wilsonl.nanoscript.Syntax.Statement.CaseStatement;
import in.wilsonl.nanoscript.Syntax.Statement.CaseStatement.Option;
import in.wilsonl.nanoscript.Syntax.Statement.ClassStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ConditionalBranchesStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ConditionalBranchesStatement.Branch;
import in.wilsonl.nanoscript.Syntax.Statement.ExportStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ExpressionStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ForStatement;
import in.wilsonl.nanoscript.Syntax.Statement.LoopStatement;
import in.wilsonl.nanoscript.Syntax.Statement.NextStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ReturnStatement;
import in.wilsonl.nanoscript.Syntax.Statement.Statement;
import in.wilsonl.nanoscript.Syntax.Statement.ThrowStatement;
import in.wilsonl.nanoscript.Syntax.Statement.TryStatement;
import in.wilsonl.nanoscript.Syntax.Statement.VariableDeclarationStatement;

import java.util.List;
import java.util.Set;

import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.EndOfIterationError;
import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.SyntaxError;
import static in.wilsonl.nanoscript.Interpreting.Evaluator.ExpressionEvaluator.evaluateExpression;
import static in.wilsonl.nanoscript.Interpreting.Evaluator.ExpressionEvaluator.evaluateTypeOfExpression;

public class CodeBlockEvaluator {
    public static EvaluationResult evaluateCodeBlock(Context context, CodeBlock codeBlock) {
        for (Statement statement : codeBlock.getBody()) {
            // If <result> is not null, short circuit
            EvaluationResult result;

            if (statement instanceof BreakStatement) {
                result = evaluateBreakStatement();

            } else if (statement instanceof CaseStatement) {
                result = evaluateCaseStatement(context, (CaseStatement) statement);

            } else if (statement instanceof ClassStatement) {
                result = evaluateClassStatement(context, (ClassStatement) statement);

            } else if (statement instanceof ConditionalBranchesStatement) {
                result = evaluateConditionalBranchesStatement(context, (ConditionalBranchesStatement) statement);

            } else if (statement instanceof ExportStatement) {
                result = evaluateExportStatement(context, (ExportStatement) statement);

            } else if (statement instanceof ExpressionStatement) {
                result = evaluateExpressionStatement(context, (ExpressionStatement) statement);

            } else if (statement instanceof ForStatement) {
                result = evaluateForStatement(context, (ForStatement) statement);

            } else if (statement instanceof LoopStatement) {
                result = evaluateLoopStatement(context, (LoopStatement) statement);

            } else if (statement instanceof NextStatement) {
                result = evaluateNextStatement();

            } else if (statement instanceof ReturnStatement) {
                result = evaluateReturnStatement(context, (ReturnStatement) statement);

            } else if (statement instanceof ThrowStatement) {
                result = evaluateThrowStatement(context, (ThrowStatement) statement);

            } else if (statement instanceof TryStatement) {
                result = evaluateTryStatement(context, (TryStatement) statement);

            } else if (statement instanceof VariableDeclarationStatement) {
                result = evaluateVariableDeclarationStatement(context, (VariableDeclarationStatement) statement);

            } else {
                throw new InternalStateError("Unknown statement type");
            }

            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private static EvaluationResult evaluateTryStatement(Context context, TryStatement statement) {
        CodeBlock st_tryBody = statement.getTryBody();
        BlockScope scope = new BlockScope(context, BlockScope.Type.TRY);
        try {
            evaluateCodeBlock(scope, st_tryBody);
        } catch (VMError vme) {
            NSData error = vme.getValue();
            scope.clearSymbols();
            for (TryStatement.Catch st_catch : statement.getCatchBlocks()) {
                Set<Reference> types = st_catch.getTypes();
                CodeBlock st_catchBody = st_catch.getBody();
                String paramName = st_catch.getParameterName().getName();

                boolean thisBlockMatches = false;

                if (types != null) {
                    for (Reference st_type : types) {
                        if (evaluateTypeOfExpression(context, error, st_type.toExpression())) {
                            thisBlockMatches = true;
                            break;
                        }
                    }
                } else {
                    thisBlockMatches = true;
                }

                if (thisBlockMatches) {
                    scope.createContextSymbol(paramName, error);
                    evaluateCodeBlock(scope, st_catchBody);
                    return null;
                }
            }

            throw vme;
        }

        return null;
    }

    private static EvaluationResult evaluateClassStatement(Context context, ClassStatement statement) {
        if (!(context instanceof GlobalScope)) {
            throw VMError.from(SyntaxError, "Classes must be declared at the top level");
        }

        NSClass nsClass = NSVirtualClass.from(context, statement.getNSClass());
        context.createContextSymbol(nsClass.getName(), nsClass);

        return null;
    }

    private static EvaluationResult evaluateExportStatement(Context context, ExportStatement statement) {
        if (!(context instanceof GlobalScope)) {
            throw VMError.from(SyntaxError, "Exports must be declared at the top level");
        }

        String name = statement.getName().getName();
        NSData value = evaluateExpression(context, statement.getValue());

        ((GlobalScope) context).addExport(name, value);

        return null;
    }

    private static EvaluationResult evaluateForStatement(Context context, ForStatement statement) {
        List<ForStatement.Iterable> st_iterables = statement.getIterables();
        CodeBlock st_body = statement.getBody();

        int iterablesCount = st_iterables.size();
        String[] names = new String[iterablesCount];
        NSIterator[] iters = new NSIterator[iterablesCount];

        for (int i = 0; i < iterablesCount; i++) {
            ForStatement.Iterable st_iter = st_iterables.get(i);
            names[i] = st_iter.getFormalParameterName().getName();
            iters[i] = evaluateExpression(context, st_iter.getExpression()).nsIterate();
        }

        BlockScope scope = new BlockScope(context, BlockScope.Type.FOR);

        while (true) {
            scope.clearSymbols();

            for (int i = 0; i < iterablesCount; i++) {
                String name = names[i];
                NSData value;
                try {
                    value = iters[i].next();
                } catch (VMError err) {
                    NSData vmerrobj = err.getValue();
                    if (vmerrobj instanceof NSObject && ((NSObject) vmerrobj).isInstanceOf(EndOfIterationError.getNSClass()).isTrue()) {
                        return null;
                    } else {
                        throw err;
                    }
                }
                scope.createContextSymbol(name, value);
            }

            EvaluationResult evaluationResult = evaluateCodeBlock(scope, st_body);
            //noinspection Duplicates
            if (evaluationResult != null) {
                switch (evaluationResult.getMode()) {
                    case BREAK:
                        return null;

                    case NEXT:
                        break;

                    case RETURN:
                        return evaluationResult;

                    default:
                        throw new InternalStateError("Unknown evaluation result mode");
                }
            }
        }
    }

    private static EvaluationResult evaluateThrowStatement(Context context, ThrowStatement statement) {
        NSData value = evaluateExpression(context, statement.getValue());
        throw new VMError(value);
    }

    private static EvaluationResult evaluateReturnStatement(Context context, ReturnStatement statement) {
        Expression st_val = statement.getValue();
        NSData value;
        if (st_val == null) {
            value = NSNull.NULL;
        } else {
            value = evaluateExpression(context, statement.getValue());
        }
        return new EvaluationResult(EvaluationResult.Mode.RETURN, value);
    }

    private static EvaluationResult evaluateNextStatement() {
        return new EvaluationResult(EvaluationResult.Mode.NEXT);
    }

    private static EvaluationResult evaluateBreakStatement() {
        return new EvaluationResult(EvaluationResult.Mode.BREAK);
    }

    private static EvaluationResult evaluateConditionalBranchesStatement(Context context, ConditionalBranchesStatement statement) {
        for (Branch b : statement.getConditionalBranches()) {
            Expression st_cond = b.getCondition();
            boolean passed = st_cond == null || evaluateExpression(context, st_cond).nsToBoolean().isTrue();
            if (passed) {
                BlockScope scope = new BlockScope(context, BlockScope.Type.CONDITIONAL_BRANCH);
                return evaluateCodeBlock(scope, b.getBody());
            }
        }
        return null;
    }

    private static EvaluationResult evaluateExpressionStatement(Context context, ExpressionStatement statement) {
        evaluateExpression(context, statement.getExpression());
        return null;
    }

    private static EvaluationResult evaluateCaseStatement(Context context, CaseStatement statement) {
        NSData target = evaluateExpression(context, statement.getTarget());
        List<Option> st_options = statement.getOptions();

        for (Option o : st_options) {
            Expression st_cond = o.getCondition();
            CodeBlock st_body = o.getBody();

            boolean passed = st_cond == null || target.nsTestEquality(evaluateExpression(context, st_cond)).isTrue();

            if (passed) {
                EvaluationResult evaluationResult = evaluateCodeBlock(context, st_body);
                if (evaluationResult != null) {
                    switch (evaluationResult.getMode()) {
                        case BREAK:
                            return null;

                        case NEXT:
                        case RETURN:
                            return evaluationResult;

                        default:
                            throw new InternalStateError("Unknown evaluation result mode");
                    }
                }
            }
        }

        return null;
    }

    private static EvaluationResult evaluateVariableDeclarationStatement(Context context, VariableDeclarationStatement statement) {
        String name = statement.getVariable().getName().getName();
        Expression st_init = statement.getVariable().getInitialiser();
        NSData value = evaluateExpression(context, st_init);
        context.createContextSymbol(name, value);
        return null;
    }

    private static EvaluationResult evaluateLoopStatement(Context context, LoopStatement statement) {
        CodeBlock st_body = statement.getBody();
        Expression st_condition = statement.getCondition();
        boolean invertResult = statement.getTestType() == LoopStatement.TestType.NEGATIVE;
        boolean testBefore = statement.getTestStage() == LoopStatement.TestStage.PRE;

        BlockScope loopScope = new BlockScope(context, BlockScope.Type.LOOP);

        while (true) {
            // Clear before evaluating condition expression
            loopScope.clearSymbols();

            if (testBefore) {
                boolean shouldStart = evaluateExpression(loopScope, st_condition).nsToBoolean().isTrue();
                if (invertResult) {
                    shouldStart = !shouldStart;
                }
                if (!shouldStart) {
                    break;
                }
            }

            EvaluationResult evaluationResult = evaluateCodeBlock(loopScope, st_body);
            //noinspection Duplicates
            if (evaluationResult != null) {
                switch (evaluationResult.getMode()) {
                    case BREAK:
                        return null;

                    case NEXT:
                        break;

                    case RETURN:
                        return evaluationResult;

                    default:
                        throw new InternalStateError("Unknown evaluation result mode");
                }
            }

            if (!testBefore) {
                boolean shouldEnd = !evaluateExpression(loopScope, st_condition).nsToBoolean().isTrue();
                if (invertResult) {
                    shouldEnd = !shouldEnd;
                }
                if (shouldEnd) {
                    break;
                }
            }
        }

        return null;
    }

}
