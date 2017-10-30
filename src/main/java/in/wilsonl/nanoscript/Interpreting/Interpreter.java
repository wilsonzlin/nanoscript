package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Builtin.print;
import in.wilsonl.nanoscript.Interpreting.Builtin.range;
import in.wilsonl.nanoscript.Syntax.Chunk;

public class Interpreter {
    private final Chunk chunk;

    public Interpreter(Chunk chunk) {
        this.chunk = chunk;
    }

    public void interpret() {
        GlobalScope globalScope = new GlobalScope();

        // TODO imports

        globalScope.createContextSymbol("print", print.print);
        globalScope.createContextSymbol("range", range.range);

        globalScope.evaluateCodeBlockInContext(chunk.getCodeBlock());
    }
}
