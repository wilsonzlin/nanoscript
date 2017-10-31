package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinFunction;
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

        for (BuiltinFunction f : EnumSet.allOf(BuiltinFunction.class)) {
            globalScope.createContextSymbol(f.name(), f.getFunction());
        }
        for (BuiltinClass f : EnumSet.allOf(BuiltinClass.class)) {
            globalScope.createContextSymbol(f.name(), f.getNSClass());
        }

        globalScope.evaluateCodeBlockInContext(chunk.getCodeBlock());
    }
}
