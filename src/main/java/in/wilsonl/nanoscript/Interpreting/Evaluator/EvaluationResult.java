package in.wilsonl.nanoscript.Interpreting.Evaluator;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;

public class EvaluationResult {
    private final Mode mode;
    private final NSData value; // Can be null; if returning without a value, <value> should be NSNull.NULL

    public EvaluationResult(Mode mode, NSData value) {
        this.mode = mode;
        this.value = value;
    }

    public EvaluationResult(Mode mode) {
        this(mode, null);
    }

    public Mode getMode() {
        return mode;
    }

    public NSData getValue() {
        return value;
    }

    public enum Mode {
        BREAK, NEXT, RETURN
    }
}
