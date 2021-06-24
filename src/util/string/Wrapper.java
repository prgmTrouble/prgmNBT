package util.string;

/**
 * A {@linkplain Builder} which wraps an input string between values.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public class Wrapper implements Builder {
    /**Current sequence stored in the instance.*/
    private Sequence data;
    /**Storage for the wrapper characters.*/
    private final Sequence[] wrapper;
    
    /**@param wrapper A character which appears at both ends of the output string.*/
    public Wrapper(final char wrapper) {this(wrapper,wrapper);}
    /**
     * @param nfix     A character which appears at one end of the output string.
     * @param isPrefix <code>true</code> if the character appears at the start of
     *                 the output string, <code>false</code> if at the end.
     */
    public Wrapper(final char nfix,final boolean isPrefix) {
        wrapper = Sequence.shared(new char[] {nfix},isPrefix? 1 : 0);
    }
    /**
     * @param prefix A character which appears before the output string.
     * @param suffix A character which appears after the output string.
     */
    public Wrapper(final char prefix,final char suffix) {wrapper = Sequence.shared(new char[] {prefix,suffix},1);}
    /**
     * @param prefix A character sequence which appears before the output string.
     * @param suffix A character sequence which appears after the output string.
     */
    public Wrapper(final char[] prefix,final char[] suffix) {wrapper = Sequence.shared(prefix,suffix);}
    /**
     * @param prefix A character sequence which appears before the output string.
     * @param suffix A character sequence which appears after the output string.
     */
    public Wrapper(final Sequence prefix,final Sequence suffix) {this(prefix.toChars(),suffix.toChars());}
    
    @Override
    public Wrapper push(final Sequence s) {
        data = s;
        return this;
    }
    
    @Override
    public Sequence concat() {
        if(data == null || data.length() == 0) return wrapper[0].getSharedSequence();
        return new Sequence(Sequence.merge(wrapper[0],data,wrapper[1]));
    }
    
    /**
     * @param data    Data to wrap.
     * @param wrapper The character which appears at both ends of the output string.
     * 
     * @return Thre wrapped data.
     */
    public static Sequence wrap(final Sequence data,final char wrapper) {
        if(data == null || data.length() == 0) return new Sequence(wrapper,wrapper);
        final char[] out = new char[data.length() + 2];
        data.copyInto(out,1);
        out[0] = out[out.length - 1] = wrapper;
        return new Sequence(out);
    }
}