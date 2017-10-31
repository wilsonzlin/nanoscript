package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.CodeBlock;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Syntax.Reference;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.ROSet;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;
import java.util.Set;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class TryStatement extends Statement {
    private final SetOnce<CodeBlock> tryBody = new SetOnce<>();
    private final List<Catch> catchBlocks = new ROList<>();

    public static TryStatement parseTryStatement(Tokens tokens) {
        TryStatement tryStatement = new TryStatement();

        tokens.require(T_KEYWORD_TRY);

        tryStatement.setTryBody(CodeBlock.parseCodeBlock(tokens, new AcceptableTokenTypes(T_KEYWORD_CATCH, T_KEYWORD_TRY_END)));

        while (tokens.skipIfNext(T_KEYWORD_CATCH)) {
            Identifier paramName = Identifier.requireIdentifier(tokens);
            tokens.require(T_COLON);
            Set<Reference> types;

            if (tokens.skipIfNext(T_MULTIPLY)) {
                types = null;
            } else {
                types = new ROSet<>();
                do {
                    types.add(Reference.parseReference(tokens));
                } while (tokens.skipIfNext(T_PLUS));
            }

            CodeBlock catchBody = CodeBlock.parseCodeBlock(tokens, new AcceptableTokenTypes(T_KEYWORD_CATCH, T_KEYWORD_TRY_END));

            tryStatement.addCatchBlock(new Catch(types, paramName, catchBody));
        }

        tokens.require(T_KEYWORD_TRY_END);

        return tryStatement;
    }

    public CodeBlock getTryBody() {
        return tryBody.get();
    }

    public void setTryBody(CodeBlock t) {
        tryBody.set(t);
    }

    public void addCatchBlock(Catch c) {
        catchBlocks.add(c);
    }

    public List<Catch> getCatchBlocks() {
        return catchBlocks;
    }

    public static class Catch {
        private final Set<Reference> types; // Can be null to catch all
        private final Identifier parameterName;
        private final CodeBlock body;

        public Catch(Set<Reference> types, Identifier parameterName, CodeBlock body) {
            this.types = types;
            this.parameterName = parameterName;
            this.body = body;
        }

        public Set<Reference> getTypes() {
            return types;
        }

        public Identifier getParameterName() {
            return parameterName;
        }

        public CodeBlock getBody() {
            return body;
        }
    }
}
