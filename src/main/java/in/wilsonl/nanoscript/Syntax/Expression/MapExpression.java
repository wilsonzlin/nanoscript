package in.wilsonl.nanoscript.Syntax.Expression;

import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.Position;
import in.wilsonl.nanoscript.Utils.ROList;

import java.util.List;

public class MapExpression extends Expression {
  // Order matters
  private final List<Member> members = new ROList<>();

  public MapExpression (Position position) {
    super(position);
  }

  public static MapExpression parseMapExpression (Tokens tokens) {
    Position position = tokens
      .require(TokenType.T_LEFT_BRACE)
      .getPosition();
    MapExpression map = new MapExpression(position);

    do {
      if (tokens.isNext(TokenType.T_RIGHT_BRACE)) {
        break;
      }

      Expression key = Expression.parseExpression(tokens, new AcceptableTokenTypes(TokenType.T_KEYWORD_AS));
      tokens.require(TokenType.T_KEYWORD_AS);
      Expression value = Expression.parseExpression(tokens, new AcceptableTokenTypes(TokenType.T_COMMA, TokenType.T_RIGHT_BRACE));

      map.addMember(new Member(key, value));
    } while (tokens.skipIfNext(TokenType.T_COMMA));

    tokens.require(TokenType.T_RIGHT_BRACE);

    return map;
  }

  public void addMember (Member m) {
    members.add(m);
  }

  public List<Member> getMembers () {
    return members;
  }

  public static class Member {
    private final Expression key;
    private final Expression value;

    public Member (Expression key, Expression value) {
      this.key = key;
      this.value = value;
    }

    public Expression getKey () {
      return key;
    }

    public Expression getValue () {
      return value;
    }
  }
}
