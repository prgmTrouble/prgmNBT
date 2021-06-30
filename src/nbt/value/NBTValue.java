package nbt.value;

import nbt.NBT;
import nbt.exception.NBTConversionException;
import nbt.exception.NBTParsingException;
import nbt.value.collection.NBTArray;
import nbt.value.collection.NBTObject;
import nbt.value.number.NBTNumber;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.Segment;
import util.string.outline.ValueSegment;

/**
 * A typed {@linkplain NBT} value.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public abstract class NBTValue extends NBT {
    /**@return The {@linkplain ValueType} associated with this NBT value.*/
    public abstract ValueType type();
    
    /**
     * Constructs a value with default minimalism.
     * 
     * @see NBT#NBT()
     */
    public NBTValue() {super();}
    /**
     * Constructs an {@linkplain NBTValue}.
     * 
     * @param minimal {@linkplain #minimal}
     * 
     * @see NBT#NBT(boolean)
     */
    public NBTValue(final boolean minimal) {super(minimal);}
    
    /**@return The non-minimal representation of this value.*/
    protected abstract Sequence complete();
    /**@return The minimal representation of this value.*/
    protected abstract Sequence minimal();
    
    /**@return A representation of this NBT as a character sequence.*/
    public Sequence toSequence() {return minimal? minimal() : complete();}
    @Override public Segment toSegment() {return new ValueSegment(toSequence());}
    
    /**@see ValueType#convert(NBTValue,ValueType)*/
    public NBTValue convertTo(final ValueType type)
                              throws NBTConversionException {
        return ValueType.convert(this,type);
    }
    
    /**
     * Uses the iterator to parse the next value. The iterator's position following
     * this call will be at the last character of the parsed value.
     * 
     * @param i           A {@linkplain SequenceIterator} which points to the
     *                    position just before the value's sequence.
     * @param terminators A character which marks the end of a structure.
     *                    <code>null</code> indicates the end of the sequence.
     * @param commas      <code>true</code> iff commas are allowed to terminate a
     *                    value.
     * 
     * @return The appropriate {@linkplain NBTValue}.
     * 
     * @throws NBTParsingException The iterator cannot find a valid value.
     */
    public static NBTValue parse(final SequenceIterator i,
                                 final Character terminator,
                                 final boolean commas)
                                 throws NBTParsingException {
        if(i.skipWS() == null)
            throw new NBTParsingException("Cannot parse an empty value.");
        final int start = i.index();
        NBTValue v = null;
        try {v = parseNotString(i,terminator,commas);}
        catch(final NBTParsingException e) {}
        if(v == null) v = NBTString.parse(i.jumpTo(start),terminator,commas);
        final Character c = i.skipWS();
        if(!(commas && c == ',') && c != terminator)
            throw new NBTParsingException("value",i,terminator,commas,c);
        return v;
    }
    /**
     * Attempts to parse a value which is not a string.
     * 
     * @param i          A {@linkplain SequenceIterator} which points to the
     *                   position just before the value's sequence.
     * @param terminator A character which marks the end of a structure.
     *                   <code>null</code> indicates the end of the sequence.
     * @param commas     <code>true</code> iff commas are allowed to terminate a
     *                   value.
     * @param strict     <code>true</code> iff the function should throw an error
     *                   when the terminator/comma requirement is not met.
     *                   
     * @return The appropriate {@linkplain NBTValue}.
     *                   
     * @throws NBTParsingException The iterator cannot find a valid non-string value.
     */
    public static NBTValue parseNotString(final SequenceIterator i,
                                          final Character terminator,
                                          final boolean commas)
                                          throws NBTParsingException {
        return switch(i.peek()) {
            case 't','f','T','F' -> NBTBool.parse(i,terminator,commas);
            case '{' -> NBTObject.parse(i,terminator,commas);
            case '[' -> NBTArray.parse(i,terminator,commas);
            case '+','-','.','0','1','2','3',
                 '4','5','6','7','8','9' -> NBTNumber.parse(i,terminator,commas);
            default -> null;
        };
    }
}