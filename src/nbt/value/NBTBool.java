package nbt.value;

import java.io.DataInput;
import java.io.IOException;
import nbt.exception.NBTConversionException;
import nbt.exception.NBTParsingException;
import nbt.value.number.NBTByte;
import nbt.value.number.NBTDouble;
import nbt.value.number.NBTFloat;
import nbt.value.number.NBTInt;
import nbt.value.number.NBTLong;
import nbt.value.number.NBTShort;
import settings.Version;
import util.string.Sequence;

/**
 * A {@linkplain NBTValue} holding a boolean.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public class NBTBool extends NBTByte {
    public static final ValueType TYPE = ValueType.BOOL;
    @Override public ValueType type() {return TYPE;}
    
    /**
     * Causes <code>toSequence()</code> to output "<code>true</code>" or
     * "<code>false</code>" instead of "<code>1b</code>" or "<code>0b</code>" when
     * not minimal.
     */
    public static final boolean STRING_OUTPUT = Version.atLeast(Version.unknown);
    
    public static final Sequence TRUE_SEQUENCE,FALSE_SEQUENCE;
    public static final Sequence TRUE_BYTE,FALSE_BYTE;
    static {
        final String True = Boolean.toString(true),False = Boolean.toString(false),
                     TrueB = Byte.toString((byte)1) + 'b',FalseB = Byte.toString((byte)0) + 'b';
        final Sequence[] share = Sequence.shared(
            (True + False + TrueB + FalseB).toCharArray(),
            True.length(),
            True.length() + False.length(),
            True.length() + False.length() + TrueB.length()
        );
        TRUE_SEQUENCE = share[0];
        FALSE_SEQUENCE = share[1];
        TRUE_BYTE = share[2];
        FALSE_BYTE = share[3];
    }
    
    /**
     * Creates a boolean value of {@linkplain #GLOBAL_DEFAULT} (<code>false</code>).
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    public NBTBool() {super();}
    /**
     * Creates a boolean value.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    public NBTBool(final boolean value) {super(value? 1 : 0);}
    /**
     * Creates a boolean value.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    public NBTBool(final byte value) {super(value != 0? 1 : 0);}
    /**Reads a boolean value.*/
    public NBTBool(final DataInput in) throws IOException {this(in.readBoolean());}
    
    public boolean getValue() {return value.intValue() != 0;}
    
    @Override
    public NBTBool setValue(final byte value) {
        this.value = value != 0? 1 : 0;
        return this;
    }
    @Override
    public NBTBool setValue(final int value) {
        this.value = value != 0? 1 : 0;
        return this;
    }
    public NBTBool setValue(final boolean value) {
        this.value = value? 1 : 0;
        return this;
    }
    
    @Override
    public NBTBool setDefault(final byte localDefault) {
        this.localDefault = localDefault != 0? 1 : 0;
        return this;
    }
    @Override
    public NBTBool setDefault(final int localDefault) {
        this.localDefault = localDefault != 0? 1 : 0;
        return this;
    }
    public NBTBool setDefault(final boolean localDefault) {
        this.localDefault = localDefault? 1 : 0;
        return this;
    }
    
    @Override
    protected Sequence complete() {
        return getValue()
            ? (STRING_OUTPUT? TRUE_SEQUENCE : TRUE_BYTE)
            : (STRING_OUTPUT? FALSE_SEQUENCE : FALSE_BYTE);
    }
    @Override protected Sequence minimal() {return getValue()? TRUE_SEQUENCE : FALSE_SEQUENCE;}
    
    @Override public Sequence toSequence() {return minimal? minimal() : complete();}
    
    public NBTValue convertTo(final ValueType type) throws NBTConversionException {
        try {
            return switch(type) {
                case BYTE,SHORT,INT,LONG,FLOAT,DOUBLE -> {
                    final int v = value.intValue();
                    yield switch(type) {
                        case BYTE   -> new NBTByte  (v,minimal);
                        case SHORT  -> new NBTShort (v,minimal);
                        case INT    -> new NBTInt   (v,minimal);
                        case LONG   -> new NBTLong  (v,minimal);
                        case FLOAT  -> new NBTFloat (v,minimal);
                        case DOUBLE -> new NBTDouble(v,minimal);
                        default -> throw new NBTConversionException(TYPE,type);
                    };
                }
                case BOOL -> this;
                case STRING -> new NBTString(toSequence(),minimal);
                default -> throw new NBTConversionException(TYPE,type);
            };
        } catch(final NBTConversionException e) {throw e;}
        catch(final NBTParsingException e) {throw new NBTConversionException(TYPE,type,e);}
    }
}