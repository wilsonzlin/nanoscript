package in.wilsonl.nanoscript.Syntax;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;
import in.wilsonl.nanoscript.Utils.Utils;

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

    public void pushPart(Identifier part) {
        parts.add(part);
    }

    @Override
    public String toString() {
        return Utils.join(".", parts);
    }

}
