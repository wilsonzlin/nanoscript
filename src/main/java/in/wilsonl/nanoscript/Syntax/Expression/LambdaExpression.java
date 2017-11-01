package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Parameter;
import in.wilsonl.nanoscript.Syntax.Statement.ReturnStatement;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class LambdaExpression extends Expression {
    private final List<Parameter> parameters = new ROList<>();
    private final SetOnce<CodeBlock> body = new SetOnce<>();

    public LambdaExpression(Position position) {
        super(position);
    }

    public static LambdaExpression parseLambdaExpression(Tokens tokens) {
        Position position = tokens.require(T_KEYWORD_FUNCTION).getPosition();
        LambdaExpression lambda = new LambdaExpression(position);

        lambda.addAllParameters(Parameter.parseParametersList(tokens));

        CodeBlock body;
        if (tokens.skipIfNext(T_ARROW_RIGHT)) {
            Expression expr = Expression.parseExpression(tokens);
            body = new CodeBlock();
            body.pushStatement(new ReturnStatement(expr.getPosition(), expr));
        } else {
            body = CodeBlock.parseCodeBlock(tokens, T_KEYWORD_FUNCTION_END);
            tokens.require(T_KEYWORD_FUNCTION_END);
        }

        lambda.setBody(body);

        return lambda;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void addAllParameters(List<Parameter> params) {
        parameters.addAll(params);
    }

    public CodeBlock getBody() {
        return body.get();
    }

    public void setBody(CodeBlock b) {
        body.set(b);
    }
}
