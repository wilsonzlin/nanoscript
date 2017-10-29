package in.wilsonl.nanoscript.Syntax.Class;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassMethod;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassVariable;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Syntax.Reference;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class Class {
    // Don't use a Set, as ordering matters
    private final List<ClassVariable> memberVariables = new ROList<>();
    private final List<ClassMethod> memberMethods = new ROList<>();
    private final List<Reference> parents = new ROList<>();
    private final SetOnce<Identifier> name = new SetOnce<>();

    public static Class parseClass(Tokens tokens, boolean isTopLevel) {
        Class nanoscriptClass = new Class();

        tokens.require(T_KEYWORD_CLASS);
        tokens.require(T_COLON);

        nanoscriptClass.setName(Identifier.requireIdentifier(tokens));

        if (tokens.skipIfNext(T_COLON)) {
            do {
                nanoscriptClass.addParent(Reference.parseReference(tokens));
            } while (tokens.skipIfNext(T_PLUS));
        }

        tokens.require(T_KEYWORD_BEGIN);

        TokenType nextTokenType;

        while ((nextTokenType = tokens.peekType()) != T_KEYWORD_CLASS_END) {
            switch (nextTokenType) {
                case T_KEYWORD_METHOD:
                    nanoscriptClass.addMemberMethod(ClassMethod.parseClassMethod(tokens));
                    break;

                case T_IDENTIFIER:
                    nanoscriptClass.addMemberVariable(ClassVariable.parseClassVariable(tokens));
                    break;

                default:
                    throw tokens.constructMalformedSyntaxException("Expected a class body unit, got " + nextTokenType);
            }
        }

        tokens.require(T_KEYWORD_CLASS_END);

        return nanoscriptClass;
    }

    public Identifier getName() {
        return name.get();
    }

    public void setName(Identifier name) {
        this.name.set(name);
    }

    public void addParent(Reference parent) {
        this.parents.add(parent);
    }

    public void addMemberVariable(ClassVariable variable) {
        memberVariables.add(variable);
    }

    public void addMemberMethod(ClassMethod method) {
        memberMethods.add(method);
    }
}
