package in.wilsonl.nanoscript.Syntax;

import in.wilsonl.nanoscript.Exception.UnexpectedEndOfCodeException;
import in.wilsonl.nanoscript.Parsing.AcceptableTokenTypes;
import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Statement.BreakStatement;
import in.wilsonl.nanoscript.Syntax.Statement.CaseStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ConditionalBranchesStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ExpressionStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ForStatement;
import in.wilsonl.nanoscript.Syntax.Statement.LoopStatement;
import in.wilsonl.nanoscript.Syntax.Statement.NextStatement;
import in.wilsonl.nanoscript.Syntax.Statement.ReturnStatement;
import in.wilsonl.nanoscript.Syntax.Statement.Statement;
import in.wilsonl.nanoscript.Syntax.Statement.ThrowStatement;
import in.wilsonl.nanoscript.Syntax.Statement.VariableDeclarationStatement;

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

    private static Map<TokenType, Function<Tokens, Statement>> _createParsersMap() {
        Map<TokenType, Function<Tokens, Statement>> map = new HashMap<>();

        map.put(T_KEYWORD_BREAK, BreakStatement::parseBreakStatement);
        map.put(T_KEYWORD_CASE, CaseStatement::parseCaseStatement);
        map.put(T_KEYWORD_IF, ConditionalBranchesStatement::parseConditionalBranchesStatement);
        map.put(T_KEYWORD_FOR, ForStatement::parseForStatement);
        map.put(T_KEYWORD_WHILE, LoopStatement::parseWhileStatement);
        map.put(T_KEYWORD_UNTIL, LoopStatement::parseUntilStatement);
        map.put(T_KEYWORD_NEXT, NextStatement::parseNextStatement);
        map.put(T_KEYWORD_RETURN, ReturnStatement::parseReturnStatement);
        map.put(T_KEYWORD_THROW, ThrowStatement::parseThrowStatement);

        return map;
    }

    public static CodeBlock parseCodeBlock(Tokens tokens, TokenType breakOn) {
        return parseCodeBlock(tokens, new AcceptableTokenTypes(breakOn));
    }

    public static CodeBlock parseCodeBlock(Tokens tokens, AcceptableTokenTypes breakOn) {
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
            } else if (nextTokenType == T_IDENTIFIER || nextTokenType == T_KEYWORD_SELF) {
                TokenType tokenTypeAfter = tokens.peek(2).getType();
                if (tokenTypeAfter == T_INITIALISE) {
                    // If <nextTokenType> is `self`, this will throw exception
                    statement = VariableDeclarationStatement.parseVariableDeclarationStatement(tokens);
                } else {
                    // Handles all other possibilities, and throws when unknown
                    statement = ExpressionStatement.parseExpressionStatement(tokens);
                }
            } else {
                throw tokens.constructMalformedSyntaxException("Unknown statement");
            }

            codeBlock.pushStatement(statement);
        }

        return codeBlock;
    }

    public void pushStatement(Statement statement) {
        body.add(statement);
    }

    public <T extends Statement> Statement popStatementIfIsInstance(Class<T> type) {
        Statement last = body.remove(body.size() - 1);
        if (type.isInstance(last)) {
            return last;
        } else {
            body.add(last);
            return null;
        }
    }

    public boolean isEmpty() {
        return body.isEmpty();
    }
}
