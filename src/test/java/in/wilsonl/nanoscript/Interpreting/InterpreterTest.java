package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Lexing.Code;
import in.wilsonl.nanoscript.Lexing.Lexer;
import in.wilsonl.nanoscript.Parsing.Parser;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Chunk;
import org.junit.Test;

import java.io.InputStream;

public class InterpreterTest {
    private InputStream getUTF8TextResource(String path) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(path);
    }

    private void testInterpreter(InputStream testCode) {
        Code code = new Code(testCode);

        Lexer lexer = new Lexer(code);
        Tokens tokens = new Tokens(lexer);
        Chunk parsed = Parser.parse(tokens);
        Interpreter interpreter = new Interpreter(parsed);
        interpreter.interpret();
    }

    @Test
    public void testSyntaxInterpreting() {
        testInterpreter(getUTF8TextResource("code/syntax.nanoscript"));
    }
}
