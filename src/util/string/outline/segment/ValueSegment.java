package util.string.outline.segment;

import util.string.Sequence;

public class ValueSegment extends Segment {
    private final Sequence value;
    public ValueSegment(final Sequence value) {
        super();
        this.value = value;
    }
    
    @Override public int size() {return value.length();}
    
    @Override public void onExpand() {}
}