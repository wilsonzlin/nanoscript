package in.wilsonl.nanoscript.Interpreting;

public class BlockScope implements Context {
    private final ContextHelper contextHelper;

    public BlockScope(ContextHelper contextHelper) {
        this.contextHelper = contextHelper;
    }
}
