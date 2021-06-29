package nbt.value.number;

import nbt.exception.NBTParsingException;
import nbt.value.NBTValue;
import util.string.Sequence.SequenceIterator;

/**
 * A floating-point {@linkplain NBTNumber}.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public abstract class NBTFP extends NBTNumber {
    /**An enumeration of characters which break up a floating-point string.*/
    public static enum Delimiter {
        fraction('.'),
        exponent('e','E');
        
        private final char[] delimiters;
        private Delimiter(final char...c) {delimiters = c;}
        
        /**@return <code>true</code> iff the character is an instance of this delimiter.*/
        public boolean matches(final char c) {
            for(final char d : delimiters) if(c == d) return true;
            return false;
        }
        /**@return The default character representing this delimiter.*/
        public char get() {return delimiters[0];}
    }
    
    /**
     * Creates a floating-point value with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    protected NBTFP(final Number value) {super(value);}
    /**
     * Creates a floating-point value.
     * 
     * @see NBTValue#NBTValue()
     */
    protected NBTFP(final Number value,final boolean minimal) {super(value,minimal);}
    
    /**@return <code>true</code> iff the argument matches a float or double suffix.*/
    public static boolean isFPSuffix(final char suffix) {
        return NBTFloat.isFloatSuffix(suffix) || NBTDouble.isDoubleSuffix(suffix);
    }
    
    /**
     * @param i      A {@linkplain SequenceIterator} which points to the position
     *               just before the numeric sequence.
     * @param suffix A character which hints at a suffix.
     *               
     * @return The appropriate floating-point value.
     *               
     * @throws NBTParsingException The iterator could not find a valid floating
     *                             point number.
     */
    protected static NBTFP parse(final SequenceIterator i,final char suffix) throws NBTParsingException {
        if(NBTFloat.isFloatSuffix(suffix)) {
            final float parse;
            try {parse = Float.parseFloat(i.subSequence().toString());}
            catch(final NumberFormatException e) {
                throw new NBTParsingException("Invalid 32-bit float",i,e);
            }
            return new NBTFloat(parse);
        }
        final double parse;
        try {parse = Double.parseDouble(i.subSequence().toString());}
        catch(final NumberFormatException e) {
            throw new NBTParsingException("Invalid 64-bit float",i,e);
        }
        return new NBTDouble(parse);
    }
}