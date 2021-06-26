package util.string.outline;

import util.string.Sequence;

public class ValueSegment extends Segment {
    private final Sequence value;
    public ValueSegment(final Sequence value) {
        super();
        this.value = value;
    }
    
    @Override public int size() {return value.length();}
    
    @Override protected boolean firstPass() {return false;}
    @Override protected Sequence[] secondPass() {return new Sequence[] {value};}
}