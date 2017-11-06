package in.wilsonl.nanoscript.Exception;

import in.wilsonl.nanoscript.Utils.Utils;

import java.io.File;

public class CyclicImportException extends Exception {
    public CyclicImportException(File[] cycle) {
        super(buildMessage(cycle));
    }

    private static String buildMessage(File[] cycle) {
        return "Cyclic import detected:\n\n" + Utils.join("\n  ↓\n", cycle);
    }
}
