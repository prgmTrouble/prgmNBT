package util.string.outline;

import util.string.Sequence;

public abstract class Segment {
    public static final int MAX_LIMIT = 0;
    protected int charLimit = MAX_LIMIT; //TODO put default in settings.
    
    /**@return The size of this segment if it is folded.*/
    public abstract int size();
    public int charLimit() {return charLimit;}
    public Segment charLimit(final int limit) {
        this.charLimit = limit;
        return this;
    }
    
    protected boolean shouldExpand() {
        return charLimit() != MAX_LIMIT &&
               Integer.compareUnsigned(size(),charLimit()) > 0;
    }
    
    protected abstract boolean firstPass();
    protected abstract Sequence[] secondPass();
    //TODO
    // first pass:
    //     evaluate each child's first pass
    //     determine if this sequence is expanded
    //
    // second pass:
    //     apply rule for open
    //     evaluate each child's second pass
    //     apply rule for close
}