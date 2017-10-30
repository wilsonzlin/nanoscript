package in.wilsonl.nanoscript.Interpreting.Evaluator;

import in.wilsonl.nanoscript.Exception.InternalError;
import in.wilsonl.nanoscript.Interpreting.BlockScope;
import in.wilsonl.nanoscript.Interpreting.Context;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSNull;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Operator;
import in.wilsonl.nanoscript.Syntax.Statement.BreakStatement;
import in.wilsonl.nanoscript.Syntax.Statement.CaseStatement;
import in.wilsonl.nanoscript.Syntax.Statement.CaseStatement.Option;
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
import in.wilsonl.nanoscript.Syntax.Statement.VariableDeclarationStatement;

import java.util.List;

import static in.wilsonl.nanoscript.Interpreting.Evaluator.ExpressionEvaluator.evaluateExpression;

public class CodeBlockEvaluator {
    public static EvaluationResult evaluateCodeBlock(Context context, CodeBlock codeBlock) {
        for (Statement statement : codeBlock.getBody()) {
            // If <result> is not null, short circuit
            EvaluationResult result;

            if (statement instanceof BreakStatement) {
                result = evaluateBreakStatement();

            } else if (statement instanceof CaseStatement) {
                result = evaluateCaseStatement(context, (CaseStatement) statement);

            } else if (statement instanceof ConditionalBranchesStatement) {
                result = evaluateConditionalBranchesStatement(context, (ConditionalBranchesStatement) statement);

            } else if (statement instanceof ExportStatement) {
                // TODO

            } else if (statement instanceof ExpressionStatement) {
                result = evaluateExpressionStatement(context, (ExpressionStatement) statement);

            } else if (statement instanceof ForStatement) {
                // TODO

            } else if (statement instanceof LoopStatement) {
                result = evaluateLoopStatement(context, (LoopStatement) statement);

            } else if (statement instanceof NextStatement) {
                result = evaluateNextStatement();

            } else if (statement instanceof ReturnStatement) {
                result = evaluateReturnStatement(context, (ReturnStatement) statement);

            } else if (statement instanceof ThrowStatement) {
                result = evaluateThrowStatement(context, (ThrowStatement) statement);

            } else if (statement instanceof VariableDeclarationStatement) {
                result = evaluateVariableDeclarationStatement(context, (VariableDeclarationStatement) statement);

            } else {
                throw new InternalError("Unknown statement type");
            }

            if (result != null) {
                return result;
            }
        }
    }

    private static EvaluationResult evaluateThrowStatement(Context context, ThrowStatement statement) {
        NSData<?> value = evaluateExpression(context, statement.getValue());
        return new EvaluationResult(EvaluationResult.Mode.THROW, value);
    }

    private static EvaluationResult evaluateReturnStatement(Context context, ReturnStatement statement) {
        Expression st_val = statement.getValue();
        NSData<?> value;
        if (st_val == null) {
            value = NSNull.NULL;
        } else {
            value = evaluateExpression(context, statement.getValue());
        }
        return new EvaluationResult(EvaluationResult.Mode.RETURN, value);
    }

    private static EvaluationResult evaluateNextStatement() {
        return new EvaluationResult(EvaluationResult.Mode.CONTINUE);
    }

    private static EvaluationResult evaluateBreakStatement() {
        return new EvaluationResult(EvaluationResult.Mode.BREAK);
    }

    private static EvaluationResult evaluateConditionalBranchesStatement(Context context, ConditionalBranchesStatement statement) {
        for (Branch b : statement.getConditionalBranches()) {
            Expression st_cond = b.getCondition();
            boolean passed = st_cond == null || evaluateExpression(context, st_cond).toNSBoolean().getRawValue();
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
        NSData<?> target = evaluateExpression(context, statement.getTarget());
        List<Option> st_options = statement.getOptions();

        for (Option o : st_options) {
            Expression st_cond = o.getCondition();
            CodeBlock st_body = o.getBody();

            boolean passed = st_cond == null || target.applyBinaryOperator(Operator.EQ, evaluateExpression(context, st_cond)).toNSBoolean().getRawValue();

            if (passed) {
                EvaluationResult evaluationResult = evaluateCodeBlock(context, st_body);
                if (evaluationResult != null) {
                    switch (evaluationResult.getMode()) {
                        case BREAK:
                            return null;

                        case CONTINUE:
                        case RETURN:
                        case THROW:
                            return evaluationResult;

                        default:
                            throw new InternalError("Unknown evaluation result mode");
                    }
                }
            }
        }

        return null;
    }

    private static EvaluationResult evaluateVariableDeclarationStatement(Context context, VariableDeclarationStatement statement) {
        String name = statement.getVariable().getName().getName();
        Expression st_init = statement.getVariable().getInitialiser();
        NSData<?> value = evaluateExpression(context, st_init);
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
            if (testBefore) {
                boolean shouldStart = evaluateExpression(loopScope, st_condition).toNSBoolean().getRawValue();
                if (invertResult) {
                    shouldStart = !shouldStart;
                }
                if (!shouldStart) {
                    break;
                }
            }

            boolean broken = false;
            EvaluationResult evaluationResult = evaluateCodeBlock(loopScope, st_body);
            if (evaluationResult != null) {
                switch (evaluationResult.getMode()) {
                    case BREAK:
                        broken = true;
                        break;
                    case CONTINUE:
                        break;

                    case RETURN:
                    case THROW:
                        return evaluationResult;

                    default:
                        throw new InternalError("Unknown evaluation result mode");
                }
            }

            if (broken) {
                break;
            }

            if (!testBefore) {
                boolean shouldEnd = !evaluateExpression(loopScope, st_condition).toNSBoolean().getRawValue();
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

    public static class EvaluationResult {
        private final Mode mode;
        private final NSData<?> value; // Can be null; if returning without a value, <value> should be NSNull.NULL

        public EvaluationResult(Mode mode, NSData<?> value) {
            this.mode = mode;
            this.value = value;
        }

        public EvaluationResult(Mode mode) {
            this(mode, null);
        }

        public Mode getMode() {
            return mode;
        }

        public NSData<?> getValue() {
            return value;
        }

        public enum Mode {
            BREAK, CONTINUE, RETURN, THROW
        }
    }
}
