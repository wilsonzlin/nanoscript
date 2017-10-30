package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Builtin.Class;
import in.wilsonl.nanoscript.Interpreting.Builtin.Function;
import in.wilsonl.nanoscript.Syntax.Chunk;

import java.util.EnumSet;

public class Interpreter {
    private final Chunk chunk;

    public Interpreter(Chunk chunk) {
        this.chunk = chunk;
    }

    public void interpret() {
        GlobalScope globalScope = new GlobalScope();

        // TODO imports

        for (Function f : EnumSet.allOf(Function.class)) {
            globalScope.createContextSymbol(f.name(), f.getFunction());
        }
        for (Class f : EnumSet.allOf(Class.class)) {
            globalScope.createContextSymbol(f.name(), f.getNativeClass());
        }

        globalScope.evaluateCodeBlockInContext(chunk.getCodeBlock());
    }
}
