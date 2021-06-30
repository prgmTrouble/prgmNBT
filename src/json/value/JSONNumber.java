package json.value;

import json.exception.JSONConversionException;
import json.exception.JSONParsingException;
import util.string.FPDelimiter;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;

/**
 * A {@linkplain JSONValue} which holds a numeric value.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class JSONNumber extends JSONValue {
    public static final ValueType TYPE = ValueType.NUMBER;
    @Override public ValueType type() {return TYPE;}
    @Override
    public JSONValue convertTo(final ValueType target) throws JSONConversionException {
        return target == ValueType.BOOL? new JSONBool(value().doubleValue() != 0d)
                                       : super.convertTo(target);
    }
    
    private Number value = 0;
    
    /**Creates a number with the value zero.*/
    public JSONNumber() {super();}
    /**
     * Creates a number with the specified value. <code>null</code> values yield
     * zero.
     */
    public JSONNumber(final Number value) {super(); value(value);}
    
    /**@return This number's value.*/
    public Number value() {return value;}
    /**
     * @param value This number's value. <code>null</code> values yield zero.
     * 
     * @return <code>this</code>
     */
    public JSONNumber value(final Number value) {
        this.value = value == null? 0 : value;
        return this;
    }
    
    @Override public Sequence toSequence() {return new Sequence(value().toString());}
    
    /**
     * @return <code>true</code> iff the input is a unary minus (<code>'-'</code>)
     *         or plus (<code>'+'</code>).
     */
    private static boolean isSign(final char c) {return c == '-' || c == '+';}
    /**@return <code>true</code> iff the input represents a decimal digit.*/
    private static boolean isNumeric(final char c) {return '0' <= c && c <= '9';}
    
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
     * @return The appropriate {@linkplain JSONNumber}.
     * 
     * @throws JSONParsingException The iterator cannot find a valid number.
     */
    public static JSONNumber parse(final SequenceIterator i,
                                   final Character terminator,
                                   final boolean commas)
                                   throws JSONParsingException {
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
                    throw new JSONParsingException("Invalid sign character",i);
                isFP = true;
            }
        }
        
        // Check fractional part.
        final boolean hasFP;
        if(hasFP = FPDelimiter.fraction.matches(next)) {
            final int idx = i.index();
            next = eatDigits(i);
            
            // Check for decimal point with no adjacent digits.
            if(noIP && idx + 1 >= i.index())
                throw new JSONParsingException("No digits found adjacent to the decimal point",i);
            isFP = true;
        }
        
        // Check exponent part.
        if(FPDelimiter.exponent.matches(next)) {
            // Check for leading exponent delimiter.
            if(noIP && !hasFP)
                throw new JSONParsingException("Exponent delimiter is the first character",i);
            // Check for tailing exponent delimiter.
            if(!i.hasNext())
                throw new JSONParsingException("Exponent delimiter is the last character",i);
            
            final boolean expSign = isSign(i.next());
            final int idx = i.index();
            // Check for an invalid character.
            if(!(expSign || isNumeric(i.peek())))
                throw new JSONParsingException("Invalid character in the exponent",i);
            next = eatDigits(i);
            
            // Check for lone sign character.
            if(expSign && idx + 1 >= i.index())
                throw new JSONParsingException("Invalid sign character in the exponent",i);
            isFP = true;
        }
        
        final Character c = i.skipWS();
        if(!(commas && c == ',') && c != terminator)
            throw new JSONParsingException("number",i,terminator,commas,c);
        
        final Sequence num = i.subSequence();
        if(isFP) {
            try {return new JSONNumber(Double.parseDouble(num.toString()));}
            catch(final NumberFormatException e) {
                throw new JSONParsingException("Invalid 64-bit float",i,e);
            }
        }
        return parseIntegral(num.iterator());
    }
    private static JSONNumber parseIntegral(final SequenceIterator i) throws JSONParsingException {
        if(i.hasNext()) {
            final boolean negative;
            final long limit;
            long result = 0L;
            {
                final char firstChar = i.next();
                limit = (negative = firstChar == '-')? Long.MIN_VALUE : -Long.MAX_VALUE;
                if((negative || firstChar == '+') && !i.hasNext())
                    throw new JSONParsingException("Lone sign character in integral parsing",i);
                else if(!isNumeric(firstChar))
                    throw new JSONParsingException("Invalid character in integral parsing",i);
                else result = '0' - firstChar;
            }
            final long multmin = limit / 10L;
            while(i.hasNext()) {
                final int digit = i.next() - '0';
                if(9 < digit || digit < 0 || result < multmin)
                    throw new JSONParsingException(
                        "Invalid character '%c' ('\\u%04X') in integral parsing"
                        .formatted(i.peek(),(int)i.peek()),i
                    );
                result *= 10L;
                if(result < limit + digit)
                    throw new JSONParsingException("Numeric sequence too large",i);
                // Subtracting avoids edge cases near overflow values.
                result -= digit;
            }
            if(!negative) result = -result;
            return new JSONNumber(
                Integer.MAX_VALUE < result || result < Integer.MIN_VALUE
                ? result
                : (int)result
            );
        }
        throw new JSONParsingException("Empty sequence in integral parsing",i);
    }
}