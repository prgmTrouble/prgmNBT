package nbt.value.collection;

import nbt.exception.NBTConversionException;
import nbt.exception.NBTException;
import nbt.exception.NBTParsingException;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import nbt.value.number.NBTByte;
import nbt.value.number.NBTInt;
import nbt.value.number.NBTLong;
import settings.Version;
import util.string.Sequence.SequenceIterator;

/**
 * An {@linkplain NBTArray} which holds primitive values.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public abstract class NBTPrimitiveArray extends NBTArray {
    /**Enables primitive arrays.*/
    public static final boolean PRIMITIVE_ARRAY_ENABLED = Version.atLeast(Version.v17w16a);
    {
        // Throw a hissy-fit on initialization.
        if(!PRIMITIVE_ARRAY_ENABLED)
            throw new NBTException(
                "Primitive arrays are not enabled until %s.".formatted(Version.v17w16a.name)
            );
    }
    
    /**Allows the array token to have surrounding whitespace.*/
    public static final boolean ALLOW_TOKEN_WHITESPACE = Version.atLeast(Version.unknown);
    
    public static final char TOKEN_SEPARATOR = ';';
    
    /**
     * Creates an empty primitive array with default minimalism.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    protected NBTPrimitiveArray(final ValueType subtype) throws NBTException {
        super();
        this.subtype = subtype;
    }
    /**
     * Creates an empty primitive array.
     * 
     * @see {@linkplain NBTValue#NBTValue(boolean)}
     */
    protected NBTPrimitiveArray(final ValueType subtype,final boolean minimal) throws NBTException {
        super(minimal);
        this.subtype = subtype;
    }
    
    @Override protected NBTValue adopt(final NBTValue element) throws NBTConversionException {return element.convertTo(subtype);}
    @Override
    public NBTValue remove(final Integer key) {
        if(key == null || values.size() <= key || key < 0)
            throw new IllegalArgumentException(String.format(
                "Cannot remove value at position %s (size = %d).",
                key,values.size()
            ));
        return values.remove(key.intValue());
    }
    
    /**
     * @param i A {@linkplain SequenceIterator} which points to the position at the
     *          opening bracket.
     * 
     * @return An {@linkplain NBTPrimitiveArray}, or an {@linkplain NBTArray} if no
     *         matching tokens are found.
     *         
     * @throws NBTParsingException If the iterator cannot find a valid array.
     */
    protected static NBTArray parseHeader(final SequenceIterator i) throws NBTParsingException {
        final Character c = ALLOW_TOKEN_WHITESPACE? i.nextNonWS()
                                                  : i.next();
        if(c == null) throw new NBTParsingException("Missing closing character '%c'".formatted(CLOSE),i);
        switch(c) {
            case NBTLong.ARRAY_TOKEN,
                 NBTByte.ARRAY_TOKEN,
                 NBTInt .ARRAY_TOKEN:
                if(!PRIMITIVE_ARRAY_ENABLED) break;
                final Character sep = ALLOW_TOKEN_WHITESPACE? i.nextNonWS()
                                                            : i.next();
                if(sep == null) throw new NBTParsingException("Missing closing character '%c'".formatted(CLOSE),i);
                if(sep != TOKEN_SEPARATOR) break;
                try {
                    return switch(c) {
                        case NBTByte.ARRAY_TOKEN -> new NBTByteArray();
                        case NBTInt.ARRAY_TOKEN -> new NBTIntArray();
                        case NBTLong.ARRAY_TOKEN -> new NBTLongArray();
                        default -> new NBTArray();
                    };
                } catch(final NBTException e) {
                    throw new NBTParsingException(
                        "Error while instantiating primitive array.",e
                    );
                }
        }
        return new NBTArray();
    }
}























