package in.wilsonl.nanoscript.Syntax.Class.Member;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Expression.LambdaExpression;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Syntax.Parameter;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.SetOnce;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class ClassMethod extends ClassMember {
    private final SetOnce<LambdaExpression> lambda = new SetOnce<>();
    private final SetOnce<Identifier> name = new SetOnce<>();

    public static ClassMethod parseClassMethod(Tokens tokens) {
        ClassMethod method = new ClassMethod();
        Position position = tokens.require(T_KEYWORD_METHOD).getPosition();
        LambdaExpression lambda = new LambdaExpression(position);

        if (tokens.skipIfNext(T_COLON)) {
            method.isStatic(tokens.skipIfNext(T_KEYWORD_STATIC));

            // NOTE: This is not an AliasableReference, but rather a new identifier declaration
            // that **may** refer to some implemented interface's method
            Identifier name = Identifier.requireIdentifier(tokens);
            method.setName(name);
        } else {
            method.isStatic(false);
            method.setName(Identifier.requireIdentifier(tokens));
        }

        tokens.require(T_COLON);

        lambda.addAllParameters(Parameter.parseParametersList(tokens));
        lambda.setBody(CodeBlock.parseCodeBlock(tokens, T_KEYWORD_METHOD_END));
        tokens.require(T_KEYWORD_METHOD_END);
        method.setLambda(lambda);

        return method;
    }

    public Identifier getName() {
        return this.name.get();
    }

    public void setName(Identifier name) {
        this.name.set(name);
    }

    public LambdaExpression getLambda() {
        return lambda.get();
    }

    public void setLambda(LambdaExpression lambda) {
        this.lambda.set(lambda);
    }
}
