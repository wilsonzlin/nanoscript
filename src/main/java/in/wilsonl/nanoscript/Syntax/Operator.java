package in.wilsonl.nanoscript.Syntax;

import in.wilsonl.nanoscript.Parsing.TokenType;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Utils.MatcherTreeNode;

import static in.wilsonl.nanoscript.Parsing.TokenType.*;
import static in.wilsonl.nanoscript.Syntax.Operator.PrecedenceCounter.dec;
import static in.wilsonl.nanoscript.Syntax.Operator.PrecedenceCounter.get;

public enum Operator {

  ACCESSOR(dec()),
  NULL_ACCESSOR(get()),
  LOOKUP(get(), Associativity.LEFT, Arity.UNARY),
  NULL_LOOKUP(get(), Associativity.LEFT, Arity.UNARY),
  CALL(get(), Associativity.LEFT, Arity.UNARY),
  NULL_CALL(get(), Associativity.LEFT, Arity.UNARY),

  AWAIT(dec(), Associativity.RIGHT, Arity.UNARY),

  EXPONENTIATE(dec(), Associativity.RIGHT),

  BIT_NOT(dec(), Associativity.RIGHT, Arity.UNARY),

  MEASURE(dec(), Associativity.RIGHT, Arity.UNARY),

  MULTIPLY(dec()),
  DIVIDE(get()),
  MODULO(get()),

  PLUS(dec()),
  MINUS(get()),

  BIT_LSHIFT(dec()),
  BIT_RSHIFT(get()),
  BIT_ARSHIFT(get()),

  BIT_AND(dec()),

  BIT_XOR(dec()),

  BIT_OR(dec()),

  IN(dec()),
  NOT_IN(get()),
  EMPTY(get(), Associativity.RIGHT, Arity.UNARY),
  IS(get()),
  IS_NOT(get()),
  EQ(get()),
  NEQ(get()),
  LT(get()),
  LEQ(get()),
  GT(get()),
  GEQ(get()),
  SPACESHIP(get()),
  TYPEOF(get()),
  NOT_TYPEOF(get()),

  NOT(dec(), Associativity.RIGHT, Arity.UNARY),

  AND(dec()),

  OR(dec()),

  NULL_COALESCING(dec(), Associativity.RIGHT),

  EMPTY_COALESCING(dec(), Associativity.RIGHT),

  YIELD(dec(), Associativity.RIGHT, Arity.UNARY);

  static class PrecedenceCounter {
    private static int value = 17;
    static int get() {
      return value;
    }

    static int dec() {
      return --value;
    }
  }

  private static final MatcherTreeNode<TokenType, Operator> OPERATOR_TREE_ROOT_NODE = _createOperatorTreeNode();

  private final int precedence;
  private final Associativity associativity;
  private final Arity arity;

  Operator (int precedence, Associativity associativity, Arity arity) {
    this.precedence = precedence;
    this.associativity = associativity;
    this.arity = arity;
  }

  Operator (int precedence, Associativity associativity) {
    this(precedence, associativity, Arity.BINARY);
  }

  Operator (int precedence) {
    this(precedence, Associativity.LEFT, Arity.BINARY);
  }

  private static MatcherTreeNode<TokenType, Operator> _createOperatorTreeNode () {
    MatcherTreeNode<TokenType, Operator> root = new MatcherTreeNode<>();

    root.addSequence(ACCESSOR, T_ACCESSOR);
    root.addSequence(NULL_ACCESSOR, T_NULL_ACCESSOR);
    root.addSequence(LOOKUP, T_LOOKUP);
    root.addSequence(NULL_LOOKUP, T_NULL_LOOKUP);
    root.addSequence(CALL, T_CALL);
    root.addSequence(NULL_CALL, T_NULL_CALL);

    root.addSequence(BIT_NOT, T_BIT_NOT);

    root.addSequence(MEASURE, T_MEASURE);

    root.addSequence(EXPONENTIATE, T_EXPONENTIATE);

    root.addSequence(MULTIPLY, T_MULTIPLY);
    root.addSequence(DIVIDE, T_DIVIDE);
    root.addSequence(MODULO, T_MODULO);

    root.addSequence(PLUS, T_PLUS);
    root.addSequence(MINUS, T_MINUS);

    root.addSequence(BIT_LSHIFT, T_BIT_LSHIFT);
    root.addSequence(BIT_RSHIFT, T_BIT_RSHIFT);
    root.addSequence(BIT_ARSHIFT, T_BIT_ARSHIFT);

    root.addSequence(BIT_AND, T_BIT_AND);
    root.addSequence(BIT_OR, T_BIT_OR);
    root.addSequence(BIT_XOR, T_BIT_XOR);

    root.addSequence(IN, T_KEYWORD_IN);
    root.addSequence(NOT_IN, T_KEYWORD_NOT, T_KEYWORD_IN);
    root.addSequence(EMPTY, T_KEYWORD_EMPTY);
    root.addSequence(IS, T_KEYWORD_IS);
    root.addSequence(IS_NOT, T_KEYWORD_IS, T_KEYWORD_NOT);
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

  public static Operator parseOperator (Tokens tokens) {
    Operator operator = OPERATOR_TREE_ROOT_NODE.match(tokens);
    if (operator == null) {
      throw tokens.constructMalformedSyntaxException("Expected an operator");
    }
    return operator;
  }

  public static boolean isOperatorToken (TokenType token) {
    return OPERATOR_TREE_ROOT_NODE.hasChild(token);
  }

  public int getPrecedence () {
    return precedence;
  }

  public Associativity getAssociativity () {
    return associativity;
  }

  public Arity getArity () {
    return arity;
  }

  public enum Associativity {
    LEFT, RIGHT
  }

  public enum Arity {
    UNARY, BINARY
  }

}
