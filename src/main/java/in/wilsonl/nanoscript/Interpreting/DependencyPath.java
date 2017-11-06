package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Exception.CyclicImportException;

import java.io.File;

public class DependencyPath {
    private final File[] parts;

    public DependencyPath(File[] parts) {
        this.parts = parts;
    }

    public DependencyPath concat(File newPart) throws CyclicImportException {
        File[] newParts = new File[parts.length + 1];
        int cycleStart = -1;

        for (int i = 0; i < parts.length; i++) {
            File p = parts[i];
            if (p.equals(newPart)) {
                cycleStart = i;
            }
            newParts[i] = p;
        }
        newParts[parts.length] = newPart;

        if (cycleStart != -1) {
            File[] cycle = new File[newParts.length - cycleStart];
            System.arraycopy(newParts, cycleStart, cycle, 0, newParts.length - cycleStart);
            throw new CyclicImportException(cycle);
        }

        return new DependencyPath(newParts);
    }
}
