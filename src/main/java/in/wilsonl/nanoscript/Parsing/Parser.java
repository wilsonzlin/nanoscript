package in.wilsonl.nanoscript.Parsing;

import in.wilsonl.nanoscript.Syntax.Chunk;

public class Parser {
  public static Chunk parse (Tokens tokens) {
    return Chunk.parseChunk(tokens);
  }
}
