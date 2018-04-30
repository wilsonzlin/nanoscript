package in.wilsonl.nanoscript;

import in.wilsonl.nanoscript.Exception.CyclicImportException;
import in.wilsonl.nanoscript.Exception.ExceptionFromSource;
import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Exception.NoSuchModuleException;
import in.wilsonl.nanoscript.Interpreting.DependencyPath;
import in.wilsonl.nanoscript.Interpreting.Exports;
import in.wilsonl.nanoscript.Interpreting.Interpreter;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Lexing.Code;
import in.wilsonl.nanoscript.Lexing.Lexer;
import in.wilsonl.nanoscript.Parsing.Parser;
import in.wilsonl.nanoscript.Parsing.Tokens;
import in.wilsonl.nanoscript.Syntax.Chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
  public static void main (String[] args) {
    String filePath;
    try {
      filePath = args[0];
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      throw new IllegalArgumentException("No file provided as first argument");
    }
    // WARNING: user.dir is the working directory, NOT the user's home directory
    String cwd = System.getProperty("user.dir");

    Environment env = new Environment();
    try {
      env.loadClass(new DependencyPath(new File[0]), filePath, new File(cwd));
    } catch (CyclicImportException | VMError e) {
      throw new ExceptionFromSource(filePath, e);
    } catch (FileNotFoundException | NoSuchModuleException e) {
      throw new IllegalArgumentException("Can't find " + filePath);
    }
  }

  private static class Environment {
    // Scripts are only loaded once, so changes to globals in that script
    // will change it for all
    private final Map<File, Exports> loaded = new HashMap<>();

    // <relativeToDir> should be a File instance containing a canonical
    // path to the directory containing the script doing the importing
    private static File resolveImportName (String name, File relativeToDir) throws NoSuchModuleException {
      List<File> tested = new ArrayList<>();

      String relativeToDirPath;
      try {
        if (!relativeToDir.isDirectory()) {
          throw new InternalStateError("Working directory path is not a directory");
        }
        relativeToDirPath = relativeToDir.getCanonicalPath();
      } catch (SecurityException | IOException se) {
        throw new InternalStateError("Permission denied to access possible directory " + relativeToDir.getPath());
      }

      // TODO

      File relFile = new File(relativeToDirPath + '/' + name);
      tested.add(relFile);
      try {
        if (relFile.isFile()) {
          return relFile.getCanonicalFile();
        }
      } catch (SecurityException | IOException ignored) {
      }

      File absFile = new File(name);
      tested.add(absFile);
      try {
        if (absFile.isAbsolute()) {
          return absFile.getCanonicalFile();
        }
      } catch (SecurityException | IOException ignored) {
      }

      throw new NoSuchModuleException(name, tested);
    }

    public Exports loadClass (DependencyPath prev, String importName, File prevRelTo) throws CyclicImportException, FileNotFoundException, NoSuchModuleException {
      File filePath = resolveImportName(importName, prevRelTo);

      if (!loaded.containsKey(filePath)) {
        DependencyPath curr = prev.concat(filePath);
        File currRelTo = filePath.getParentFile();

        FileInputStream sourceFile = new FileInputStream(filePath);

        Code code = new Code(sourceFile);

        Lexer lexer = new Lexer(code);
        Tokens tokens = new Tokens(lexer);
        Chunk parsed = Parser.parse(tokens);
        Exports exports = Interpreter.interpret(name -> {
          try {
            return loadClass(curr, name, currRelTo);
          } catch (CyclicImportException | FileNotFoundException | NoSuchModuleException | VMError e) {
            throw new ExceptionFromSource(filePath.getPath(), e);
          }
        }, parsed);

        loaded.put(filePath, exports);
      }

      return loaded.get(filePath);
    }
  }
}
