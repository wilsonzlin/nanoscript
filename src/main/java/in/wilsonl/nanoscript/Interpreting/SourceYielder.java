package in.wilsonl.nanoscript.Interpreting;

public interface SourceYielder {
    // Up to implementation whether to re-evaluate previously-
    // loaded sources
    Exports yieldImport(String name);
}
