package nbt.value;

import java.util.Set;

import java.io.DataInput;
import java.io.IOException;
import nbt.exception.NBTConversionException;
import nbt.exception.NBTException;
import nbt.exception.NBTParsingException;
import nbt.value.collection.NBTArray;
import nbt.value.collection.NBTByteArray;
import nbt.value.collection.NBTIntArray;
import nbt.value.collection.NBTLongArray;
import nbt.value.collection.NBTObject;
import nbt.value.number.NBTByte;
import nbt.value.number.NBTDouble;
import nbt.value.number.NBTFloat;
import nbt.value.number.NBTInt;
import nbt.value.number.NBTLong;
import nbt.value.number.NBTNumber;
import nbt.value.number.NBTShort;
import settings.Version;

public enum ValueType {
    BYTE      ( 1,in -> new NBTByte     (in),"Byte"),
    SHORT     ( 2,in -> new NBTShort    (in),"Short"),
    INT       ( 3,in -> new NBTInt      (in),"Int"),
    LONG      ( 4,in -> new NBTLong     (in),"Long"),
    FLOAT     ( 5,in -> new NBTFloat    (in),"Float"),
    DOUBLE    ( 6,in -> new NBTDouble   (in),"Double"),
    BOOL      ( 1,in -> new NBTBool     (in),"Bool"),
    STRING    ( 8,in -> new NBTString   (in),"String"),
    OBJECT    (10,in -> new NBTObject   (in),"Compound"),
    ARRAY     ( 9,in -> new NBTArray    (in),"List"),
    BYTE_ARRAY( 7,in -> new NBTByteArray(in),"Byte Array"),
    INT_ARRAY (11,in -> new NBTIntArray (in),"Int Array"),
    LONG_ARRAY(12,in -> new NBTLongArray(in),"Long Array");
    
    private static interface Reader {NBTValue read(final DataInput in) throws IOException,NBTException;}
    
    private final Reader reader;
    public final String name;
    public final int id;
    private ValueType(final int id,final Reader reader,final String name) {
        this.id = id;
        this.reader = reader;
        this.name = name;
    }
    /**Reads a value of this type.*/
    public NBTValue read(final DataInput in) throws NBTException {
        try {return reader.read(in);}
        catch(final IOException e) {
            throw new NBTException(
                "Could not read %s.".formatted(name),e
            );
        }
    }
    
    /**A set containing the {@linkplain ValueType}s of NBTs inheriting from {@linkplain NBTNumber}.*/
    public static final Set<ValueType> NUMERIC = Set.of(BYTE,SHORT,INT,LONG,FLOAT,DOUBLE);
    
    /**
     * @param value  Value to convert.
     * @param target Type of conversion result.
     * 
     * @return The converted value.
     */
    public static NBTValue convert(final NBTValue value,
                                   final ValueType target)
                                   throws NBTConversionException {
        // Null checks.
        if(value == null || target == null)
            throw new NBTConversionException("Cannot convert between null types.");
        final ValueType type = value.type();
        // If already matches, do nothing.
        if(type == target) return value;
        // Anything can be converted to a string.
        if(target == STRING) {
            try {return new NBTString(value.toSequence(),value.minimal);}
            catch(final NBTParsingException e) {throw new NBTConversionException(type,target,e);}
        }
        // Booleans and numeric values can be converted interchangeably.
        if(type == BOOL) return ((NBTBool)value).convertTo(target);
        if(NUMERIC.contains(type)) return ((NBTNumber)value).convertTo(target);
        // No valid conversion found.
        throw new NBTConversionException(type,target);
    }
    
    /**Converts a byte id into a type.*/
    public static ValueType getType(final byte i) {
        return switch(i) {
            case  1 -> BYTE;
            case  2 -> SHORT;
            case  3 -> INT;
            case  4 -> LONG;
            case  5 -> FLOAT;
            case  6 -> DOUBLE;
            case  7 -> BYTE_ARRAY;
            case  8 -> STRING;
            case  9 -> ARRAY;
            case 10 -> OBJECT;
            case 11 -> INT_ARRAY;
            case 12 -> Version.atLeast(Version.v17w16a)? LONG_ARRAY : null;
            default -> null;
        };
    }
    /**Reads a complete value.*/
    public static NBTValue infer(final DataInput in) throws NBTException {
        final ValueType v;
        {
            final byte b;
            try {b = in.readByte();}
            catch(final IOException e) {
                throw new NBTException(
                    "Could not read value type.",e
                );
            }
            if((v = getType(b)) == null)
                throw new NBTException(
                    "Unknown type %d.".formatted(b)
                );
        }
        return v.read(in);
    }
}