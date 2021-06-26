package util.string.outline;

import settings.Settings;
import util.string.Sequence;

public abstract class Segment {
    public static final int MAX_LIMIT = 0;
    protected int charLimit = Settings.defaultFoldedCharLimit();
    
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
}