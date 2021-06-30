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
import util.string.Sequence.SequenceIterator;

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
    
    public static final Sequence TRUE_SEQUENCE,FALSE_SEQUENCE,
                                 TRUE_BYTE,FALSE_BYTE,
                                 TRUE_BYTE_MIN,FALSE_BYTE_MIN;
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
        TRUE_BYTE = share[2]; TRUE_BYTE_MIN = TRUE_BYTE.subSequence(0,1);
        FALSE_BYTE = share[3]; FALSE_BYTE_MIN = FALSE_BYTE.subSequence(0,1);
    }
    
    /**
     * Creates a boolean value of {@linkplain #GLOBAL_DEFAULT} (<code>false</code>).
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTBool() {super();}
    /**
     * Creates a boolean value.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTBool(final boolean value) {super(value? 1 : 0);}
    /**
     * Creates a boolean value.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTBool(final byte value) {super(value != 0? 1 : 0);}
    /**
     * Reads a boolean value.
     * 
     * @throws IOException The input cannot be read.
     */
    public NBTBool(final DataInput in) throws IOException {this(in.readBoolean());}
    
    /**@return The value of this boolean value.*/
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
    @Override protected Sequence minimal() {return getValue()? TRUE_BYTE_MIN : FALSE_BYTE_MIN;}
    
    @Override public Sequence toSequence() {return minimal? minimal() : complete();}
    
    @Override
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
    
    /**
     * @param i          A {@linkplain SequenceIterator} which points to the
     *                   position just before the boolean sequence.
     * @param terminator A character which marks the end of a structure.
     *                   <code>null</code> indicates the end of the sequence.
     * @param commas     <code>true</code> iff commas are allowed to terminate a
     *                   value.
     * 
     * @return The appropriate {@linkplain NBTBool}.
     * 
     * @throws NBTParsingException The iterator cannot find a valid boolean.
     * 
     * @implNote This function does not consider numeric values. These should be
     *           handled by {@linkplain NBTByte}.
     */
    public static NBTBool parse(final SequenceIterator i,
                                final Character terminator,
                                final boolean commas)
                                throws NBTParsingException {
        final boolean parity = switch(i.peek()) {
            case 't','T' -> true;
            case 'f','F' -> false;
            default -> throw new NBTParsingException(
                "Invalid boolean character '%c' ('\\u%04X')"
                .formatted(i.peek(),(int)i.peek()),i
            );
        };
        final SequenceIterator match = (parity? TRUE_SEQUENCE : FALSE_SEQUENCE).iterator();
        while(match.hasNext())
            if(match.next().charValue() != Character.toLowerCase(i.next()))
                throw new NBTParsingException(
                    "Invalid boolean character '%c' ('\\u%04X')"
                    .formatted(i.peek(),(int)i.peek()),i
                );
        final Character c = i.nextNonWS();
        if(!(commas && c == ',') && c != terminator)
            throw new NBTParsingException("boolean",i,terminator,commas,c);
        return new NBTBool(parity);
    }
}