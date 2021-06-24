package nbt.value.collection;

import static nbt.value.collection.NBTArray.CLOSE;
import static nbt.value.collection.NBTArray.OPEN;
import static nbt.value.collection.NBTPrimitiveArray.TOKEN_SEPARATOR;

import nbt.value.ValueType;
import util.string.Joiner;
import util.string.Sequence;

public enum PrimitiveArrayToken {
    BYTE('B',ValueType.BYTE),
    INT ('I',ValueType.INT),
    LONG('L',ValueType.LONG);
    
    public final char arrayPrefix;
    public final ValueType subtype;
    public final Sequence empty;
    private final Sequence[] wrapper;
    
    private PrimitiveArrayToken(final char prefix,final ValueType subtype) {
        final char[] data = {OPEN,arrayPrefix = prefix,TOKEN_SEPARATOR,CLOSE};
        wrapper = Sequence.shared(data,3);
        empty = new Sequence(data);
        this.subtype = subtype;
    }
    
    public Joiner getJoiner() {return new Joiner(wrapper);}
    public static PrimitiveArrayToken fromChar(final char c) {
        return switch(c) {
            case 'B' -> BYTE;
            case 'I' -> INT;
            case 'L' -> LONG;
            default  -> null;
        };
    }
}