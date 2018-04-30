package in.wilsonl.nanoscript.Syntax;

import in.wilsonl.nanoscript.Exception.UnexpectedEndOfCodeException;
import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Statement.BreakStatement;
import in.wilsonl.nanoscript.Syntax.Statement.CaseStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ClassStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ConditionalBranchesStatement;
import in.wilsonl.nanoscript.Syntax.Statement.CreateStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ExportStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ExpressionStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ForStatement;
import in.wilsonl.nanoscript.Syntax.Statement.LoopStatement;
import in.wilsonl.nanoscript.Syntax.Statement.NextStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ReturnStatement;
import in.wilsonl.nanoscript.Syntax.Statement.SetStatement;
import in.wilsonl.nanoscript.Syntax.Statement.Statement;
import in.wilsonl.nanoscript.Syntax.Statement.SuperStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ThrowStatement;
import in.wilsonl.nanoscript.Syntax.Statement.TryStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public class CodeBlock {
  private static final Map<TokenType, Function<Tokens, Statement>> PARSERS = _createParsersMap();
  // <body> is popped
  private final List<Statement> body = new ArrayList<>();

  private static Map<TokenType, Function<Tokens, Statement>> _createParsersMap () {
    Map<TokenType, Function<Tokens, Statement>> map = new HashMap<>();

    map.put(T_KEYWORD_BREAK, BreakStatement::parseBreakStatement);
    map.put(T_KEYWORD_CASE, CaseStatement::parseCaseStatement);
    map.put(T_KEYWORD_CLASS, ClassStatement::parseClassStatement);
    map.put(T_KEYWORD_IF, ConditionalBranchesStatement::parseConditionalBranchesStatement);
    map.put(T_KEYWORD_CREATE, CreateStatement::parseCreateStatement);
    map.put(T_KEYWORD_EXPORT, ExportStatement::parseExportStatement);
    map.put(T_KEYWORD_FOR, ForStatement::parseForStatement);
    map.put(T_KEYWORD_WHILE, LoopStatement::parseWhileStatement);
    map.put(T_KEYWORD_UNTIL, LoopStatement::parseUntilStatement);
    map.put(T_KEYWORD_NEXT, NextStatement::parseNextStatement);
    map.put(T_KEYWORD_RETURN, ReturnStatement::parseReturnStatement);
    map.put(T_KEYWORD_SET, SetStatement::parseSetStatement);
    map.put(T_KEYWORD_SUPER, SuperStatement::parseSuperStatement);
    map.put(T_KEYWORD_THROW, ThrowStatement::parseThrowStatement);
    map.put(T_KEYWORD_TRY, TryStatement::parseTryStatement);

    return map;
  }

  public static CodeBlock parseCodeBlock (Tokens tokens, TokenType breakOn) {
    return parseCodeBlock(tokens, new AcceptableTokenTypes(breakOn));
  }

  public static CodeBlock parseCodeBlock (Tokens tokens, AcceptableTokenTypes breakOn) {
    CodeBlock codeBlock = new CodeBlock();

    while (true) {
      TokenType nextTokenType;
      try {
        nextTokenType = tokens.peekType();
      } catch (UnexpectedEndOfCodeException ueoce) {
        break;
      }

      if (breakOn.has(nextTokenType)) {
        break;
      }

      Statement statement;

      if (PARSERS.containsKey(nextTokenType)) {
        statement = PARSERS.get(nextTokenType).apply(tokens);
      } else {
        // Handles all other possibilities, and throws when unknown
        statement = ExpressionStatement.parseExpressionStatement(tokens);
      }

      codeBlock.pushStatement(statement);
    }

    return codeBlock;
  }

  public void pushStatement (Statement statement) {
    body.add(statement);
  }

  public List<Statement> getBody () {
    return body;
  }

  public boolean isEmpty () {
    return body.isEmpty();
  }
}
