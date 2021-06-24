package util.string;

/**
 * An interface for objects which manipulate character sequences.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public interface Builder {
    /**
     * @param c Character sequence to append.
     * 
     * @return <code>this</code>
     */
    public default Builder push(final char[] c) {return c == null? this : push(new Sequence(c));}
    public default Builder push(final Builder b) {return b == null? this : push(b.concat());}
    public Builder push(final Sequence s);
    /**@return The fully assembled character sequence.*/
    public Sequence concat();
}