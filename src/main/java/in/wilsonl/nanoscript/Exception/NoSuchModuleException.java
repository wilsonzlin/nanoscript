package in.wilsonl.nanoscript.Exception;

import in.wilsonl.nanoscript.Utils.Utils;

import java.io.File;
import java.util.List;

public class NoSuchModuleException extends Exception {
    public NoSuchModuleException(String importName, List<File> testedPaths) {
        super(String.format("The import source `%s` could not be found at any of the following locations:\n\n%s", importName, Utils.join("\n", testedPaths)));
    }
}
