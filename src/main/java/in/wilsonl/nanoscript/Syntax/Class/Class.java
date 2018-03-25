package in.wilsonl.nanoscript.Syntax.Class;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassConstructor;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassMethod;
import in.wilsonl.nanoscript.Syntax.Class.Member.ClassVariable;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Syntax.Reference;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class Class {
    private static final AcceptableTokenTypes MODIFIER_TOKENS = new AcceptableTokenTypes(T_KEYWORD_STATIC, T_KEYWORD_FINAL);
    // Don't use a Set, as ordering matters
    private final List<ClassVariable> memberVariables = new ROList<>();
    private final List<ClassMethod> memberMethods = new ROList<>();
    private final List<Reference> parents = new ROList<>();
    private final SetOnce<Identifier> name = new SetOnce<>();
    private final SetOnce<ClassConstructor> constructor = new SetOnce<>(true); // Can be null if using default constructor
    // TODO final, static
    private final Position position;

    public Class(Position position) {
        this.position = position;
    }

    public static Class parseClass(Tokens tokens) {
        Position position = tokens.require(T_KEYWORD_CLASS).getPosition();
        Class nanoscriptClass = new Class(position);

        if (tokens.skipIfNext(T_COLON)) {
            // TODO
            tokens.require(MODIFIER_TOKENS);
        }

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
                case T_KEYWORD_CONSTRUCTOR:
                    if (nanoscriptClass.constructor.isSet()) {
                        throw tokens.constructMalformedSyntaxException("A constructor already exists");
                    }
                    nanoscriptClass.setConstructor(ClassConstructor.parseConstructor(tokens));
                    break;

                case T_KEYWORD_METHOD:
                    nanoscriptClass.addMemberMethod(ClassMethod.parseClassMethod(tokens));
                    break;

                case T_KEYWORD_VARIABLE:
                    nanoscriptClass.addMemberVariable(ClassVariable.parseClassVariable(tokens));
                    break;

                default:
                    throw tokens.constructMalformedSyntaxException("Expected a class body unit, got " + nextTokenType);
            }
        }

        if (!nanoscriptClass.constructor.isSet()) {
            nanoscriptClass.constructor.set(null);
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

    public List<Reference> getParents() {
        return parents;
    }

    public List<ClassMethod> getMethods() {
        return memberMethods;
    }

    public List<ClassVariable> getVariables() {
        return memberVariables;
    }

    public ClassConstructor getConstructor() {
        return constructor.get();
    }

    public void setConstructor(ClassConstructor constructor) {
        this.constructor.set(constructor);
    }

    public Position getPosition() {
        return position;
    }
}
