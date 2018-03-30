package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Utils.Position;

public class LoopStatement extends Statement {
    private final Expression condition;
    private final CodeBlock body;
    private final TestStage testStage;
    private final TestType testType;

    public LoopStatement(Position position, Expression condition, CodeBlock body, TestStage testStage, TestType testType) {
        super(position);
        this.condition = condition;
        this.body = body;
        this.testStage = testStage;
        this.testType = testType;
    }

    private static LoopStatement parseWhileOrUntilStatement(Tokens tokens, TokenType initialToken) {
        TestType testType;
        TokenType bodyEndDelimiter;
        switch (initialToken) {
            case T_KEYWORD_WHILE:
                testType = TestType.POSITIVE;
                bodyEndDelimiter = TokenType.T_KEYWORD_WHILE_END;
                break;

            case T_KEYWORD_UNTIL:
                testType = TestType.NEGATIVE;
                bodyEndDelimiter = TokenType.T_KEYWORD_UNTIL_END;
                break;

            default:
                throw new InternalStateError("Unknown loop statement initial token: " + initialToken);
        }
        Position position = tokens.require(initialToken).getPosition();
        Expression condition = Expression.parseExpression(tokens, new AcceptableTokenTypes(TokenType.T_KEYWORD_BEFORE, TokenType.T_KEYWORD_AFTER));
        TestStage testStage;
        switch (tokens.accept().getType()) {
            case T_KEYWORD_BEFORE:
                testStage = TestStage.PRE;
                break;

            case T_KEYWORD_AFTER:
                testStage = TestStage.POST;
                break;

            default:
                throw tokens.constructRequiredSyntaxNotFoundException("Loop test stage not specified");
        }
        CodeBlock body = CodeBlock.parseCodeBlock(tokens, bodyEndDelimiter);
        tokens.require(bodyEndDelimiter);

        return new LoopStatement(position, condition, body, testStage, testType);
    }

    public static LoopStatement parseWhileStatement(Tokens tokens) {
        return parseWhileOrUntilStatement(tokens, TokenType.T_KEYWORD_WHILE);
    }

    public static LoopStatement parseUntilStatement(Tokens tokens) {
        return parseWhileOrUntilStatement(tokens, TokenType.T_KEYWORD_UNTIL);
    }

    public Expression getCondition() {
        return condition;
    }

    public CodeBlock getBody() {
        return body;
    }

    public TestStage getTestStage() {
        return testStage;
    }

    public TestType getTestType() {
        return testType;
    }

    public enum TestStage {
        PRE, POST
    }

    public enum TestType {
        POSITIVE, NEGATIVE
    }
}
