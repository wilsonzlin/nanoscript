package in.wilsonl.nanoscript.Syntax;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.MatcherTreeNode;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;

public enum Operator {

    ACCESSOR(13),
    NULL_ACCESSOR(13),
    LOOKUP(13, Associativity.LEFT, Arity.UNARY),
    NULL_LOOKUP(13, Associativity.LEFT, Arity.UNARY),
    CALL(13, Associativity.LEFT, Arity.UNARY),
    NULL_CALL(13, Associativity.LEFT, Arity.UNARY),

    HASH(11, Associativity.RIGHT, Arity.UNARY),

    EXPONENTIATE(10, Associativity.RIGHT),

    MULTIPLY(9),
    DIVIDE(9),
    MODULO(9),

    PLUS(8),
    MINUS(8),

    EQ(7),
    NEQ(7),
    LT(7),
    LEQ(7),
    GT(7),
    GEQ(7),
    SPACESHIP(7),
    TYPEOF(7),
    NOT_TYPEOF(7),

    NOT(6, Associativity.RIGHT, Arity.UNARY),

    AND(5),

    OR(4),

    NULL_COALESCING(3, Associativity.RIGHT);

    private static final MatcherTreeNode<TokenType, Operator> OPERATOR_TREE_ROOT_NODE = _createOperatorTreeNode();

    private final int precedence;
    private final Associativity associativity;
    private final Arity arity;

    Operator(int precedence, Associativity associativity, Arity arity) {
        this.precedence = precedence;
        this.associativity = associativity;
        this.arity = arity;
    }

    Operator(int precedence, Associativity associativity) {
        this(precedence, associativity, Arity.BINARY);
    }

    Operator(int precedence) {
        this(precedence, Associativity.LEFT, Arity.BINARY);
    }

    private static MatcherTreeNode<TokenType, Operator> _createOperatorTreeNode() {
        MatcherTreeNode<TokenType, Operator> root = new MatcherTreeNode<>();

        root.addSequence(ACCESSOR, T_ACCESSOR);
        root.addSequence(NULL_ACCESSOR, T_NULL_ACCESSOR);
        root.addSequence(LOOKUP, T_LOOKUP);
        root.addSequence(NULL_LOOKUP, T_NULL_LOOKUP);
        root.addSequence(CALL, T_CALL);
        root.addSequence(NULL_CALL, T_NULL_CALL);

        root.addSequence(HASH, T_HASH);

        root.addSequence(EXPONENTIATE, T_EXPONENTIATE);

        root.addSequence(MULTIPLY, T_MULTIPLY);
        root.addSequence(DIVIDE, T_DIVIDE);
        root.addSequence(MODULO, T_MODULO);

        root.addSequence(PLUS, T_PLUS);
        root.addSequence(MINUS, T_MINUS);

        root.addSequence(EQ, T_EQ);
        root.addSequence(NEQ, T_NEQ);
        root.addSequence(LT, T_CHEVRON_LEFT);
        root.addSequence(LEQ, T_LEQ);
        root.addSequence(GT, T_CHEVRON_RIGHT);
        root.addSequence(GEQ, T_GEQ);
        root.addSequence(SPACESHIP, T_SPACESHIP);
        root.addSequence(TYPEOF, T_KEYWORD_TYPEOF);
        root.addSequence(NOT_TYPEOF, T_KEYWORD_NOT, T_KEYWORD_TYPEOF);

        root.addSequence(NOT, T_KEYWORD_NOT);

        root.addSequence(AND, T_KEYWORD_AND);

        root.addSequence(OR, T_KEYWORD_OR);

        root.addSequence(NULL_COALESCING, T_NULL_COALESCING);

        return root;
    }

    public static Operator parseOperator(Tokens tokens) {
        Operator operator = OPERATOR_TREE_ROOT_NODE.match(tokens);
        if (operator == null) {
            throw tokens.constructMalformedSyntaxException("Expected an operator");
        }
        return operator;
    }

    public static boolean isOperatorToken(TokenType token) {
        return OPERATOR_TREE_ROOT_NODE.hasChild(token);
    }

    public int getPrecedence() {
        return precedence;
    }

    public Associativity getAssociativity() {
        return associativity;
    }

    public Arity getArity() {
        return arity;
    }

    public enum Associativity {
        LEFT, RIGHT
    }

    public enum Arity {
        UNARY, BINARY
    }

}
