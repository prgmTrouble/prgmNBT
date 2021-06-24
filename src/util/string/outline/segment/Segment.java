package util.string.outline.segment;

public abstract class Segment {
    protected int charLimit = Integer.MAX_VALUE;
    
    public abstract int size();
    public int charLimit() {return charLimit;}
    public Segment charLimit(final int limit) {
        this.charLimit = limit;
        return this;
    }
    
    public abstract void onExpand();
    protected boolean shouldExpand() {return size() > charLimit();}
    public boolean expand() {
        if(shouldExpand()) {
            onExpand();
            return true;
        }
        return false;
    }
}