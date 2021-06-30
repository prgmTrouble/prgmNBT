package util.string;

/**An enumeration of characters which break up a floating-point string.*/
public enum FPDelimiter {
    fraction('.'),
    exponent('e','E');
    
    private final char[] delimiters;
    private FPDelimiter(final char...c) {delimiters = c;}
    
    /**@return <code>true</code> iff the character is an instance of this delimiter.*/
    public boolean matches(final char c) {
        for(final char d : delimiters) if(c == d) return true;
        return false;
    }
    /**@return The default character representing this delimiter.*/
    public char get() {return delimiters[0];}
}