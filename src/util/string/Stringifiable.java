package util.string;

import util.string.outline.Segment;

/**
 * An interface for objects which can be serialized in a human-readable format.
 * 
 * @author prgmTrouble
 */
public interface Stringifiable {
    /**@return A representation of this object as a character sequence.*/
    public Sequence toSequence();
    
    /**
     * Appends the string representation to the builder.
     * 
     * @param b {@linkplain Builder}
     * 
     * @return <code>b</code>
     * 
     * @throws NullPointerException
     */
    @SuppressWarnings("unchecked")
    public default <B extends Builder> B appendTo(final B b) throws NullPointerException {
        return (B)b.push(toSequence());
    }
    
    /**@return An outline segment representation of this object.*/
    public Segment toSegment();
}