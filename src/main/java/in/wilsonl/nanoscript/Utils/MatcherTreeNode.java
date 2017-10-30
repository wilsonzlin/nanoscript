package in.wilsonl.nanoscript.Utils;

import in.wilsonl.nanoscript.Exception.InternalError;

import java.util.Map;
import java.util.TreeMap;

public class MatcherTreeNode<M, R> {
    protected final Map<M, MatcherTreeNode<M, R>> children = new TreeMap<>();
    protected R tail = null;

    public boolean hasTail() {
        return tail != null;
    }

    public void addSequence(M[] sequence, int start, R result) {
        M m = sequence[start];
        MatcherTreeNode<M, R> child = children.get(m);

        if (child == null) {
            child = new MatcherTreeNode<>();
            children.put(m, child);
        }

        if (start == sequence.length - 1) {
            if (child.hasTail()) {
                throw new InternalError("Duplicate matcher sequence");
            }
            child.tail = result;
        } else {
            child.addSequence(sequence, start + 1, result);
        }
    }

    public void addSequence(R result, M... sequence) {
        addSequence(sequence, 0, result);
    }

    public R match(M[] toMatch, int offset) {
        M currentUnit = toMatch[offset];
        MatcherTreeNode<M, R> child = children.get(currentUnit);
        if (child == null) {
            return tail;
        }
        return child.match(toMatch, offset + 1);
    }

    public R match(Matchable<M> toMatch) {
        M currentUnit = toMatch.matcherConsume();
        MatcherTreeNode<M, R> child = children.get(currentUnit);
        if (child == null) {
            toMatch.matcherReverse();
            return tail;
        }
        return child.match(toMatch);
    }

    public boolean hasChild(M unit) {
        return children.containsKey(unit);
    }
}
