package nbt.value.number;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import nbt.exception.NBTParsingException;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import util.string.Sequence.SequenceIterator;

/**
 * An integral {@linkplain NBTNumber} which can be represented in 64 bits.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class NBTLong extends NBTNumber {
    public static final ValueType TYPE = ValueType.LONG;
    @Override public ValueType type() {return TYPE;}
    
    public static final char SUFFIX_0 = 'L',SUFFIX_1 = 'l';
    public static final char ARRAY_TOKEN = 'L';
    public static final boolean SUFFIX_POLICY = true;
    public static final long GLOBAL_DEFAULT = 0L;
    
    /**Used in {@linkplain #isDefault()}.*/
    public long localDefault = GLOBAL_DEFAULT;
    
    /**
     * Creates a long value of {@linkplain #GLOBAL_DEFAULT} with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTLong() {super(GLOBAL_DEFAULT);}
    /**
     * Creates a long value of {@linkplain #GLOBAL_DEFAULT}.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTLong(final boolean minimal) {super(GLOBAL_DEFAULT,minimal);}
    /**
     * Creates a long value default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTLong(final long value) {super(value);}
    /**
     * Creates a long value.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTLong(final long value,final boolean minimal) {super(value,minimal);}
    /**Reads a long value.*/
    public NBTLong(final DataInput in) throws IOException {super(in.readLong());}
    /**Writes this long value.*/
    @Override
    public void write(final DataOutput out) throws IOException {out.writeLong(value.longValue());}
    
    @Override public boolean isDefault() {return value.longValue() == localDefault;}
    
    @Override protected char suffix() {return SUFFIX_0;}
    @Override protected boolean forceSuffix() {return SUFFIX_POLICY;}
    
    /**@return <code>true</code> iff the provided character represents a 64 bit integral type.*/
    public static boolean isLongSuffix(final char suffix) {
        return suffix == SUFFIX_0 || suffix == SUFFIX_1;
    }
    
    /**
     * @param i A {@linkplain SequenceIterator} which points to the position just
     *          before the long sequence. There cannot be any trailing data
     *          following the number.
     *          
     * @throws NBTParsingException If the iterator cannot find a valid long value.
     */
    public static NBTLong parseNoSuffix(final SequenceIterator i) throws NBTParsingException {
        if(i.hasNext()) {
            final boolean negative;
            final long limit;
            {
                final char firstChar = i.next();
                limit = (negative = firstChar == '-')? Long.MIN_VALUE : -Long.MAX_VALUE;
                if((negative || firstChar == '+') && !i.hasNext())
                    throw new NBTParsingException("Lone sign character in integral parsing",i);
                else if(!NBTNumber.isNumeric(firstChar))
                    throw new NBTParsingException("Invalid character in integral parsing",i);
            }
            final long multmin = limit / 10L;
            long result = 0L;
            while(i.hasNext()) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                final int digit = i.next() - '0';
                if(9 < digit || digit < 0 || result < multmin)
                    throw new NBTParsingException(
                        "Invalid character '%c' ('\\u%04X') in integral parsing"
                        .formatted(i.peek(),(int)i.peek()),i
                    );
                result *= 10L;
                if(result < limit + digit)
                    throw new NBTParsingException("Numeric sequence too large",i);
                result -= digit;
            }
            return new NBTLong(negative? result:-result);
        }
        throw new NBTParsingException("Empty sequence in integral parsing",i);
    }
}