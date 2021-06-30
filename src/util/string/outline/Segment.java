package util.string.outline;

import settings.Settings;
import util.string.Joiner;
import util.string.Sequence;

/**
 * An abstract part of an outline. Objects extending this type are expected to
 * produce a stringified representation of its contents formatted with
 * appropriate indentation.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public abstract class Segment {
    /**The maximum number representing an unsigned limit.*/
    public static final int MAX_LIMIT = 0;
    protected int charLimit = Settings.defaultFoldedCharLimit();
    
    /**@return The size of this segment if it is folded.*/
    public abstract int size();
    
    /**@return The maximum number of characters per folded line (unsigned).*/
    public int charLimit() {return charLimit;}
    /**
     * @param charLimit The maximum number of characters per folded line (unsigned).
     * 
     * @return <code>this</code>
     */
    public Segment charLimit(final int charLimit) {
        this.charLimit = charLimit;
        return this;
    }
    
    /**
     * Determines if this segment should be expanded without considering any
     * children.
     */
    protected boolean shouldExpand() {
        return charLimit() != MAX_LIMIT &&
               Integer.compareUnsigned(size(),charLimit()) > 0;
    }
    
    /**Determines if this Segment should expand by checking its children.*/
    protected abstract boolean firstPass();
    /**Converts this segment into an array of lines.*/
    protected abstract Sequence[] secondPass();
    
    /**Converts this segment into a sequence.*/
    public Sequence concat() {
        firstPass();
        return Joiner.join('\n',secondPass());
    }
}