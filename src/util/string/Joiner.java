package util.string;

/**
 * A {@linkplain Builder} which separates strings using a separator.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public class Joiner extends SequenceQueue { //TODO test changes
    /**Holds the characters which wrap and separate the elements.*/
    private final Sequence[] wrapper;
    
    /**Creates a joiner using a comma (',') for a separator.*/
    public Joiner() {this(',');}
    /**
     * Creates a joiner.
     * 
     * @param separator A character separating the strings.
     */
    public Joiner(final char separator) {
        wrapper = new Sequence[] {Sequence.EMPTY,
                                  Sequence.EMPTY,
                                  new Sequence(separator)};
    }
    /**
     * Creates a joiner.
     * 
     * @param prefix    A character which appears before the output string.
     * @param suffix    A character which appears after the output string.
     * @param separator A character separating the strings.
     */
    public Joiner(final char prefix,final char suffix,final char separator) {
        wrapper = Sequence.shared(new char[] {prefix,suffix,separator},1,2);
    }
    /**
     * Creates a joiner.
     * 
     * @param prefix    A character sequence which appears before the output string.
     * @param suffix    A character sequence which appears after the output string.
     * @param separator A character sequence separating the strings.
     */
    public Joiner(final char[] prefix,final char[] suffix,final char[] separator) {
        wrapper = Sequence.shared(prefix,suffix,separator);
    }
    /**
     * Creates a joiner using a comma (',') for a separator.
     * 
     * @param prefix    A character sequence which appears before the output string.
     * @param suffix    A character sequence which appears after the output string.
     */
    public Joiner(final char[] prefix,final char[] suffix) {
        wrapper = Sequence.shared(prefix,suffix,new char[] {','});
    }
    /**
     * Creates a joiner using a comma (',') for a separator.
     * 
     * @param prefix    A character sequence which appears before the output string.
     * @param suffix    A character sequence which appears after the output string.
     */
    public Joiner(final char prefix,final char suffix) {
        wrapper = Sequence.shared(new char[] {prefix,suffix,','},1,2);
    }
    /**
     * Creates a joiner.
     * 
     * @param separator A character sequence separating the strings.
     */
    public Joiner(final Sequence separator) {
        wrapper = new Sequence[] {Sequence.EMPTY,Sequence.EMPTY,separator};
    }
    /**
     * @param wrapper The sequences to use, in the order prefix, suffix, separator.
     *                The prefix and suffix default to empty sequences, and the
     *                seprator defaults to a comma
     */
    public Joiner(final Sequence...wrapper) {
        final int wl = wrapper == null? 0 : wrapper.length;
        this.wrapper = new Sequence[] {wl < 1 || wrapper[0] == null? Sequence.EMPTY    : wrapper[0],
                                       wl < 2 || wrapper[1] == null? Sequence.EMPTY    : wrapper[1],
                                       wl < 3 || wrapper[2] == null? new Sequence(',') : wrapper[2]};
    }
    
    @Override
    public Sequence concat() {
        final char[] out = new char[wrapper[0].length() +
                                    wrapper[1].length() +
                                    wrapper[2].length() * Math.max(size() - 1,0) +
                                    currentSize()];
        int cursor = wrapper[0].copyInto(out,0);
        if(!empty()) {
            cursor = pop().copyInto(out,cursor);
            while(!empty())
                cursor = pop().copyInto(out,wrapper[2].copyInto(out,cursor));
        }
        wrapper[1].copyInto(out,cursor);
        return new Sequence(out);
    }
}