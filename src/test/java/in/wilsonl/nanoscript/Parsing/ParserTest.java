package in.wilsonl.nanoscript.Parsing;

import in.wilsonl.nanoscript.Lexing.Code;
import in.wilsonl.nanoscript.Lexing.Lexer;
import in.wilsonl.nanoscript.Syntax.Chunk;
import org.junit.Test;

import java.io.InputStream;

public class ParserTest {
    private InputStream getUTF8TextResource(String path) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(path);
    }

    private Chunk testParser(InputStream testCode) {
        Code code = new Code(testCode);

        Lexer lexer = new Lexer(code);
        Tokens tokens = new Tokens(lexer);
        Chunk parsed = Parser.parse(tokens);

        return parsed;
    }

    @Test
    public void testSyntaxParsing() {
        testParser(getUTF8TextResource("code/syntax.nanoscript"));
    }
}
