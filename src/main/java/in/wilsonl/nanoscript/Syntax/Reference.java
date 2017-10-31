package in.wilsonl.nanoscript.Syntax;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Expression;
import in.wilsonl.nanoscript.Syntax.Expression.General.BinaryExpression;
import in.wilsonl.nanoscript.Syntax.Expression.IdentifierExpression;
import in.wilsonl.nanoscript.Syntax.Expression.SelfExpression;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;
import in.wilsonl.nanoscript.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.T_ACCESSOR;
import static in.wilsonl.nanoscript.Parsing.TokenType.T_KEYWORD_SELF;

public class Reference {

    private final SetOnce<Boolean> startsWithSelf = new SetOnce<>();
    private final List<Identifier> parts = new ROList<>();

    public static Reference parseReference(Tokens tokens) {
        Reference reference = new Reference();

        reference.startsWithSelf(tokens.skipIfNext(T_KEYWORD_SELF));
        if (reference.startsWithSelf() && !tokens.skipIfNext(T_ACCESSOR)) {
            // End here
            return reference;
        }

        do {
            reference.pushPart(Identifier.requireIdentifier(tokens));
        } while (tokens.skipIfNext(T_ACCESSOR));

        return reference;
    }

    public boolean startsWithSelf() {
        return startsWithSelf.get();
    }

    public void startsWithSelf(boolean s) {
        startsWithSelf.set(s);
    }

    public List<Identifier> getParts() {
        return parts;
    }

    public void pushPart(Identifier part) {
        parts.add(part);
    }

    public Expression toExpression() {
        // Reference may just be one identifier and not involve any accessing
        List<Identifier> toProcess = new ArrayList<>(getParts());
        Expression result;
        if (startsWithSelf()) {
            result = new SelfExpression();
        } else {
            result = new IdentifierExpression(toProcess.remove(0));
        }

        while (!toProcess.isEmpty()) {
            Identifier rhs = toProcess.remove(0);
            result = new BinaryExpression(result, Operator.ACCESSOR, new IdentifierExpression(rhs));
        }

        return result;
    }

    @Override
    public String toString() {
        return Utils.join(".", parts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reference that = (Reference) o;

        return startsWithSelf() == that.startsWithSelf() && getParts().equals(that.getParts());
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(startsWithSelf());
        result = 31 * result + getParts().hashCode();
        return result;
    }

}
