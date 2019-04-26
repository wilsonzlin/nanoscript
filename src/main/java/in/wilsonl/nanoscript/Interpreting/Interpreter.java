package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinFunction;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSMap;
import in.wilsonl.nanoscript.Interpreting.Evaluator.CodeBlockEvaluator;
import in.wilsonl.nanoscript.Interpreting.Evaluator.EvaluationResult;
import in.wilsonl.nanoscript.Syntax.Chunk;
import in.wilsonl.nanoscript.Syntax.Identifier;
import in.wilsonl.nanoscript.Syntax.Statement.ImportStatement;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class Interpreter {
  // This is a static class
  private Interpreter () {
  }

  public static Exports interpret (SourceYielder yielder, Chunk chunk) {
    GlobalScope globalScope = new GlobalScope();

    /*
     *
     *  RESPONSIBILITIES:
     *
     *  - Can't check for cyclic imports here:
     *     - Method for identifying duplicates may be more complex than
     *       simply checking for the same name
     *     - Even then, it may not be a duplicate (i.e. already imported)
     */

    for (BuiltinFunction f : EnumSet.allOf(BuiltinFunction.class)) {
      globalScope.createContextSymbol(f.name(), f.getFunction());
    }
    for (BuiltinClass f : EnumSet.allOf(BuiltinClass.class)) {
      globalScope.createContextSymbol(f.name(), f.getNSClass());
    }

    for (ImportStatement st_impstmt : chunk.getImports()) {
      String from = st_impstmt.getFrom().getValue();
      Exports importables = yielder.yieldImport(from);
      Set<String> importableNames = new HashSet<>(importables.names());

      for (ImportStatement.Import st_impunit : st_impstmt.getImports()) {
        Identifier importable = st_impunit.getImportable();
        Identifier alias = st_impunit.getAlias();

        NSData value;
        String importAs;

        if (importable == null) {
          value = importables.nsMap();
          if (alias == null) {
            throw VMError.from(st_impstmt.getPosition(), BuiltinClass.SyntaxError, "A self import needs an alias");
          }
          importAs = alias.getName();

        } else {
          String importableName = importable.getName();
          if (!importableNames.remove(importableName)) {
            throw VMError.from(st_impstmt.getPosition(), BuiltinClass.ReferenceError, format("`%s` is not an export or has already been imported", importableName));
          }
          importAs = alias == null ?
            importableName :
            alias.getName();
          value = importables.get(importableName);
        }

        if (globalScope.hasContextSymbol(importAs)) {
          throw VMError.from(st_impstmt.getPosition(), BuiltinClass.ReferenceError, format("Something called `%s` already exists", importAs));
        }
        globalScope.createContextSymbol(importAs, value);
      }
    }

    EvaluationResult evaluationResult = CodeBlockEvaluator.evaluateCodeBlock(globalScope, chunk.getCodeBlock());
    if (evaluationResult != null) {
      switch (evaluationResult.getMode()) {
      case BREAK:
      case NEXT:
        throw VMError.from(BuiltinClass.SyntaxError, "Invalid break or next statement");

      case RETURN:
        throw VMError.from(BuiltinClass.SyntaxError, "Can't return from top level");
      }
    }

    return globalScope.consumeExports();
  }
}
