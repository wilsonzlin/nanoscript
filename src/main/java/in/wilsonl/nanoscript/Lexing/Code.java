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

    public Code(InputStream inputStream) {
        this.inputStream = new BufferedReader(new InputStreamReader(inputStream));
    }

    private char readChar() {
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

    public Position getCurrentPosition() {
        return new Position(currentLineNo, currentColNo);
    }

    public void backUp() {
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

    private String consumeUsingStrategy(Object acceptable, ConsumeStrategy strategy, boolean beforeEnd, boolean returnConsumedAsString) {
        AcceptableChars acceptableChars = null;
        char acceptChar = '\0';
        int acceptCount = 0;
        StringBuilder buffer = new StringBuilder();

        if (acceptable instanceof AcceptableChars) {
            if (strategy == ConsumeStrategy.COUNT) {
                throw new IllegalArgumentException("Passed AcceptableChars as acceptable but COUNT as strategy");
            }
            acceptableChars = (AcceptableChars) acceptable;

        } else if (acceptable instanceof Character) {
            if (strategy == ConsumeStrategy.COUNT) {
                throw new IllegalArgumentException("Passed character as acceptable but COUNT as strategy");
            }
            acceptChar = (char) acceptable;

        } else if (acceptable instanceof Integer) {
            if (strategy != ConsumeStrategy.COUNT) {
                throw new IllegalArgumentException("Passed integer as acceptable but not COUNT as strategy");
            }
            acceptCount = (int) acceptable;
            if (acceptCount < 1) {
                throw new IllegalArgumentException("Invalid count");
            }

        } else {
            throw new IllegalArgumentException("Invalid acceptable object");
        }

        int i = 0;
        while (true) {
            char nextChar;
            try {
                nextChar = readChar();
            } catch (UnexpectedEndOfCodeException ueoce) {
                if (beforeEnd) {
                    break;
                } else {
                    throw ueoce;
                }
            }
            buffer.append(nextChar);
            i++;

            if (strategy == ConsumeStrategy.SINGLE && i == 1) {
                break;
            }

            // This still needs to be inside the loop as beforeEnd may be true
            if (strategy == ConsumeStrategy.COUNT) {
                if (i != acceptCount) {
                    continue;
                } else {
                    break;
                }
            }

            boolean has;
            if (acceptableChars != null) {
                has = acceptableChars.has(nextChar);
            } else {
                has = acceptChar == nextChar;
            }

            if (strategy == ConsumeStrategy.UNTIL && has || (strategy == ConsumeStrategy.GREEDY || strategy == ConsumeStrategy.SINGLE) && !has) {
                backUp();
                buffer.deleteCharAt(buffer.length() - 1);
                break;
            }
        }

        if (i == 0) {
            return "";
        }

        String accepted = null;
        if (returnConsumedAsString) {
            accepted = buffer.toString();
        }

        return accepted;
    }

    public String accept(int count) {
        return consumeUsingStrategy(count, ConsumeStrategy.COUNT, false, true);
    }

    public String acceptOptional(char c) {
        return consumeUsingStrategy(c, ConsumeStrategy.SINGLE, false, true);
    }

    public String acceptOptional(AcceptableChars chars) {
        return consumeUsingStrategy(chars, ConsumeStrategy.SINGLE, false, true);
    }

    public String acceptGreedy(AcceptableChars chars) {
        return consumeUsingStrategy(chars, ConsumeStrategy.GREEDY, false, true);
    }

    public String acceptUntil(AcceptableChars chars) {
        return consumeUsingStrategy(chars, ConsumeStrategy.UNTIL, false, true);
    }

    public String acceptUntilBeforeEnd(AcceptableChars chars) {
        return consumeUsingStrategy(chars, ConsumeStrategy.UNTIL, true, true);
    }

    public String acceptUntil(char c) {
        return consumeUsingStrategy(c, ConsumeStrategy.UNTIL, false, true);
    }

    public String acceptUntilBeforeEnd(char c) {
        return consumeUsingStrategy(c, ConsumeStrategy.UNTIL, true, true);
    }

    public void skip(int count) {
        consumeUsingStrategy(count, ConsumeStrategy.COUNT, false, false);
    }

    public void skipGreedy(AcceptableChars chars) {
        consumeUsingStrategy(chars, ConsumeStrategy.GREEDY, false, false);
    }

    public void skipGreedyBeforeEnd(AcceptableChars chars) {
        consumeUsingStrategy(chars, ConsumeStrategy.GREEDY, true, false);
    }

    public void skipUntil(AcceptableChars chars) {
        consumeUsingStrategy(chars, ConsumeStrategy.UNTIL, false, false);
    }

    public void skipUntil(char c) {
        consumeUsingStrategy(c, ConsumeStrategy.UNTIL, false, false);
    }

    public char accept() {
        return readChar();
    }

    public void skip() {
        readChar();
    }

    public boolean skipIfNext(char c) {
        char nextChar = readChar();
        if (nextChar == c) {
            return true;
        } else {
            backUp();
            return false;
        }
    }

    public char peek() {
        char nextChar = readChar();
        backUp();
        return nextChar;
    }

    public char peek(int offset) {
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

    public MalformedSyntaxException constructMalformedSyntaxException(String message) {
        Position pos = getCurrentPosition();

        return new MalformedSyntaxException(message, pos);
    }

    @Override
    public Character matcherConsume() {
        return accept();
    }

    @Override
    public void matcherReverse() {
        backUp();
    }

    private enum ConsumeStrategy {
        COUNT, SINGLE, GREEDY, UNTIL
    }

}
