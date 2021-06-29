package nbt.value.collection;

import static nbt.value.collection.NBTArray.CLOSE;
import static nbt.value.collection.NBTArray.OPEN;
import static nbt.value.collection.NBTPrimitiveArray.TOKEN_SEPARATOR;

import nbt.value.ValueType;
import util.string.Joiner;
import util.string.Sequence;

/**
 * An enumeration of tokens for primitive array types.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public enum PrimitiveArrayToken {
    /**@see NBTByteArray*/
    BYTE('B',ValueType.BYTE),
    /**@see NBTIntArray*/
    INT ('I',ValueType.INT),
    /**@see NBTLongArray*/
    LONG('L',ValueType.LONG);
    
    /**The character which appears directly after the opening character.*/
    public final char arrayPrefix;
    /**The {@linkplain ValueType} of the elements.*/
    public final ValueType subtype;
    /**A shared sequence of an empty primitive array.*/
    public final Sequence empty;
    private final Sequence[] wrapper;
    
    private PrimitiveArrayToken(final char prefix,final ValueType subtype) {
        final char[] data = {OPEN,arrayPrefix = prefix,TOKEN_SEPARATOR,CLOSE};
        wrapper = Sequence.shared(data,3);
        empty = new Sequence(data);
        this.subtype = subtype;
    }
    
    public Joiner getJoiner() {return new Joiner(wrapper);}
    /**@return The token associated with this character, or <code>null</code> iff no tokens match.*/
    public static PrimitiveArrayToken fromChar(final char c) {
        return switch(c) {
            case 'B' -> BYTE;
            case 'I' -> INT;
            case 'L' -> LONG;
            default  -> null;
        };
    }
}