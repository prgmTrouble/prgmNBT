package util.string.outline;

import util.string.Sequence;

/**
 * A {@linkplain Segment} which represents a raw {@linkplain Sequence}.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class ValueSegment extends Segment {
    private final Sequence value;
    
    /**
     * Creates a value segment.
     * 
     * @param value This segment's value. Blank and <code>null</code> values yield a literal
     *              <code>"null"</code>.
     */
    public ValueSegment(Sequence value) {
        super();
        this.value = value == null || (value = value.stripLeading()).isEmpty()
                ? Sequence.EMPTY
                : value.stripTailing();
    }
    
    @Override public int size() {return value.length();}
    
    @Override protected boolean firstPass() {return false;}
    @Override protected Sequence[] secondPass() {return new Sequence[] {value};}
}