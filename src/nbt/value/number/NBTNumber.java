package nbt.value.number;

import nbt.exception.NBTConversionException;
import nbt.exception.NBTParsingException;
import nbt.value.NBTBool;
import nbt.value.NBTString;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import nbt.value.number.NBTFP.Delimiter;
import settings.Version;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;

public abstract class NBTNumber extends NBTValue {
    // Access is protected to avoid null values.
    protected Number value;
    
    /**
     * Causes the parser to not recognise the exponent part in floating point values.
     */
    public static final boolean THE_WILD_WEST = Version.isBefore(Version.v17w16a);
    
    /**
     * Creates a numeric value with default minimalism.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    protected NBTNumber(final Number value) {super(); this.value = value;}
    /**
     * Creates a numeric value.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    protected NBTNumber(final Number value,final boolean minimal) {super(minimal); this.value = value;}
    
    /**@return This number's type suffix. Can be zero to specify that none exist.*/
    protected abstract char suffix();
    /**@return <code>true</code> if the number must be followed by a type suffix.*/
    protected abstract boolean forceSuffix();
    
    @Override protected Sequence minimal() {return new Sequence(value.toString());}
    
    @Override
    protected Sequence complete() {
        // Already checked empty suffix in overridden toSequence() function.
        final Sequence data = minimal();
        final int l = data.length();
        final char[] out = new char[l + 1];
        data.copyInto(out,0);
        out[l] = suffix();
        return new Sequence(out);
    }
    
    @Override public Sequence toSequence() {return suffix() == 0 || !forceSuffix() && minimal? minimal() : complete();}
    
    /**
     * Converts a numeric value to another type.
     * 
     * @param type The type to convert to. Can be any numeric or string type.
     * 
     * @return The converted value, or <code>null</code> if the value could not be
     *         converted.
     */
    public NBTValue convertTo(final ValueType type) throws NBTConversionException {
        return switch(type) {
            case BYTE,SHORT,INT -> { // All of these are i32, so they are all reducible to ints.
                final int v = value.intValue();
                yield switch(type) {
                    case BYTE -> new NBTByte(v,minimal);
                    case SHORT -> new NBTShort(v,minimal);
                    case INT -> new NBTInt(v,minimal);
                    default -> null;
                };
            }
            case LONG -> new NBTLong(value.longValue(),minimal);
            case FLOAT -> new NBTFloat(value.floatValue(),minimal);
            case DOUBLE -> new NBTDouble(value.doubleValue(),minimal);
            case BOOL -> new NBTBool(value.intValue() == 0? false : true);
            case STRING -> {
                try {yield new NBTString(toSequence(),minimal);}
                catch(final NBTParsingException e) {throw new NBTConversionException("numeric",type);}
            }
            default -> null;
        };
    }
    
    /**
     * @return <code>true</code> iff the input is a unary minus (<code>'-'</code>)
     *         or plus (<code>'+'</code>).
     */
    public static boolean isSign(final char c) {return c == '-' || c == '+';}
    /**@return <code>true</code> iff the input represents a decimal digit.*/
    public static boolean isNumeric(final char c) {return '0' <= c && c <= '9';}
    
    private static char eatDigits(final SequenceIterator i) {
        while(i.hasNext() && isNumeric(i.next()));
        return i.peek();
    }
    /**
     * @param i          A {@linkplain SequenceIterator} which points to the
     *                   position just before the numeric sequence.
     * @param terminator A character which marks the end of a structure.
     *                   <code>null</code> indicates the end of the sequence.
     * @param commas     <code>true</code> iff commas are allowed to terminate a
     *                   value.
     * 
     * @return The appropriate {@linkplain NBTNumber}.
     * 
     * @throws NBTParsingException If the iterator cannot find a valid number.
     */
    public static NBTNumber parse(final SequenceIterator i,
                                  final Character terminator,
                                  final boolean commas)
                                  throws NBTParsingException {
        i.skipWS();
        i.mark();
        final boolean noIP;
        boolean isFP = false; // A flag to make sure that a double without a suffix is caught.
        char next; // A character which takes note of a potential suffix.
        // Check integral part.
        {
            final int idx = i.index();
            final boolean hasSign = isSign(i.peek());
            next = eatDigits(i);
            // Check for lone sign.
            if((noIP = idx + 1 == i.index()) && hasSign) {
                // Lone sign is exempted if it could be part of a floating point number.
                if(next != '.')
                    throw new NBTParsingException("Invalid sign character",i);
                isFP = true;
            }
        }
        // Check for long.
        if(NBTLong.isLongSuffix(next)) {
            final NBTLong v = NBTLong.parseNoSuffix(i.subSequence().iterator());
            i.next();
            return v;
        }
        
        // Check fractional part.
        final boolean hasFP;
        if(hasFP = Delimiter.fraction.matches(next)) {
            final int idx = i.index();
            next = eatDigits(i);
            
            // Check for decimal point with no adjacent digits.
            if(noIP && idx + 1 >= i.index())
                throw new NBTParsingException("No digits found adjacent to the decimal point",i);
            isFP = true;
        }
        
        // Check exponent part.
        if(Delimiter.exponent.matches(next)) {
            if(THE_WILD_WEST)
                throw new NBTParsingException("Disallowed exponent part",i);
            // Check for leading exponent delimiter.
            if(noIP && !hasFP)
                throw new NBTParsingException("Exponent delimiter is the first character",i);
            // Check for tailing exponent delimiter.
            if(!i.hasNext())
                throw new NBTParsingException("Exponent delimiter is the last character",i);
            
            final boolean expSign = isSign(i.next());
            final int idx = i.index();
            // Check for an invalid character.
            if(!(expSign || isNumeric(i.peek())))
                throw new NBTParsingException("Invalid character in the exponent",i);
            next = eatDigits(i);
            
            // Check for lone sign character.
            if(expSign && idx + 1 >= i.index())
                throw new NBTParsingException("Invalid sign character in the exponent",i);
            isFP = true;
        }
        
        final Character c = NBTFP.isFPSuffix(next) || NBTi32.isIntSuffix(next)
                ? i.nextNonWS()
                : i.skipWS();
        if(!(commas && c == ',') && c != terminator)
            throw new NBTParsingException("number",i,terminator,commas,c);
        
        // Send to float or integral child parsing routine.
        return isFP || NBTFP.isFPSuffix(next)
                   ? NBTFP.parse(i,next)
                   : NBTi32.parse(i.subSequence().iterator(),next);
    }
}