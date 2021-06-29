package nbt.value.collection;

import java.io.DataOutput;
import java.io.IOException;
import nbt.NBT;
import nbt.exception.NBTException;
import nbt.exception.NBTParsingException;
import nbt.value.NBTValue;
import settings.Version;
import util.string.Joiner;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.JoiningSegment;
import util.string.outline.Segment;
import util.string.outline.WrappingSegment;

/**
 * An {@linkplain NBTValue} which holds other {@linkplain NBT}s.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public abstract class NBTCollection<K,V extends NBT> extends NBTValue implements Iterable<V> {
    /**Allows the parser to ignore an empty value at the end of a collection.*/
    public static final boolean TRAILING_COMMA = Version.atLeast(Version.v13w36a);
    
    /**
     * Creates an empty collection with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTCollection() {super();}
    /**
     * Creates an empty collection.
     * 
     * @param minimal {@linkplain NBTValue#minimal}
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTCollection(final boolean minimal) {super(minimal);}
    
    /**
     * Writes this collection's values.
     * 
     * @throws IOException The collection could not be written.
     */
    @Override
    public void write(final DataOutput out) throws IOException {
        for(final V v : this) v.write(out);
    }
    
    /**
     * Maps the value to the key.
     * 
     * @throws NBTException The key or value could not be set.
     */
    public abstract NBTCollection<K,V> set(final K key,final NBTValue value) throws NBTException;
    /**Gets an value mapped to the key.*/
    public abstract NBTValue get(final K key);
    /**Removes and returns the value mapped to the key.*/
    public abstract NBTValue remove(final K key);
    
    /**Gets a {@linkplain Joiner} instance to use when converting to a character sequence.*/
    protected abstract Joiner getJoiner();
    /**Gets a {@linkplain WrappingSegment} instance to use when converting to an outline.*/
    protected abstract WrappingSegment getWrapper();
    
    protected abstract Iterable<NBTValue> values();
    
    @Override
    protected Sequence complete() {
        final Joiner j = getJoiner();
        for(final V nbt : this) nbt.appendTo(j);
        return j.concat();
    }
    
    /**@return A segment containing the children in a stringified form.*/
    protected Segment getChildren() {
        final JoiningSegment s = new JoiningSegment(new Sequence(',')); //TODO customization?
        for(final NBT n : this) s.push(n.toSegment());
        return s;
    }
    @Override public Segment toSegment() {return getWrapper().child(getChildren());}
    
    @Override
    public boolean isDefault() {
        for(final NBTValue nbt : values()) if(!nbt.isDefault()) return false;
        return true;
    }
    
    protected static boolean testEmpty(final SequenceIterator i,
                                       final char close)
                                       throws NBTParsingException {
        final Character c = i.nextNonWS();
        // Reached end without finding closing character.
        if(c == null)
            throw new NBTParsingException(
                "Missing closing character '%c'"
                .formatted(close),i
            );
        // Closing character found.
        return c == close;
    }
}