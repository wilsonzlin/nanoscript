package in.wilsonl.nanoscript.Lexing;

import in.wilsonl.nanoscript.Exception.MalformedSyntaxException;
import in.wilsonl.nanoscript.Exception.UnexpectedEndOfCodeException;
import in.wilsonl.nanoscript.Utils.Matchable;
import in.wilsonl.nanoscript.Utils.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public class Code implements Matchable<Character> {

  private static final int MAX_HISTORICAL_BUFFER_SIZE = 1024;

  private final BufferedReader inputStream;
  /*
   *
   *          CONSUMED | UPCOMING
   *   <lastReadChars> | <returnBuffer> <inputStream>
   *
   */
  private final Deque<Character> returnBuffer = new ArrayDeque<>();
  // Keep a maximum of MAX_HISTORICAL_BUFFER_SIZE last characters
  // (i.e. can only call `backUp` sequentially at most 10 times)
  private final Deque<Character> lastReadChars = new ArrayDeque<>(MAX_HISTORICAL_BUFFER_SIZE);
  private int currentLineNo = 1;
  private int currentColNo = 0;

  public Code (InputStream inputStream) {
    this.inputStream = new BufferedReader(new InputStreamReader(inputStream));
  }

  private char readChar () {
    while (lastReadChars.size() > MAX_HISTORICAL_BUFFER_SIZE) {
      lastReadChars.removeFirst();
    }
    char nextChar;
    try {
      nextChar = returnBuffer.removeFirst();
    } catch (NoSuchElementException nsee) {
      int nextCharAsInt;
      try {
        nextCharAsInt = inputStream.read();
      } catch (IOException e) {
        throw new UnexpectedEndOfCodeException();
      }
      if (nextCharAsInt == -1) {
        throw new UnexpectedEndOfCodeException();
      }
      nextChar = (char) nextCharAsInt;
    }
    lastReadChars.addLast(nextChar);
    switch (nextChar) {
    case '\n':
      if (lastReadChars.peekLast() == '\r') {
        break;
      }
    case '\r':
      currentLineNo++;
      currentColNo = 0;
      break;

    default:
      currentColNo++;
    }
    return nextChar;
  }

  public Position getCurrentPosition () {
    return new Position(currentLineNo, currentColNo);
  }

  public void backUp () {
    char ret = lastReadChars.removeLast();
    switch (ret) {
    case '\r':
      if (returnBuffer.peekFirst() == '\n') {
        break;
      }
    case '\n':
      currentLineNo--;
      currentColNo = -1;
      break;

    default:
      currentColNo--;
    }
    returnBuffer.addFirst(ret);
  }

  public String acceptOptional (char c) {
    char next = accept();
    if (c != next) {
      backUp();
      return "";
    }
    return "" + next;
  }

  public String acceptOptional (AcceptableChars chars) {
    char next = accept();
    if (!chars.has(next)) {
      backUp();
      return "";
    }
    return "" + next;
  }

  public String acceptGreedy (AcceptableChars chars) {
    StringBuilder res = new StringBuilder();
    while (true) {
      char next = accept();
      if (!chars.has(next)) {
        backUp();
        break;
      }
      res.append(next);
    }
    return res.toString();
  }

  public String acceptUntil (AcceptableChars chars) {
    StringBuilder res = new StringBuilder();
    while (true) {
      char next = accept();
      if (chars.has(next)) {
        backUp();
        break;
      }
      res.append(next);
    }
    return res.toString();
  }

  public void skipGreedyBeforeEnd (AcceptableChars chars) {
    while (true) {
      char next = accept();
      if (!chars.has(next)) {
        backUp();
        break;
      }
    }
  }

  public void skipUntil (char c) {
    while (true) {
      char next = accept();
      if (c == next) {
        backUp();
        break;
      }
    }
  }

  public char accept () {
    return readChar();
  }

  public void skip () {
    readChar();
  }

  public boolean skipIfNext (char c) {
    char nextChar = readChar();
    if (nextChar == c) {
      return true;
    } else {
      backUp();
      return false;
    }
  }

  public char peek () {
    char nextChar = readChar();
    backUp();
    return nextChar;
  }

  public char peek (int offset) {
    if (offset < 1) {
      throw new IllegalArgumentException("Invalid offset");
    }

    char nextChar = 0;
    for (int i = 0; i < offset; i++) {
      nextChar = readChar();
    }
    for (int i = 0; i < offset; i++) {
      backUp();
    }

    return nextChar;
  }

  public MalformedSyntaxException constructMalformedSyntaxException (String message) {
    Position pos = getCurrentPosition();

    return new MalformedSyntaxException(message, pos);
  }

  @Override
  public Character matcherConsume () {
    return accept();
  }

  @Override
  public void matcherReverse () {
    backUp();
  }

}
