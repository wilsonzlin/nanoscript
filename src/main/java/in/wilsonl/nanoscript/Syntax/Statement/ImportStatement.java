package in.wilsonl.nanoscript.Syntax.Statement;

import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Expression.Literal.LiteralStringExpression;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.SetOnce;

import java.util.List;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class ImportStatement extends Statement {
    // Probably doesn't need to be in order, but just to be safe...
    private final List<Import> imports = new ROList<>();
    private final SetOnce<LiteralStringExpression> from = new SetOnce<>();

    // TODO Fix import statement syntax where asterisk on end of line means that next identifier is incorrectly parsed

    public static ImportStatement parseImportStatement(Tokens tokens) {
        ImportStatement statement = new ImportStatement();

        tokens.require(T_KEYWORD_FROM);

        statement.setFrom(LiteralStringExpression.parseLiteralStringExpression(tokens));

        tokens.require(T_KEYWORD_IMPORT);

        do {
            Identifier importableLeft = null;
            boolean importableWildcard = false;
            Identifier importableRight = null;

            Pattern importable;

            Identifier aliasLeft = null;
            boolean aliasWildcard = false;
            Identifier aliasRight = null;

            Pattern alias;

            if (tokens.skipIfNext(T_KEYWORD_SELF)) {
                importable = null;
            } else {
                importableLeft = Identifier.acceptIdentifier(tokens);
                importableWildcard = tokens.skipIfNext(T_MULTIPLY);
                if (importableWildcard) {
                    importableRight = Identifier.acceptIdentifier(tokens);
                }

                try {
                    importable = new Pattern(importableLeft, importableWildcard, importableRight);
                } catch (IllegalArgumentException iae) {
                    throw tokens.constructMalformedSyntaxException(iae.getMessage());
                }
            }

            if (!tokens.skipIfNext(T_KEYWORD_AS)) {
                alias = null;
            } else {
                aliasLeft = Identifier.acceptIdentifier(tokens);
                aliasWildcard = tokens.skipIfNext(T_MULTIPLY);
                if (aliasWildcard) {
                    aliasRight = Identifier.acceptIdentifier(tokens);
                }

                try {
                    alias = new Pattern(aliasLeft, aliasWildcard, aliasRight);
                } catch (IllegalArgumentException iae) {
                    throw tokens.constructMalformedSyntaxException(iae.getMessage());
                }
            }

            try {
                statement.addImport(new Import(importable, alias));
            } catch (IllegalArgumentException iae) {
                throw tokens.constructMalformedSyntaxException(iae.getMessage());
            }
        } while (tokens.skipIfNext(T_COMMA));

        return statement;
    }

    public LiteralStringExpression getFrom() {
        return this.from.get();
    }

    public void setFrom(LiteralStringExpression from) {
        this.from.set(from);
    }

    public void addImport(Import imp) {
        imports.add(imp);
    }

    public static class Import {
        private final Pattern importable; // Can be null (i.e. import the class)
        private final Pattern alias; // Can be null (i.e. no alias)

        public Import(Pattern importable, Pattern alias) {
            if (alias != null) {
                if (importable != null && importable.hasWildcard() && !alias.hasWildcard()) {
                    throw new IllegalArgumentException("Alias pattern does not have wildcard but importable does");
                }
                if ((importable == null || !importable.hasWildcard()) && alias.hasWildcard()) {
                    throw new IllegalArgumentException("Alias pattern has wildcard but importable does not");
                }
            }

            this.importable = importable;
            this.alias = alias;
        }
    }

    public static class Pattern {
        private final Identifier left;
        private final boolean hasWildcard;
        private final Identifier right;

        public Pattern(Identifier left, boolean hasWildcard, Identifier right) {
            if (!hasWildcard) {
                // Can only be <left>, not none, not both, not <right>
                if (left == null || right != null) {
                    throw new IllegalArgumentException("Invalid pattern");
                }
            }

            this.left = left;
            this.hasWildcard = hasWildcard;
            this.right = right;
        }

        public Pattern(Identifier left) {
            this(left, false, null);
        }

        public boolean hasWildcard() {
            return hasWildcard;
        }
    }
}
