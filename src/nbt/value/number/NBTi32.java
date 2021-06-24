package nbt.value.number;

import nbt.exception.NBTParsingException;
import nbt.value.NBTValue;
import util.string.Sequence.SequenceIterator;

/**
 * An integral {@linkplain NBTNumber} which can be represented in 32 bits (i.e.
 * <code>byte</code>s, <code>short</code>s, and <code>int</code>s).
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public abstract class NBTi32 extends NBTNumber {
    public static final boolean SUFFIX_POLICY = false;
    public static final int GLOBAL_DEFAULT = 0;
    
    /**Used in {@linkplain #isDefault()}.*/
    protected int localDefault = GLOBAL_DEFAULT;
    
    /**
     * Creates an integer value of {@linkplain #GLOBAL_DEFAULT} with default minimalism.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    protected NBTi32() {super(GLOBAL_DEFAULT);}
    /**
     * Creates a integer value of {@linkplain #GLOBAL_DEFAULT}.
     * 
     * @see {@linkplain NBTValue#NBTValue(boolean)}
     */
    protected NBTi32(final boolean minimal) {super(GLOBAL_DEFAULT,minimal);}
    /**
     * Creates a integer value default minimalism.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    protected NBTi32(final int value) {super(value);}
    /**
     * Creates a integer value.
     * 
     * @see {@linkplain NBTValue#NBTValue(boolean)}
     */
    protected NBTi32(final int value,final boolean minimal) {super(value,minimal);}
    
    public NBTi32 setValue(final int value) {this.value = value; return this;}
    public NBTi32 setDefault(final int localDefault) {this.localDefault = localDefault; return this;}
    
    @Override public boolean isDefault() {return value.intValue() == localDefault;}
    
    @Override protected boolean forceSuffix() {return SUFFIX_POLICY;}
    
    public static boolean isIntSuffix(final char c) {return NBTByte.isByteSuffix(c) || NBTShort.isShortSuffix(c);}
    
    /**
     * @param i      A {@linkplain SequenceIterator} which points to the position
     *               just before the numeric sequence.
     * @param suffix A character which hints at a suffix.
     *               
     * @return The appropriate integral value.
     *               
     * @throws NBTParsingException If the iterator cannot find a valid integral number.
     */
    protected static NBTi32 parse(final SequenceIterator i,final char suffix) throws NBTParsingException {
        if(i.hasNext()) {
            final boolean negative;
            final int limit;
            int result = 0;
            {
                final char firstChar = i.next();
                limit = (negative = firstChar == '-')? Integer.MIN_VALUE : -Integer.MAX_VALUE;
                if((negative || firstChar == '+') && !i.hasNext())
                    throw new NBTParsingException("Lone sign character in integral parsing",i);
                else if(!NBTNumber.isNumeric(firstChar))
                    throw new NBTParsingException("Invalid character in integral parsing",i);
                else result = '0' - firstChar;
            }
            final int multmin = limit / 10;
            while(i.hasNext()) {
                final int digit = i.next() - '0';
                if(9 < digit || digit < 0 || result < multmin)
                    throw new NBTParsingException(
                        "Invalid character '%c' ('\\u%04X') in integral parsing"
                        .formatted(i.peek(),(int)i.peek()),i
                    );
                result *= 10;
                if(result < limit + digit)
                    throw new NBTParsingException("Numeric sequence too large",i);
                // Subtracting avoids edge cases near overflow values.
                result -= digit;
            }
            if(!negative) result = -result;
            return !NBTByte.isByteSuffix(suffix)
                       ? !NBTShort.isShortSuffix(suffix)
                           ? new NBTInt(result)
                           : new NBTShort(result)
                       : new NBTByte(result);
        }
        throw new NBTParsingException("Empty sequence in integral parsing",i);
    }
}