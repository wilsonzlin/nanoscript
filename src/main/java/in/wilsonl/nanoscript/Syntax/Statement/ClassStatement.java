package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Class.Class;

public class ClassStatement extends Statement {
    private final Class nsClass;

    public ClassStatement(Class nsClass) {
        this.nsClass = nsClass;
    }

    public static ClassStatement parseClassStatement(Tokens tokens) {
        return new ClassStatement(Class.parseClass(tokens));
    }

    public Class getNSClass() {
        return nsClass;
    }
}
