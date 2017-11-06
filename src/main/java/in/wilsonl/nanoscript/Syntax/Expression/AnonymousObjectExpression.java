package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

public class AnonymousObjectExpression extends Expression {
    // Order matters
    private final List<Member> members = new ROList<>();

    public AnonymousObjectExpression(Position position) {
        super(position);
    }

    public static AnonymousObjectExpression parseAnonymousObjectExpression(Tokens tokens) {
        Position position = tokens.require(TokenType.T_BRACE_LEFT).getPosition();
        AnonymousObjectExpression anon = new AnonymousObjectExpression(position);

        do {
            if (tokens.isNext(TokenType.T_BRACE_RIGHT)) {
                break;
            }

            Identifier key = Identifier.requireIdentifier(tokens);
            tokens.require(TokenType.T_KEYWORD_AS);
            Expression value = Expression.parseExpression(tokens, new AcceptableTokenTypes(TokenType.T_COMMA, TokenType.T_BRACE_RIGHT));

            anon.addMember(new Member(key, value));
        } while (tokens.skipIfNext(TokenType.T_COMMA));

        tokens.require(TokenType.T_BRACE_RIGHT);

        return anon;
    }

    public void addMember(Member m) {
        members.add(m);
    }

    public List<Member> getMembers() {
        return members;
    }

    public static class Member {
        private final Identifier key;
        private final Expression value;

        public Member(Identifier key, Expression value) {
            this.key = key;
            this.value = value;
        }

        public Identifier getKey() {
            return key;
        }

        public Expression getValue() {
            return value;
        }
    }
}
