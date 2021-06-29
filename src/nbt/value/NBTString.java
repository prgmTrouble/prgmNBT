package nbt.value;

import static util.string.Sequence.EMPTY;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import nbt.exception.NBTParsingException;
import nbt.value.number.NBTNumber;
import settings.Version;
import util.container.NodeIterator;
import util.container.Stack;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;

/**
 * An {@linkplain NBTValue} holding a string.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class NBTString extends NBTValue implements Comparable<NBTString> {
    public static final ValueType TYPE = ValueType.STRING;
    @Override public ValueType type() {return TYPE;}
    
    /* ========================= Inactive Flags ========================= */
    // These flags are simply for future-proofing. The features they enable
    // are not currently in the game.
    
    /**Enables parsing unicode escape sequences.*/
    public static final boolean UNICODE_ENABLED = Version.atLeast(Version.unknown);
    /**
     * Enables replacing non-ASCII characters with unicode. Requires
     * {@linkplain #UNICODE_ENABLED}
     */
    public static final boolean ESCAPE_UNICODE = Version.atLeast(Version.unknown);
    /**Allows escape codes used in the Java specification.*/
    public static final boolean ALLOW_JAVA_ESCAPES = Version.atLeast(Version.unknown);
    /* ================================================================== */
    
    /**
     * Allows strings to be wrapped in single quotes.
     * 
     * @see Version#v19w08a
     */
    public static final boolean SINGLE_QUOTES_ENABLED = Version.atLeast(Version.v19w08a);
    /**@return <code>true</code> iff the character matches a valid string wrapper.*/
    public static boolean isStringWrapper(final char c) {return Sequence.isStringWrapper(c,SINGLE_QUOTES_ENABLED);}
    
    /**@return <code>true</code> iff the character whitespace or not an ISO control character.*/
    public static final boolean isPrintable(final char c) {
        return Character.isWhitespace(c) || !Character.isISOControl(c);
    }
    
    /**
     * Causes the parser to consider any sequence of characters ending at a
     * top-level comma or closing character as an unwrapped sequence, in addition to
     * the normal quoted strings.
     */
    public static final boolean THE_WILD_WEST = Version.isBefore(Version.v17w16a);
    
    public static final Sequence GLOBAL_DEFAULT = Sequence.EMPTY;
    public static final char DEFAULT_QUOTE_STYLE = '"';
    protected Sequence value,localDefault = GLOBAL_DEFAULT;
    protected char quoteStyle = DEFAULT_QUOTE_STYLE;
    
    /**
     * Creates an {@linkplain #GLOBAL_DEFAULT} string value with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTString() {super(); value = GLOBAL_DEFAULT;}
    /**
     * Creates an {@linkplain #GLOBAL_DEFAULT} string value.
     * 
     * @param minimal {@linkplain NBTValue#minimal}
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTString(final boolean minimal) {super(minimal); value = GLOBAL_DEFAULT;}
    /**
     * Creates a string value with default minimalism.
     * 
     * @param value Initial value.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTString(final char[] value) throws NBTParsingException {super(); setValue(value);}
    /**
     * Creates a string value.
     * 
     * @param value   Initial value.
     * @param minimal {@linkplain NBTValue#minimal}
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTString(final char[] value,final boolean minimal) throws NBTParsingException {super(minimal); setValue(value);}
    /**
     * Creates a string value with default minimalism.
     * 
     * @param value Initial value.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTString(final Sequence value) throws NBTParsingException {super(); setValue(value);}
    /**
     * Creates a string value.
     * 
     * @param value   Initial value.
     * @param minimal {@linkplain NBTValue#minimal}
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTString(final Sequence value,final boolean minimal) throws NBTParsingException {super(minimal); setValue(value);}
    /**
     * Creates a string value with default minimalism.
     * 
     * @param value Initial value.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTString(final String value) throws NBTParsingException {super(); setValue(value);}
    /**
     * Creates a string value.
     * 
     * @param value   Initial value.
     * @param minimal {@linkplain NBTValue#minimal}
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTString(final String value,final boolean minimal) throws NBTParsingException {super(minimal); setValue(value);}
    /**Reads a string value.*/
    public NBTString(final DataInput in) throws IOException {value = new Sequence(in.readUTF());}
    /**Writes this string value.*/
    @Override
    public void write(final DataOutput out) throws IOException {out.writeUTF(value.toString());}
    /**
     * Parses a string value. The minimalism is set to <code>true</code> if the
     * string is wrapped.
     */
    public NBTString(final SequenceIterator i) throws NBTParsingException {
        value = eatSequence(i,null);
        // Determine if string is wrapped.
        if(!(minimal = !(value.isWrappedIn('"') || SINGLE_QUOTES_ENABLED && value.isWrappedIn('\''))))
            value = value.unwrapAndUnescape();
    }
    
    /**
     * @param single <code>true</code> iff the quote style should be single
     *               (<code>'</code>,<code>\u0027</code>) or double
     *               (<code>"</code>,<code>\u0022</code>) quotes.
     *               
     * @return <code>this</code>
     */
    public NBTString setQuoteStyle(final boolean single) {
        if(SINGLE_QUOTES_ENABLED && single) quoteStyle = '\'';
        return this;
    }
    
    private static <V> V checkNN(final V value) throws NBTParsingException {
        if(value == null) throw new NBTParsingException("Cannot assign a null value.");
        return value;
    }
    
    private static Sequence eatWildWestSequence(final SequenceIterator i,
                                                final char wrapper,
                                                final Character terminator)
                                                throws NBTParsingException {
        final int start; int end;
        final Stack<Integer> escapes = new Stack<>();
        
        if(isStringWrapper(wrapper)) {
            // Start and end will point to the first and last quotes.
            start = i.index() + 1;
            end = -1;
            while(i.hasNext()) {
                final char c = i.next();
                if(c == '\\') {
                    if(!i.hasNext()) break;
                    final char nc = i.next();
                    if(nc == '\\' || nc == wrapper)
                        escapes.push(i.index());
                } else if(c == wrapper) {
                    end = i.index();
                    i.nextNonWS();
                    break;
                }
            }
            // No end quote was found
            if(end == -1) throw new NBTParsingException("Missing ending quote",i);
        } else {
            // Check for early terminators.
            if(Sequence.isClose(wrapper) || wrapper == ',') return i.subSequence();
            
            final Stack<Character> nesting = new Stack<>();
            Character top = Sequence.mapToClose(wrapper);
            
            start = i.index();
            end = start;
            
            while(i.hasNext()) {
                final Character c = i.nextNonWS();
                if(c == null) break;
                // Record the position after the last known non-whitespace.
                // character.
                end = i.index();
                if(c == '\\') {
                    if(!i.hasNext()) break;
                    final char nc = i.next();
                    if(Character.isWhitespace(nc)) continue;
                    ++end; // Another non-whitespace character is guaranteed.
                    // Check for closing quote.
                    if(isStringWrapper(nc)) {
                        // Check for invalid escape of quote.
                        if(top == null || top != nc)
                            throw new NBTParsingException("Unwrapped escaped quote",i);
                    } else if(nc != '\\') continue; // Escape doesn't get eaten.
                    // Record position of escaped character.
                    escapes.push(i.index());
                }
                // Check for matching structure character.
                else if(c == top) top = nesting.empty()? null : nesting.pop(); // Pop structure from stack.
                // Check characters outside of quotes.
                else if(top == null || !isStringWrapper(top)) {
                    if(top == null && (c == ',' || c == terminator)) break;
                    // Closing characters would have already have been detected by
                    // the '== top' condition. This indicates an imbalance.
                    if(Sequence.isClose(c))
                        throw new NBTParsingException(
                            "Unmatched closing character '%c'"
                            .formatted(c),i
                        );
                    // If the character is an open character, then the mapToClose()
                    // function will return a non-null value.
                    final Character nc = Sequence.mapToClose(c);
                    if(nc != null) {
                        // Push top to the stack, if not null.
                        if(top != null) nesting.push(top);
                        // Set top to the new structure character.
                        top = nc;
                    }
                }
            }
            // If top is not null, then there is an unmatched structure character.
            if(top != null)
                throw new NBTParsingException(
                    "End of sequence reached before closing character '%c' found"
                    .formatted(top),
                    end,i.getParent()
                );
        }
        final Sequence parent = i.getParent();
        // Skip post-processing if there were no eaten escape characters.
        if(escapes.empty()) return parent.subSequence(start,end);
        
        // The output size is the number of characters between the first
        // and last characters excepting the eaten escape characters.
        final char[] buf = new char[end - start - escapes.size()];
        int cursor = buf.length; // Current index in the output.
        for(final int nxt : escapes) {
            // Copy the region between the two eaten escapes.
            parent.subSequence(nxt,end).copyInto(buf,cursor -= end - nxt);
            end = nxt - 1;
        }
        // Copy the remaining characters.
        parent.subSequence(start,end).copyInto(buf,0);
        return new Sequence(buf);
    }
    private static Sequence eatSequence(final SequenceIterator i,
                                        final Character terminator)
                                        throws NBTParsingException {
        final Character wrapper = i.skipWS();
        if(wrapper == null) {
            // Before 17w16a, empty sequences are allowed to be strings.
            if(THE_WILD_WEST) return new Sequence();
            throw new NBTParsingException("Empty sequence",i);
        }
        i.mark();
        if(THE_WILD_WEST) return eatWildWestSequence(i,wrapper,terminator);
        if(isStringWrapper(wrapper)) {
            if(!i.hasNext()) // Ensure that there are more characters.
                throw new NBTParsingException("Missing ending quote",i);
            final Stack<Integer> chars,indices;
            if(UNICODE_ENABLED && ESCAPE_UNICODE) {chars = new Stack<>(); indices = new Stack<>();}
            else chars = indices = null;
            {
                boolean escaped = false;
                int unicode = 0;
                char c = i.next();
                while(i.hasNext()) {
                    if(UNICODE_ENABLED) {
                        if(unicode != 0) { // Skip 4 hex characters.
                            if(!Sequence.allowUnicodeHex(c))
                                throw new NBTParsingException(
                                    "Invalid hexadecimal character '%c' ('\\u%04X')"
                                    .formatted(c,(int)c),i
                                );
                            --unicode;
                            continue;
                        }
                        // Check for unicode.
                        if(escaped && c == 'u') unicode = 4;
                    }
                    if(escaped) { // Skip escaped character.
                        // Check allowed single-character escape sequences.
                        if(
                            unicode != 4 && !(
                                c == wrapper ||
                                c == '\\' ||
                                ALLOW_JAVA_ESCAPES && Sequence.isJavaEscape(c)
                            )
                        ) throw new NBTParsingException(
                            "Invalid escape character '%c' ('\\u%04d')"
                            .formatted(c,(int)c),i
                        );
                        escaped = false;
                    } else if(!isPrintable(c) || c == '\u00A7' || c == '\u007F')
                        // For whatever reason, all printable characters EXCEPT the section symbol
                        // are allowed. Presumably, this is because the section symbol is used as
                        // a formatting flag for the text renderer. However, I have no idea why its
                        // use is forbidden.
                        throw new NBTParsingException(
                            "Invalid character '\\u%04X'"
                            .formatted((int)c),i
                        );
                    else if(UNICODE_ENABLED && ESCAPE_UNICODE && c > '\u007F') { // Check for non-ASCII.
                        chars.push((int)c);
                        indices.push(i.index());
                    } else if(!(escaped = c == '\\') && c == wrapper) // Check for un-escaped quote.
                        break;
                    // Ignore everything else.
                    c = i.next(); // Should never return null because of while condition.
                }
            }
            // Ensure there's a terminating quote.
            if(i.peek() != wrapper) throw new NBTParsingException("Missing ending quote",i);
            // Advance past the quote.
            i.next();
            final Sequence value = i.subSequence();
            // Return immediately if none of the characters were translated into unicode
            // escape sequences.
            if(!(UNICODE_ENABLED && ESCAPE_UNICODE) || chars.size() == 0) return value;
            
            // Buffer size is the number of characters in the original sequence, plus 5
            // times the number of added unicode sequences. This accounts for one
            // character removed, and 6 added in the "\\u####" expression.
            final char[] buf = new char[value.length() + chars.size() * 5];
            int cursor = buf.length,prev = value.length();
            for(
                final NodeIterator<Integer> j = indices.iterator(),
                                            c = chars.iterator();
                j.hasNext();
            ) {
                final int p = j.next();
                // Copy the next contiguous section of ASCII characters.
                value.subSequence(p + 1,prev).copyInto(buf,cursor -= prev - p - 1);
                {
                    // Convert the character into hexadecimal.
                    int v = c.next();
                    buf[--cursor] = Sequence.toHexChar(v);
                    buf[--cursor] = Sequence.toHexChar(v >>>= 4);
                    buf[--cursor] = Sequence.toHexChar(v >>>= 4);
                    buf[--cursor] = Sequence.toHexChar(v >>>  4);
                }
                // Add the prefix.
                buf[--cursor] = 'u';
                buf[--cursor] = '\\';
                // Update the last index.
                prev = p;
            }
            // Copy the remaining characters from the beginning.
            value.subSequence(0,prev).copyInto(buf,0);
            return new Sequence(buf);
        }
        // Check unwrapped string for invalid characters.
        if(allowUnwrapped(wrapper))
            while(i.hasNext() && allowUnwrapped(i.next()));
        return i.subSequence();
    }
    /**Ensures that the input sequence is valid and unwraps if necessary.*/
    private static Sequence validate(Sequence value) throws NBTParsingException {
        final SequenceIterator i = value.iterator();
        value = eatSequence(i,null);
        if(i.hasNext()) throw new NBTParsingException("Trailing data found",i);
        return value.isWrappedIn('"') || SINGLE_QUOTES_ENABLED && value.isWrappedIn('\'')
            ? value.unwrapAndUnescape()
            : value;
    }
    
    /**
     * @param value This string's value.
     * 
     * @return <code>this</code>
     * 
     * @throws NBTParsingException The input does not represent a valid SNBT string.
     */
    public NBTString setValue(final Sequence value) throws NBTParsingException {
        this.value = validate(checkNN(value));
        return this;
    }
    /**
     * @param value This string's value.
     * 
     * @return <code>this</code>
     * 
     * @throws NBTParsingException The input does not represent a valid SNBT string.
     */
    public NBTString setValue(final char...value) throws NBTParsingException {
        this.value = checkNN(value).length == 0? EMPTY : validate(new Sequence(value));
        return this;
    }
    /**
     * @param value This string's value.
     * 
     * @return <code>this</code>
     * 
     * @throws NBTParsingException The input does not represent a valid SNBT string.
     */
    public NBTString setValue(final String value) throws NBTParsingException {
        this.value = checkNN(value).isEmpty()? EMPTY : validate(new Sequence(value));
        return this;
    }
    
    /**
     * @param value This string's default value.
     * 
     * @return <code>this</code>
     * 
     * @throws NBTParsingException The input does not represent a valid SNBT string.
     */
    public NBTString setDefault(final Sequence value) throws NBTParsingException {
        localDefault = validate(checkNN(value));
        return this;
    }
    /**
     * @param value This string's default value.
     * 
     * @return <code>this</code>
     * 
     * @throws NBTParsingException The input does not represent a valid SNBT string.
     */
    public NBTString setDefault(final char...value) throws NBTParsingException {
        localDefault = checkNN(value).length == 0? EMPTY : validate(new Sequence(value));
        return this;
    }
    /**
     * @param value This string's default value.
     * 
     * @return <code>this</code>
     * 
     * @throws NBTParsingException The input does not represent a valid SNBT string.
     */
    public NBTString setDefault(final String value) throws NBTParsingException {
        localDefault = checkNN(value).isEmpty()? EMPTY : validate(new Sequence(value));
        return this;
    }
    @Override public boolean isDefault() {return value.equals(localDefault);}
    
    @Override protected Sequence complete() {return value.quoteAndEscape(quoteStyle);}
    @Override protected Sequence minimal() {return value;}
    /**@return The unwrapped value.*/
    public Sequence unwrapped() {return value;}
    
    /**@return <code>true</code> iff the character can exist in an unwrapped string.*/
    public static boolean allowUnwrapped(final char c) {
        return ('0' <= c && c <= '9') ||
               ('a' <= c && c <= 'z') ||
               ('A' <= c && c <= 'Z') ||
               switch(c) {
                   case '_','-','.','+' -> true;
                   default -> false;
               };
    }
    /**@return <code>true</code> iff the value necessarily must be wrapped.*/
    public static boolean forceWrap(final Sequence str) {
        if(str == null) return false;
        if(
            !THE_WILD_WEST && str.isEmpty() ||
            Character.isWhitespace(str.charAt(0)) ||
            Character.isWhitespace(str.charAt(-1)) ||
            str.equals(NBTBool.TRUE_SEQUENCE) ||
            str.equals(NBTBool.FALSE_SEQUENCE)
        ) return true;
        if(THE_WILD_WEST) {
            final Stack<Character> nesting = new Stack<>();
            Character top = null;
            for(
                final SequenceIterator i = str.iterator();
                i.hasNext();
            ) {
                final Character c = i.next();
                if(c == '\\') {
                    if(!i.hasNext()) break;
                    final char nc = i.next();
                    if(Character.isWhitespace(nc)) continue;
                    // Check for closing quote.
                    if(isStringWrapper(nc)) {
                        // Check for invalid escape of quote.
                        if(top == null || top != nc)
                            return true;
                    } else if(nc != '\\') continue; // Escape doesn't get eaten.
                }
                // Check for matching structure character.
                else if(c == top) top = nesting.empty()? null : nesting.pop(); // Pop structure from stack.
                // Check characters outside of quotes.
                else if(top == null || !isStringWrapper(top)) {
                    // Closing characters would have already have been detected by
                    // the '== top' condition. This indicates an imbalance.
                    if(Sequence.isClose(c)) return true;
                    // If the character is an open character, then the mapToClose()
                    // function will return a non-null value.
                    final Character nc = Sequence.mapToClose(c);
                    if(nc != null) {
                        // Push top to the stack, if not null.
                        if(top != null) nesting.push(top);
                        // Set top to the new structure character.
                        top = nc;
                    }
                }
            }
            return top != null;
        } else for(final char c : str) if(!allowUnwrapped(c)) return true;
        return false;
    }
    
    @Override
    public Sequence toSequence() {
        // Must wrap if contains an un-wrappable character.
        return !minimal || forceWrap(value)? complete() : minimal();
    }
    
    @Override public int compareTo(final NBTString o) {return value.compareTo(o.value);}
    /**@see NBTString#equals(Object)*/
    public boolean equals(final Sequence s) {return value.equals(s);}
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (
            !(obj instanceof NBTString)
                ? !(obj instanceof Sequence)
                    ? false
                    : equals((Sequence)obj)
                : equals(((NBTString)obj).value)
        );
    }
    @Override public int hashCode() {return value.hashCode();}
    
    /**
     * @param i          A {@linkplain SequenceIterator} which points to the
     *                   position just before the string sequence.
     * @param terminator A character which marks the end of a structure.
     *                   <code>null</code> indicates the end of the sequence.
     * @param commas     <code>true</code> iff commas are allowed to terminate a
     *                   value.
     * 
     * @return The appropriate {@linkplain NBTNumber}.
     * 
     * @throws NBTParsingException The iterator cannot find a valid number.
     */
    public static NBTString parse(final SequenceIterator i,
                                  final Character terminator,
                                  final boolean commas) 
                                  throws NBTParsingException {
        final Sequence str = eatSequence(i,terminator);
        final Character c = i.skipWS();
        if(!(commas && c == ',') && c != terminator)
            throw new NBTParsingException("string",i,terminator,commas,c);
        // Bypass constructor checks.
        final NBTString s = new NBTString();
        s.value = str.isWrappedIn('"') || SINGLE_QUOTES_ENABLED && str.isWrappedIn('\'')
                ? str.unwrapAndUnescape()
                : str;
        return s;
    }
}