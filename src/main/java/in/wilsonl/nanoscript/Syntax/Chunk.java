package in.wilsonl.nanoscript.Syntax;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Statement.ImportStatement;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.T_KEYWORD_FROM;

public class Chunk {
    private final List<ImportStatement> imports = new ROList<>();
    private final SetOnce<CodeBlock> codeBlock = new SetOnce<>();

    public static Chunk parseChunk(Tokens tokens) {
        Chunk chunk = new Chunk();

        while (tokens.peekType() == T_KEYWORD_FROM) {
            chunk.addImport(ImportStatement.parseImportStatement(tokens));
        }

        chunk.setCodeBlock(CodeBlock.parseCodeBlock(tokens, new AcceptableTokenTypes()));

        return chunk;
    }

    public List<ImportStatement> getImports() {
        return imports;
    }

    public CodeBlock getCodeBlock() {
        return codeBlock.get();
    }

    public void setCodeBlock(CodeBlock codeBlock) {
        this.codeBlock.set(codeBlock);
    }

    public void addImport(ImportStatement importStatement) {
        imports.add(importStatement);
    }
}
