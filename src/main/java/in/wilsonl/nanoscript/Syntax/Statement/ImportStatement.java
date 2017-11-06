package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralStringExpression;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class ImportStatement extends Statement {
    // Probably doesn't need to be in order, but just to be safe...
    private final List<Import> imports = new ROList<>();
    private final SetOnce<LiteralStringExpression> from = new SetOnce<>();

    public ImportStatement(Position position) {
        super(position);
    }

    public static ImportStatement parseImportStatement(Tokens tokens) {
        Position position = tokens.require(T_KEYWORD_FROM).getPosition();
        ImportStatement statement = new ImportStatement(position);

        statement.setFrom(LiteralStringExpression.parseLiteralStringExpression(tokens));

        tokens.require(T_KEYWORD_IMPORT);

        do {
            Identifier importable;
            Identifier alias;

            if (tokens.skipIfNext(T_KEYWORD_SELF)) {
                importable = null;
            } else {
                importable = Identifier.requireIdentifier(tokens);
            }

            if (!tokens.skipIfNext(T_KEYWORD_AS)) {
                alias = null;
            } else {
                alias = Identifier.requireIdentifier(tokens);
            }

            statement.addImport(new Import(importable, alias));
        } while (tokens.skipIfNext(T_COMMA));

        return statement;
    }

    public LiteralStringExpression getFrom() {
        return this.from.get();
    }

    public void setFrom(LiteralStringExpression from) {
        this.from.set(from);
    }

    public List<Import> getImports() {
        return imports;
    }

    public void addImport(Import imp) {
        imports.add(imp);
    }

    public static class Import {
        private final Identifier importable; // Can be null (i.e. import all in a map)
        private final Identifier alias; // Can be null (i.e. no alias)

        public Import(Identifier importable, Identifier alias) {
            this.importable = importable;
            this.alias = alias;
        }

        public Identifier getImportable() {
            return importable;
        }

        public Identifier getAlias() {
            return alias;
        }
    }
}
