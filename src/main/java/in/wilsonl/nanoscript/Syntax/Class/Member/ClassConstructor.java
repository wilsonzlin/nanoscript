package in.wilsonl.nanoscript.Syntax.Class.Member;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.LambdaExpression;
import in.wilsonl.nanoscript.Syntax.Parameter;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.SetOnce;

import static in.wilsonl.nanoscript.Parsing.TokenType.T_KEYWORD_CONSTRUCTOR;
import static in.wilsonl.nanoscript.Parsing.TokenType.T_KEYWORD_CONSTRUCTOR_END;

public class ClassConstructor extends ClassMember {
    private final SetOnce<LambdaExpression> lambda = new SetOnce<>();

    public static ClassConstructor parseConstructor(Tokens tokens) {
        ClassConstructor constructor = new ClassConstructor();
        Position position = tokens.require(T_KEYWORD_CONSTRUCTOR).getPosition();
        LambdaExpression lambda = new LambdaExpression(position);

        lambda.addAllParameters(Parameter.parseParametersList(tokens));
        lambda.setBody(CodeBlock.parseCodeBlock(tokens, T_KEYWORD_CONSTRUCTOR_END));
        tokens.require(T_KEYWORD_CONSTRUCTOR_END);
        constructor.setLambda(lambda);

        return constructor;
    }

    public LambdaExpression getLambda() {
        return lambda.get();
    }

    public void setLambda(LambdaExpression lambda) {
        this.lambda.set(lambda);
    }
}
