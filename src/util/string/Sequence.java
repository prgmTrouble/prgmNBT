package util.string;

import java.util.Iterator;

import nbt.value.NBTString;
import nbt.value.collection.NBTArray;
import nbt.value.collection.NBTObject;
import nbt.value.collection.NBTPrimitiveArray;
import nbt.value.collection.NBTTag;
import util.ExceptionUtils;
import util.container.ArrayUtils;
import util.container.Queue;
import util.container.Stack;

/**
 * A lightweight immutable sequence of characters.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class Sequence implements CharSequence,Comparable<Sequence>,Iterable<Character> {
    /**A shared instance of an empty character array.*/
    private static final char[] EMPTY_DATA = {};
    /**A shared instance of an empty sequence.*/
    public static final Sequence EMPTY = new Sequence();
    
    private final char[] data;
    private final int start,end;
    
    /**Creates an empty sequence.*/
    public Sequence() {
        start = end = 0;
        data = EMPTY_DATA;
    }
    /**
     * Creates a sequence. A <code>null</code> data array will be set to an empty
     * sequence.
     * 
     * @param data Initial character data.
     */
    public Sequence(final char...data) {
        start = 0;
        if(data != null && data.length != 0) end = (this.data = data).length;
        else {end = 0; this.data = EMPTY_DATA;}
    }
    /**
     * Creates a seqence. Invalid indices are forced into the nearest valid bounds.
     * A <code>null</code> data array will be set to an empty sequence.
     * 
     * @param offset Starting index, inclusive.
     * @param data   Initial character data.
     */
    public Sequence(final int offset,final char...data) {
        if(data != null && data.length != 0)
            start = Math.min(
                Math.max(offset,0),
                end = (this.data = data).length
            );
        else {
            start = end = 0;
            this.data = EMPTY_DATA;
        }
    }
    /**
     * Creates a sequence. Invalid indices are forced into the nearest valid bounds.
     * A <code>null</code> data array will be set to an empty sequence.
     * 
     * @param start Starting index, inclusive.
     * @param end   Ending index, exclusive.
     * @param data  Initial character data.
     */
    public Sequence(final int start,final int end,final char...data) {
        if(data != null && data.length != 0) {
            this.end = Math.max(
                this.start = Math.max(
                    Math.min(
                        start,
                        (this.data = data).length
                    ),
                    0
                ),
                Math.min(
                    end,
                    data.length
                )
            );
        } else {
            this.start = this.end = 0;
            this.data = EMPTY_DATA;
        }
    }
    /**Copies a sequence.*/
    public Sequence(final Sequence s) {
        if(s == null) {
            start = end = 0;
            data = EMPTY_DATA;
        } else {
            start = s.start;
            end = s.end;
            data = s.data;
        }
    }
    /**Creates a sequence from a string.*/
    public Sequence(final String s) {
        this(s == null? EMPTY_DATA : s.toCharArray());
    }
    /**Repeats a sequence <code>count</code> times.*/
    public Sequence(final Sequence s,final int count) {
        if(s == null) {
            start = end = 0;
            data = EMPTY_DATA;
        } else if(count < 2) {
            start = s.start;
            end = s.end;
            data = s.data;
        } else {
            data = new char[count * length()];
            // Clone data from this sequence to the buffer.
            System.arraycopy(s.data,s.start,data,0,s.length());
            repeat(data,count,s.length());
            start = 0;
            end = data.length;
        }
    }
    /**Creates a sequence from a string repeated <code>count</code> times.*/
    public Sequence(final String s,final int count) {
        this(s == null? EMPTY_DATA : s.repeat(count).toCharArray());
    }
    /**Creates a sequence from a character repeated <code>count</code> times.*/
    public Sequence(final char c,final int count) {
        start = 0;
        if(count < 1) {end = 0; data = EMPTY_DATA;}
        else ArrayUtils.fill(data = new char[end = count],c);
    }
    
    private static char[] repeat(final char[] buf,final int count,int length) {
        // Double the cloned region until the next portion does
        // not fit into the buffer.
        {
            final int halfLength = buf.length >>> 1;
            do {System.arraycopy(buf,0,buf,length,length);}
            while((length <<= 1) < halfLength);
        }
        // Clone the remaining data.
        System.arraycopy(buf,0,buf,length,buf.length - length);
        return buf;
    }
    /**@return This sequence repeated <code>count</code> times.*/
    public Sequence repeat(final int count) {
        if(count < 1) return EMPTY;
        if(count < 2 || length() == 0) return this;
        final char[] buf = new char[count * length()];
        // Clone data from this sequence to the buffer.
        System.arraycopy(data,start,buf,0,length());
        return new Sequence(repeat(buf,count,length()));
    }
    
    private int idx(int i) {
        i += (i < 0? end : start);
        if(end < i || i < start)
            throw new IndexOutOfBoundsException(
                "Attempted to access index %d from sequence of length %d."
                .formatted(i,length())
            );
        return i;
    }
    @Override public int length() {return end - start;}
    /**
     * Gets the character at the specified index.
     * 
     * @param index A positive integer in the range
     *              <code>(-length(),length())</code>. Negative indices are
     *              equivalent to <code>length() + index</code>.
     *              
     * @throws IndexOutOfBoundsException <code>|index| >= length()</code>
     * 
     * @see java.lang.CharSequence#charAt(int)
     */
    @Override public char charAt(final int index) throws IndexOutOfBoundsException {return data[idx(index)];}
    
    /**A basic iterator which traverses a {@linkplain Sequence}.*/
    public abstract class SequenceIterator implements Iterator<Character> {
        protected int cursor;
        protected int mark;
        protected SequenceIterator(final int begin) {mark = cursor = begin;}
        /**
         * @return The value at the cursor without advancing, or <code>null</code> if
         *         {@linkplain SequenceIterator#next()} hasn't been called for the first
         *         time.
         */
        public abstract Character peek();
        /**
         * @return The value at the cursor's position offset by the argument without
         *         advancing, or <code>null</code> if
         *         {@linkplain SequenceIterator#next()} hasn't been called for the first
         *         time.
         */
        public abstract Character peek(final int offset);
        /**
         * @return The next non-whitespace character without advancing, or
         *         <code>null</code> if the end is reached.
         */
        public abstract Character peekNextNonWS();
        /**
         * @return The index of the character last returned by
         *         {@linkplain SequenceIterator#next()}, adjusted to the current
         *         sequence's range.
         */
        public int index() {return cursor - start;}
        /**@return The next non-whitespace character, or <code>null</code> if the end is reached.*/
        public abstract Character nextNonWS();
        
        protected abstract boolean rangeCheck();
        protected abstract void advanceFirst();
        /**@return The closest non-whitespace character, or <code>null</code> if the end is reached.*/
        public Character skipWS() {
            if(rangeCheck()) return null;
            advanceFirst();
            if(!Character.isWhitespace(data[cursor])) return data[cursor];
            return nextNonWS();
        }
        /**@return The sequence being iterated over.*/
        public Sequence getParent() {return Sequence.this;}
        /**@return Saves the current position.*/
        public void mark() {mark = cursor;}
        protected int offset(final int offset) {return Math.max(Math.min(cursor + offset,end),start);}
        /**@return Saves the current position offset by the argument.*/
        public void mark(final int offset) {mark = offset(offset);}
        /**@return The sub-sequence between the first marked position to the current index.*/
        public abstract Sequence subSequence();
        /**Sets the cursor to the specified position.*/
        public SequenceIterator jumpTo(final int index) {cursor = idx(index); return this;}
        /**Offsets the current position.*/
        public SequenceIterator jumpOffset(final int offset) {cursor += offset; return this;}
    }
    /**A {@linkplain SequenceIterator} which iterates from start to end.*/
    public class ForwardSequenceIterator extends SequenceIterator {
        private ForwardSequenceIterator() {super(start - 1);}
        @Override public boolean hasNext() {return cursor < end - 1;}
        @Override public Character next() {return ++cursor < end? data[cursor] : null;}
        @Override public Character peek() {return cursor < start || cursor >= end? null : data[cursor];}
        @Override public Character peek(final int offset) {return cursor < start? null : data[offset(offset)];}
        @Override protected boolean rangeCheck() {return cursor >= end;}
        @Override protected void advanceFirst() {if(cursor == start - 1) ++cursor;}
        @Override
        public Character peekNextNonWS() {
            int temp = cursor;
            while(++temp < end && Character.isWhitespace(data[temp]));
            return temp < start || temp >= end? null : data[temp];
        }
        @Override
        public Character nextNonWS() {
            while(++cursor < end && Character.isWhitespace(data[cursor]));
            return cursor < end? data[cursor] : null;
        }
        @Override public Sequence subSequence() {return new Sequence(mark,cursor,data);}
        
        @Override public String toString() {return new String(data,cursor,end-cursor);}
    }
    /**
     * Returns an iterator for this sequence.
     * 
     * @see java.lang.Iterable#iterator()
     * @see ForwardSequenceIterator
     */
    @Override public ForwardSequenceIterator iterator() {return new ForwardSequenceIterator();}
    /**A {@linkplain SequenceIterator} which iterates from end to start.*/
    public class ReverseSequenceIterator extends SequenceIterator {
        private ReverseSequenceIterator() {super(end);}
        @Override public boolean hasNext() {return cursor > start;}
        @Override public Character next() {return --cursor >= start? data[cursor] : null;}
        @Override public Character peek() {return cursor == end? null : data[cursor];}
        @Override public Character peek(final int offset) {return cursor == end? null : data[offset(offset)];}
        @Override protected boolean rangeCheck() {return cursor < start;}
        @Override protected void advanceFirst() {if(cursor == end) --cursor;}
        @Override
        public Character peekNextNonWS() {
            int temp = cursor;
            while(temp > start && Character.isWhitespace(data[--temp]));
            return temp == end || temp < start? null : data[temp];
        }
        @Override
        public Character nextNonWS() {
            while(cursor > start && Character.isWhitespace(data[--cursor]));
            return cursor == end || cursor < start? null : data[cursor];
        }
        @Override public Sequence subSequence() {return new Sequence(cursor,mark,data);}
        
        @Override
        public String toString() {return new String(data,start,cursor-start+1);}
    }
    /**
     * Returns a reversed iterator for this sequence.
     * 
     * @see Sequence#iterator()
     * @see ReverseSequenceIterator
     */
    public ReverseSequenceIterator reverseIterator() {return new ReverseSequenceIterator();}
    
    /**
     * @throws IndexOutOfBoundsException <code>|index| >= length()</code>
     * 
     * @see java.lang.CharSequence#subSequence(int, int)
     */
    @Override
    public Sequence subSequence(final int start,final int end) throws IndexOutOfBoundsException {
        return new Sequence(idx(start),idx(end),data);
    }
    /**
     * @param start Starting index, inclusive.
     * 
     * @return The sequence from the specified index to the end of the original
     *         sequence.
     * 
     * @throws IndexOutOfBoundsException <code>|index| >= length()</code>
     */
    public Sequence subSequence(final int start) throws IndexOutOfBoundsException {
        return new Sequence(idx(start),end,data);
    }
    
    /**@return <code>true</code> iff the first and last characters of this sequence match the input.*/
    public boolean isWrappedIn(final char c) {return end > start && data[start] == c && data[end - 1] == c;}
    /**@return This sequence, with the first and last characters removed.*/
    public Sequence unwrap() {return end > start? subSequence(1,-1) : this;}
    /**@return The hexadecimal character representation of the input's lower 4 bits.*/
    public static final char toHexChar(int c) {return (char)((c &= 0xF) > 9? c - 10 + 'A' : ('0' + c));}
    /**@return The integer representation of the hexadecimal input, or <code>-1</code> iff invalid.*/
    public static final int fromHexChar(final char c) {
        return '9' < c || c < '0'? ('F' < c || c < 'A'? 'f' < c || c < 'a'? -11
                                                                          : c - 'a'
                                                      : c - 'A') + 10
                                 : c - '0';
    }
    /**
     * @return The same string, but the escape characters are parsed.
     * 
     * @throws IllegalArgumentException If an invalid escape character at the end of
     *                                  the sequence.
     * @throws NullPointerException
     */
    public static Sequence unescape(final Sequence s)
                                    throws NullPointerException,
                                           IllegalArgumentException {
        if(s == null) throw new NullPointerException("Cannot un-escape null sequence.");
        final char[] buf = s.toChars();
        int i = -1;
        {
            boolean escaped = false;
            for(final char c : buf) {
                if(!escaped) {if(escaped = c == '\\') continue;}
                else escaped = false;
                buf[++i] = c;
            }
            if(escaped)
                throw new IllegalArgumentException(
                    ExceptionUtils.fmtParsing(
                        "Invalid escape character at end of sequence",
                        i,s
                    )
                );
        }
        return new Sequence(0,i + 1,buf);
    }
    /**@return The same sequence, but the escape characters are parsed.*/
    public Sequence unescape() {return unescape(this);}
    /**
     * @return The same sequence, but with the first and last characters removed and
     *         the escape characters parsed.
     */
    public Sequence unwrapAndUnescape() {return unwrap().unescape();}
    private static Sequence escapeEscapes(final Sequence s) {
        final Sequence[] split = s.basicSplit('\\');
        if(split.length == 1) return s;
        final Joiner j = new Joiner(new Sequence('\\',2));
        for(final Sequence sp : split) j.push(sp);
        return j.concat();
    }
    private static Sequence escape(Sequence s,final char escape) {
        final Sequence[] split = (s = escapeEscapes(s)).basicSplit(escape);
        if(split.length == 1) return s;
        final Joiner j = new Joiner(new Sequence(0,'\\',escape));
        for(final Sequence sp : split) j.push(sp);
        return j.concat();
    }
    /**@return The same sequence, but wrapped and with quotes escaped.*/
    public static Sequence quoteAndEscape(final Sequence s,final char quote)
                                          throws NullPointerException {
        if(s == null) throw new NullPointerException("Cannot escape a null sequence.");
        return Wrapper.wrap(escape(s,quote),quote);
    }
    /**@return The same sequence, but wrapped and with quotes escaped.*/
    public Sequence quoteAndEscape(final char quote) {return quoteAndEscape(this,quote);}
    
    /**
     * @param str    Character buffer to copy into.
     * @param offset Starting position in the buffer.
     * 
     * @return The sum of the offset and this sequence's length.
     * 
     * @throws NullPointerException      If <code>str</code> is <code>null</code>.
     * @throws IndexOutOfBoundsException If any of the indices are outside the input
     *                                   array.
     */
    public int copyInto(final char[] str,final int offset)
                        throws NullPointerException,
                               IndexOutOfBoundsException {
        final int l = length();
        System.arraycopy(
            data,start,
            str,offset,
            l
        );
        return offset + l;
    }
    
    /**@return A sequence containing the entire contents of the backing array.*/
    public Sequence getSharedSequence() {return new Sequence(data);}
    /**@return A copy of the characters referenced in the backing array.*/
    public char[] toChars() {
        final int l = length();
        final char[] o = new char[l];
        System.arraycopy(data,start,o,0,l);
        return o;
    }
    @Override public String toString() {return new String(data,start,length());}
    
    @Override
    public int compareTo(final Sequence o) {
        return java.util.Arrays.compare(
            data,start,end,
            o.data,o.start,o.end
        );
    }
    /**@see #equals(Object)*/
    public boolean equals(final Sequence o) {return compareTo(o) == 0;}
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj.getClass() == Sequence.class) && equals((Sequence)obj);
    }
    @Override public int hashCode() {return new String(data,start,end).hashCode();}
    
    /**
     * @return The largest sub-sequence starting from at least the second index
     *         where the first character is not whitespace.
     */
    public Sequence nextNonWS() {
        if(isEmpty()) return this;
        int a = start;
        while(++a < end && Character.isWhitespace(data[a]));
        return new Sequence(a,end,data);
    }
    /**@return The largest sub-sequence where the first character is not whitespace.*/
    public Sequence stripLeading() {
        if(isEmpty()) return this;
        int a = start - 1;
        while(++a < end && Character.isWhitespace(data[a]));
        return new Sequence(a,end,data);
    }
    /**@return The largest sub-sequence where the last character is not whitespace.*/
    public Sequence stripTailing() {
        if(isEmpty()) return this;
        int b = end;
        while(--b > start && Character.isWhitespace(data[b]));
        return new Sequence(start,b + 1,data);
    }
    /**@return The largest sub-sequence where the first and last characters are not whitespace.*/
    public Sequence strip() {
        if(isEmpty()) return this;
        int a = start - 1;
        while(++a < end && Character.isWhitespace(data[a]));
        int b = end;
        while(--b > a && Character.isWhitespace(data[b]));
        return new Sequence(a,b + 1,data);
    }
    
    /**
     * Splits this sequence into two sub-sequences.
     * 
     * @param split An index marking the end of the first sub-sequence.
     * 
     * @return An array of two {@linkplain Sequence} instances which share the same
     *         backing array as the original sequence.
     *         
     * @throws ArrayIndexOutOfBoundsException If any of the indices are outside the
     *                                        input array.
     */
    public Sequence[] split(final int split)
                            throws ArrayIndexOutOfBoundsException {
        final int idx = idx(split);
        return new Sequence[] {new Sequence(start,idx,data),new Sequence(idx,end,data)};
    }
    /**
     * Splits this sequence into sub-sequences.
     * 
     * @param split Indices marking the end of each sequence excepting the last, in
     *              ascending order.
     * 
     * @return An array of {@linkplain Sequence} instances which share the same
     *         backing array as the original sequence.
     *         
     * @throws IllegalArgumentException       If the indices are not in ascending
     *                                        order.
     * @throws ArrayIndexOutOfBoundsException If any of the indices are outside the
     *                                        input array.
     */
    public Sequence[] split(final int...split)
                            throws IllegalArgumentException,
                                   ArrayIndexOutOfBoundsException {
        if(split == null || split.length == 0) return new Sequence[] {new Sequence(data)};
        final Sequence[] out = new Sequence[split.length + 1];
        for(
            int i = 0,j = start;
            i < split.length;
            ++i
        ) {
            final int k = idx(split[i]);
            if(k < j) throw new IllegalArgumentException("Indices must be in ascending order.");
            out[i] = new Sequence(j,k,data);
            j = k;
        }
        out[split.length] = new Sequence(idx(split[split.length - 1]),end,data);
        return out;
    }
    /**
     * Splits this sequence into sub-sequences.
     * 
     * @param split Indices marking the end of each sequence excepting the last, in
     *              ascending order.
     * 
     * @return An array of {@linkplain Sequence} instances which share the same
     *         backing array as the original sequence.
     *         
     * @throws IllegalArgumentException       If the indices are not in ascending
     *                                        order.
     * @throws ArrayIndexOutOfBoundsException If any of the indices are outside the
     *                                        input array.
     *         
     */
    public Sequence[] split(final Queue<Integer> split)
                            throws IllegalArgumentException,
                                   ArrayIndexOutOfBoundsException {
        final int l;
        if(split == null || (l = split.size()) == 0) return new Sequence[] {new Sequence(data)};
        final Sequence[] out = new Sequence[l + 1];
        for(
            int i = 0,j = start;
            i < l;
            ++i
        ) {
            final int k = idx(split.pop());
            if(k < j) throw new IllegalArgumentException("Indices must be in ascending order.");
            out[i] = new Sequence(j,k,data);
            j = k;
        }
        out[l] = new Sequence(idx(split.pop()),end,data);
        return out;
    }
    /**
     * Splits this sequence into sub-sequences.
     * 
     * @param split Indices marking the end of each sequence excepting the last, in
     *              descending order.
     * 
     * @return An array of {@linkplain Sequence} instances which share the same
     *         backing array as the original sequence.
     *         
     * @throws IllegalArgumentException       If the indices are not in descending
     *                                        order.
     * @throws ArrayIndexOutOfBoundsException If any of the indices are outside the
     *                                        input array.
     */
    public Sequence[] split(final Stack<Integer> split)
                            throws IllegalArgumentException,
                                   ArrayIndexOutOfBoundsException {
        if(split == null || split.size() == 0) return new Sequence[] {new Sequence(data)};
        return splitBase(split);
    }
    /**
     * @return An array of sequences such that each is contiguous and their union
     *         does not contain any of the indices.
     */
    private Sequence[] splitBase(final Stack<Integer> indices) {
        // Split the string across the indices.
        final Sequence[] out = new Sequence[indices.size()];
        for(
            int i = indices.size(),k = end;
            !indices.empty();
        ) {
            final int j = indices.pop();
            if(j < k) throw new IllegalArgumentException("Indices must be in descending order.");
            out[--i] = new Sequence(j+1,k,data);
            k = j;
        }
        return out;
    }
    /**
     * @return The corresponding structure's closing character, or <code>null</code>
     *         if none match.
     */
    public static Character mapToClose(final char c) {
        return NBTString.isStringWrapper(c)
            ? c
            : switch(c) {
                case NBTObject.OPEN -> NBTObject.CLOSE;
                case NBTArray .OPEN -> NBTArray .CLOSE;
                default -> null;
            };
    }
    public static boolean isClose(final char c) {return c == NBTObject.CLOSE || c == NBTArray.CLOSE;}
    private static boolean allowUnwrappedOrSyntax(final char c) {
        return c == NBTTag.SEPARATOR || c == NBTPrimitiveArray.TOKEN_SEPARATOR ||
               c == ',' || NBTString.allowUnwrapped(c) || Character.isWhitespace(c);
    }
    public static boolean isJavaEscape(final char c) {
        return switch(c) {
            case '\\','/','b','f','n','r','t' -> true;
            default -> false;
        };
    }
    public static boolean isStringWrapper(final char c,final boolean singleQuotesEnabled) {
        return c == '"' || singleQuotesEnabled && c == '\'';
    }
    public static boolean allowUnicodeHex(final char c) {
        return ('0' <= c && c <= '9') ||
               ('A' <= c && c <= 'F') ||
               ('a' <= c && c <= 'f');
    }
    /**
     * Splits this sequence whenever a matching character is found on the top
     * nesting level. In other words, the sequence will not be split on matching
     * characters which are inside quotes (<code>'""'</code>), braces
     * (<code>'{}'</code>), and brackets (<code>'[]'</code>).
     * 
     * @param split Character to match. Cannot be any of the above structure
     *              characters.
     * 
     * @return An array of {@linkplain Sequence} instances which share the same
     *         backing array as the original sequence, or <code>null</code> if
     *         there are issues with parsing the structure.
     */
    public Sequence[] split(final char split,final boolean singleQuotesEnabled) {return split(split,singleQuotesEnabled,0);}
    /**
     * Splits this sequence whenever a matching character is found on the top
     * nesting level. In other words, the sequence will not be split on matching
     * characters which are inside quotes (<code>'""'</code>), braces
     * (<code>'{}'</code>), and brackets (<code>'[]'</code>).
     * 
     * @param split Character to match. Cannot be any of the above structure
     *              characters.
     * @param max   Maximum number of times to split.
     * 
     * @return An array of {@linkplain Sequence} instances which share the same
     *         backing array as the original sequence, or <code>null</code> if
     *         there are issues with parsing the structure.
     */
    public Sequence[] split(final char split,final boolean singleQuotesEnabled,int max) {
        // Ensure that the split character isn't a wrapper.
        if(isClose(split) || mapToClose(split) != null) return null;
        final Stack<Integer> indices = new Stack<>();
        indices.push(start - 1);
        {
            final Stack<Character> stk = new Stack<>();
            char top = 0;
            boolean escaped = false;
            int unicode = 0;
            for(int i = start;i < end;++i) {
                final char c = data[i];
                if(unicode != 0) { // Skip 4 hex chars.
                    if(!allowUnicodeHex(c)) return null;
                    --unicode;
                } else if(escaped) { // Skip escaped character.
                    // Check for unicode.
                    if(c == 'u') unicode = 4;
                    // Check allowed single-char escape sequences.
                    else if((isStringWrapper(top,singleQuotesEnabled) && c != top) && !isJavaEscape(c))
                        return null;
                    escaped = false;
                } else if(!(escaped = c == '\\')) {// Check for un-escaped char.
                    if(top == 0) { // Check for top-level split tokens.
                        // A split character on an empty stack is valid for a split.
                        if(c == split) {indices.push(i); if(--max == 0) break; continue;}
                    } else if(top == c) { // Check if valid closing found.
                        stk.pop();
                        top = stk.empty()? 0 : stk.top();
                        continue;
                    }
                    if(!isStringWrapper(top,singleQuotesEnabled)) { // Ignore anything else which appears in quotes
                        // Add opening characters to stack.
                        final Character mapped = mapToClose(c);
                        if(mapped != null) stk.push(top = mapped);
                        // Invalid unwrapped characters.
                        else if(!allowUnwrappedOrSyntax(c)) return null;
                    }
                }
            }
            // Check for extra unmatched characters.
            if(!stk.empty()) return null;
        }
        return splitBase(indices);
    }
    /**
     * Splits this sequence whenever a matching character is found.
     * 
     * @param split Character to match.
     * 
     * @return An array of {@linkplain Sequence} instances which share the same
     *         backing array as the original sequence.
     */
    public Sequence[] basicSplit(final char split) {
        final Stack<Integer> indices = new Stack<>();
        indices.push(start - 1);
        for(int i = start;i < end;++i)
            if(split == data[i]) indices.push(i);
        return splitBase(indices);
    }
    
    /**
     * @param data Initial character data.
     * 
     * @return An array of {@linkplain Sequence} instances which share the same
     *         backing array.
     */
    public static Sequence[] shared(final char[]...data) {
        if(data == null || data.length == 0) return new Sequence[] {EMPTY};
        final Sequence[] out = new Sequence[data.length];
        final char[] oDat;
        {
            // Get the total length of the array.
            int ts = 0;
            for(final char[] d : data) ts += d == null? 0 : d.length;
            oDat = new char[ts];
        }
        // Copy the data into the array, then instantiate the sequence object.
        for(int i = 0,j = 0;i < data.length;++i) {
            if(data[i] == null) out[i] = new Sequence(j,j,oDat);
            else {
                System.arraycopy(
                    data[i],0,
                    oDat,j,
                    data[i].length
                );
                out[i] = new Sequence(j,j += data[i].length,oDat);
            }
        }
        return out;
    }
    /**
     * @param data  Initial character data.
     * @param split Indices marking the end of each sequence excepting the last, in
     *              ascending order.
     * 
     * @return An array of {@linkplain Sequence} instances which share the same
     *         backing array.
     * 
     * @throws IllegalArgumentException       If the indices are not in ascending
     *                                        order.
     * @throws NullPointerException           If <code>data</code> is null.
     * @throws ArrayIndexOutOfBoundsException If any of the indices are outside the
     *                                        input array.
     */
    public static Sequence[] shared(final char[] data,final int...split)
                                    throws IllegalArgumentException,
                                           NullPointerException,
                                           ArrayIndexOutOfBoundsException {
        if(data == null) throw new NullPointerException("Data is null.");
        if(split == null || split.length == 0) return new Sequence[] {new Sequence(data)};
        final Sequence[] out = new Sequence[split.length + 1];
        for(
            int i = 0,j = 0;
            i < split.length;
            ++i
        ) {
            final int k = split[i];
            if(k < j) throw new IllegalArgumentException("Indices must be in ascending order.");
            out[i] = new Sequence(j,k,data);
            j = k;
        }
        out[split.length] = new Sequence(split[split.length - 1],data);
        return out;
    }
    /**Forces the input sequences to share a common buffer.*/
    public static Sequence[] shared(final Sequence...data) {
        if(data == null || data.length == 0) return null;
        if(data.length == 1) return data;
        final char[] buf = internalMerge(data);
        for(int i = 0,j = 0;i < data.length;++i)
            data[i] = new Sequence(j,j += data[i].length(),buf);
        return data;
    }
    
    /**
     * @param sequences
     * 
     * @return The contents of all the sequences merged into one array.
     */
    private static char[] internalMerge(final Sequence...sequences) {
        final char[] out;
        int ts = 0;
        for(final Sequence s : sequences) ts += s.length();
        out = new char[ts];
        ts = 0;
        for(final Sequence s : sequences) ts = s.copyInto(out,ts);
        return out;
    }
    /**
     * @param sequences
     * 
     * @return The contents of all the sequences merged into one sequence.
     */
    public static Sequence merge(final Sequence...sequences) {return new Sequence(internalMerge(sequences));}
    /**
     * @param sequences
     * 
     * @return The contents of all the sequences merged into one array.
     */
    public static Sequence merge(final Iterable<Sequence> sequences) {
        final char[] out;
        int ts = 0;
        for(final Sequence s : sequences) ts += s.length();
        out = new char[ts];
        ts = 0;
        for(final Sequence s : sequences) ts = s.copyInto(out,ts);
        return new Sequence(out);
    }
}
/*
// Unescapes and parses unicode
public static Sequence unescape(final Sequence s) throws IllegalArgumentException {
    final char[] buf = s.toChars();
    int i = -1;
    {
        boolean escaped = false;
        int unicode = 0,u = 0;
        for(int j = 0;j < buf.length;++j) {
            char c = buf[j];
            if(!escaped) {
                if(unicode != 0) {
                    final int hex = fromHexChar(c);
                    if(hex == -1)
                        throw new IllegalArgumentException(
                            ExceptionUtils.fmtParsing(
                                "Invalid hexadecimal character '%c' ('\\u%04X')"
                                .formatted(c,(int)c),j,s
                            )
                        );
                    u = u << 4 | hex;
                    if(--unicode != 0) continue;
                    c = (char)u;
                } else if(escaped = c == '\\') continue;
            } else {
                escaped = false;
                if(c == 'u') {unicode = 4; u = 0; continue;}
            }
            buf[++i] = c;
        }
    }
    return new Sequence(0,i + 1,buf);
}
*/